package com.raxx.ismartchat.AdapterClasses

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.content.DialogInterface
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.raxx.ismartchat.Models.Chat
import com.raxx.ismartchat.R
import com.squareup.picasso.Picasso
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class ChatsAdapter(
    mContext: Context,
    mChatList: List<Chat>,
    imageUrl: String
) :RecyclerView.Adapter<ChatsAdapter.ViewHolder?>(){


    private val mContext:Context
    private val mChatList:List<Chat>
    private val imageUrl:String
    var globalTym=""
    var globalTym2=""

    var firebaseUser:FirebaseUser?=FirebaseAuth.getInstance().currentUser!!


    init {

        this.mContext=mContext
        this.mChatList=mChatList
        this.imageUrl=imageUrl
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {

        return  if (position ==1){
            val view:View = LayoutInflater.from(mContext).inflate(
                R.layout.message_item_right,
                parent,
                false
            )
            ViewHolder(view)
        }
        else{
            val view:View = LayoutInflater.from(mContext).inflate(
                R.layout.message_item_left,
                parent,
                false
            )
            ViewHolder(view)
        }

    }

    override fun getItemCount(): Int {
        return mChatList.size
    }







    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)

        var strtym ="ad"
        var time=""
        var time2=""
        val current = LocalDateTime.now()

        val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val formatted = current.format(formatter)

        strtym = chat.getTymdate()!!

        var tymHr = strtym.substring(8,10)
        var tymMn = strtym.substring(10,12)
        var ampm:String = "am"
        if(tymHr.toInt()>12){
            tymHr= (tymHr.toInt()-12).toString()
            ampm="pm"
        }
        time2= "$tymHr:$tymMn $ampm"

        if(formatted.toInt()-strtym.substring(0,8).toInt()==1){
            time = "Yesterday"
        }
        else if(!(formatted.toInt()-strtym.substring(0,8).toInt()<=2)){
            time = strtym.substring(6,8)+"/"+strtym.substring(4,6)+"/"+strtym.substring(0,4)

            time+= " $time2"
        }
        else{
            time = time2

        }


        if (chat.getMessage().equals("sent") && !chat.getUrl().equals("")){
            // image message- right side
            if(chat.getSender().equals(firebaseUser!!.uid)){

                holder.show_text_message!!.visibility = View.GONE
                holder.right_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.right_image_view)



            }//image message- left side
            else if(!chat.getSender().equals(firebaseUser!!.uid)){
                holder.show_text_message!!.visibility = View.GONE
                holder.left_image_view!!.visibility = View.VISIBLE
                Picasso.get().load(chat.getUrl()).into(holder.left_image_view)

            }
        }
        //for Text Messages
        else{
            globalTym= chat.getTymdate()!!.substring(0,8).substring(6,8)+"/"+strtym.substring(4,6)+"/"+strtym.substring(0,4)
            holder.msg_chat_date!!.text =globalTym
            holder.show_text_message!!.text = "${chat.getMessage()} \n \t $time2"
            holder.show_text_message!!.setOnLongClickListener(){
                msgOptions(position,holder)
                return@setOnLongClickListener true
            }

        }

        //sent and seen message
        if(position==mChatList.size-1) {
            if (chat.getIsseen()) {
                holder.text_seen!!.text = "Seen"

                if (chat.getMessage().equals("sent") && !chat.getUrl().equals("")) {
                    val rl = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    rl!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = rl
                }

            }

            else{
                holder.text_seen!!.text = "Sent"

                if (chat.getMessage().equals("sent") && !chat.getUrl().equals("")){
                    val rl = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    rl!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams =  rl
                }

            }


        }
        else{
            holder.text_seen!!.visibility = View.GONE
        }




    }



    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var msg_chat_tym_layout: LinearLayout?=null
        var show_text_message:TextView?=null
        var profile_image:ImageView?=null
        var left_image_view:ImageView?=null
        var text_seen:TextView?=null
        var right_image_view:ImageView?=null

        var msg_chat_date:TextView?=null


        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)
            msg_chat_date = itemView.findViewById(R.id.msg_chat_date)
            msg_chat_tym_layout = itemView.findViewById(R.id.msg_chat_tym_layout)


        }
    }



    override fun getItemViewType(position: Int): Int {
        return if(mChatList[position].getSender().equals(firebaseUser!!.uid)){
            1
        }
        else{
            0
        }
    }

    private fun deleteMsg(position: Int,holder: ViewHolder){

        val ref = FirebaseDatabase.getInstance().reference.child("Chats")
            .child(mChatList.get(position).getMessageId()!!)
            .removeValue()
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    Toast.makeText(holder.itemView.context, "Message Deleted", Toast.LENGTH_SHORT).show()
                }
                else{
                    Toast.makeText(holder.itemView.context, "Error in Message Deleted", Toast.LENGTH_SHORT).show()
                }

            }
    }

    private fun msgOptions(position:Int, holder: ViewHolder){
        val options = arrayOf<CharSequence>("Delete","Copy")
        var builder = AlertDialog.Builder(holder.itemView.context)
        builder.setTitle("Options")
        builder.setItems(options, DialogInterface.OnClickListener { dialog, i ->
            if (i == 0) {
                deleteMsg(position,holder)
            }
            else if(i==1){
                copyText(position,holder)
            }
        })
        builder.show()
    }

    private fun copyText(position: Int, holder: ChatsAdapter.ViewHolder) {

        var str = mChatList.get(position).getMessage()

        var myClipboard = getSystemService(mContext, ClipboardManager::class.java) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", str)

        myClipboard.setPrimaryClip(clip)
    }
}