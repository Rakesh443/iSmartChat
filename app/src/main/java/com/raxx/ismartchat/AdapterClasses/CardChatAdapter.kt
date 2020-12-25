package com.raxx.ismartchat.AdapterClasses

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

class CardChatAdapter(mContext: Context, mUsers:List<User>, isChatCheck:Boolean) : RecyclerView.Adapter<CardChatAdapter.CardViewHolder?>(){


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
        val view:View = LayoutInflater.from(mContext).inflate(R.layout.chat_card_list,parent,false)
        return CardChatAdapter.CardViewHolder(view)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val user:User?=mUser[position]


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
            retrieveLastMsg(user.getUid(),holder.lastMessageTxt)
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
            intent.putExtra("visit_id",user.getUid())
            mContext.startActivity(intent)
        }

    }

    private fun retrieveLastMsg(onlineuserid: String?, lastMessageTxt: TextView) {

//        lastMsg = "No Messages"
//        val firebaseUser = FirebaseAuth.getInstance().currentUser
//        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
//        ref.keepSynced(true)
//        ref.addValueEventListener(object :ValueEventListener{
//            override fun onDataChange(p0: DataSnapshot) {
//                for (snapshot in p0.children){
//                    val chat = snapshot.getValue(Chat::class.java)
//                    if(firebaseUser!=null && chat!=null){
//                        if(chat.getReceiver()==firebaseUser!!.uid && chat.getSender()==onlineuserid
//                            || chat.getReceiver()==onlineuserid && chat.getSender() == firebaseUser!!.uid){
//                                lastMsg = chat.getMessage()!!
//                        }
//                    }
//                }
//
//
//                lastMessageTxt.text = lastMsg
//
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//
//            }
//
//        })
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

    }


}