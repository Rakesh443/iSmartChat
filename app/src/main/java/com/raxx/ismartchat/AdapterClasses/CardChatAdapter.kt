package com.raxx.ismartchat.AdapterClasses

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raxx.ismartchat.MessageActivity
import com.raxx.ismartchat.Models.Chat
import com.raxx.ismartchat.Models.User
import com.raxx.ismartchat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CardChatAdapter(mContext: Context, mUsers: List<User>, isChatCheck: Boolean) : RecyclerView.Adapter<CardChatAdapter.CardViewHolder?>(){


    private val mContext:Context
    private val mUser:List<User>
    private var isChatCheck:Boolean
    private var lastMsg:String =""

    init {
        this.isChatCheck = isChatCheck
        this.mContext = mContext
        this.mUser = mUsers
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val view:View = LayoutInflater.from(mContext).inflate(
            R.layout.chat_card_list,
            parent,
            false
        )
        return CardViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val user:User?=mUser[position]


//        val current = LocalDateTime.now()
//
//        val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
//        val formatted = current.format(formatter)
//
//
//        val tymHr = formatted.substring(8,10)
//        val tymMn = formatted.substring(10,12)
//        holder.card_lastmessage_time.text= tymHr + ":" + tymMn
        holder.userNameTxt.text = user!!.getUserName()
//        holder.lastMessageTxt.text = "Last Message"
        Picasso.get().load(user?.getProfile()).into(holder.profileImageView)

//        if(isChatCheck){
//
//        }
//        else{
//            holder.lastMessageTxt.visibility = View.GONE
//        }
        if(isChatCheck){
            retrieveLastMsg(user.getUid(), holder.lastMessageTxt, holder.card_lastmessage_time)
            if(user.getStatus()=="online"){
                holder.onlineImageView.visibility=View.VISIBLE
                holder.offlineImageView.visibility = View.GONE
            }
            else{
                holder.onlineImageView.visibility=View.GONE
                holder.offlineImageView.visibility = View.VISIBLE
            }
        }
        else{
            holder.onlineImageView.visibility=View.GONE
            holder.offlineImageView.visibility = View.GONE
            holder.lastMessageTxt.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(mContext, MessageActivity::class.java)
            intent.putExtra("visit_id", user.getUid())
            mContext.startActivity(intent)
        }

//        holder.itemView!!.setOnLongClickListener(){
//            chatListOptions(position, holder)
//            return@setOnLongClickListener true
//        }
//
    }

//    private fun chatListOptions(position: Int, holder: CardChatAdapter.CardViewHolder) {
//
//
//            val options = arrayOf<CharSequence>("Delete")
//            var builder = AlertDialog.Builder(holder.itemView.context)
//            builder.setTitle("Options")
//            builder.setItems(options, DialogInterface.OnClickListener { dialog, i ->
//                if (i == 0) {
//                    deleteChatList(position, holder)
//                }
//
//            })
//            builder.show()
//
//    }

//    private fun deleteChatList(position: Int, holder: CardChatAdapter.CardViewHolder) {
//        val ref = FirebaseDatabase.getInstance().reference.child("ChatList")
//
//
//        ref.child(mUser[position].getUid()!!)
//            .removeValue()
//            .addOnCompleteListener{ task ->
//                if(task.isSuccessful){
//                    Toast.makeText(holder.itemView.context, "Message Deleted", Toast.LENGTH_SHORT).show()
//                }
//                else{
//                    Toast.makeText(
//                        holder.itemView.context,
//                        "Error in Message Deleted",
//                        Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//            }
//        removeItem(position,holder)
//
//    }
//    private fun removeItem(position: Int, holder: CardViewHolder) {
//        holder.remove(position)
//        notifyItemRemoved(position)
//        notifyItemRangeChanged(position, holder.size())
//    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun retrieveLastMsg(
        onlineuserid: String?,
        lastMessageTxt: TextView,
        cardLastmessageTime: TextView
    ) {

        lastMsg = "No Messages"
        var strtym ="ad"
        var time=""
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formatted = current.format(formatter)
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
        ref.keepSynced(true)
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver() == firebaseUser!!.uid && chat.getSender() == onlineuserid
                            || chat.getReceiver() == onlineuserid && chat.getSender() == firebaseUser!!.uid
                        ) {
                            var msg = chat.getMessage()


                            strtym = chat.getTymdate()!!
                            if(formatted.toInt()-strtym.substring(0,8).toInt()>1 && formatted.toInt()-strtym.substring(0,8).toInt()<2){
                                time = strtym.substring(6,8)+"/"+strtym.substring(4,6)+"/"+strtym.substring(0,4)
                            }
                            else if(formatted.toInt()-strtym.substring(0,8).toInt()==1){
                                time = "Yesterday"
                            }
                            else{
                                var tymHr = strtym.substring(8,10)
                                var tymMn = strtym.substring(10,12)
                                var ampm:String = "am"
                                if(tymHr.toInt()>12){
                                    tymHr= (tymHr.toInt()-12).toString()
                                    ampm="pm"
                                }
                                time= "$tymHr : $tymMn $ampm"

                            }

                            if (msg!!.length >= 35) {
                                msg = msg!!.substring(0, 33)
                            }
                            if (chat.getReceiver() == firebaseUser!!.uid) {


                                lastMsg = msg

                            } else {
                                if (chat.getIsseen())
                                    lastMsg = "seen: " + msg!!
                                else
                                    lastMsg = "sent: " + msg!!
                            }


                        }
                    }
                }


                cardLastmessageTime.text = time
                lastMessageTxt.text = lastMsg

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    override fun getItemCount(): Int {
        return  mUser.size
    }

    class CardViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView) {
        var userNameTxt: TextView = itemView.findViewById(R.id.card_username)
        var profileImageView: CircleImageView = itemView.findViewById(R.id.card_userImage)
        var onlineImageView: CircleImageView = itemView.findViewById(R.id.card_image_online)
        var offlineImageView: CircleImageView = itemView.findViewById(R.id.card_image_offline)
        var lastMessageTxt: TextView = itemView.findViewById(R.id.card_lastmessage)
        var card_lastmessage_time:TextView = itemView.findViewById(R.id.card_lastmessage_time)

    }


}