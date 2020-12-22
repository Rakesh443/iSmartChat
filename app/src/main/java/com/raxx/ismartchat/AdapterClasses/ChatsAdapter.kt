package com.raxx.ismartchat.AdapterClasses

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.raxx.ismartchat.Models.Chat
import com.raxx.ismartchat.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.view.*

class ChatsAdapter(
    mContext: Context,
    mChatList:List<Chat>,
    imageUrl:String
) :RecyclerView.Adapter<ChatsAdapter.ViewHolder?>(){


    private val mContext:Context
    private val mChatList:List<Chat>
    private val imageUrl:String

    var firebaseUser:FirebaseUser?=FirebaseAuth.getInstance().currentUser!!
    init {
        this.mContext=mContext
        this.mChatList=mChatList
        this.imageUrl=imageUrl
    }


    override fun onCreateViewHolder(parent: ViewGroup, position: Int): ViewHolder {

        return  if (position ==1){
            val view:View = LayoutInflater.from(mContext).inflate(R.layout.message_item_left,parent,false)
            ViewHolder(view)
        }
        else{
            val view:View = LayoutInflater.from(mContext).inflate(R.layout.message_item_right,parent,false)
            ViewHolder(view)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat = mChatList[position]

        Picasso.get().load(imageUrl).into(holder.profile_image)


        if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
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
            holder.show_text_message!!.text = chat.getMessage()
        }

        //sent and seen message
        if(position==mChatList.size-1) {
            if (chat.getIsseen()) {
                holder.text_seen!!.text = "Seen"

                if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")) {
                    val rl = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                    rl!!.setMargins(0, 245, 10, 0)
                    holder.text_seen!!.layoutParams = rl
                }

            }

           else{
               holder.text_seen!!.text = "Sent"

               if (chat.getMessage().equals("sent you an image.") && !chat.getUrl().equals("")){
                   val rl = holder.text_seen!!.layoutParams as RelativeLayout.LayoutParams?
                   rl!!.setMargins(0,245,10,0)
                   holder.text_seen!!.layoutParams =  rl
               }

           }


        }
        else{
            holder.text_seen!!.visibility = View.GONE
        }

    }

    override fun getItemCount(): Int {
        return mChatList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var show_text_message:TextView?=null
        var profile_image:ImageView?=null
        var left_image_view:ImageView?=null
        var text_seen:TextView?=null
        var right_image_view:ImageView?=null

        init {
            profile_image = itemView.findViewById(R.id.profile_image)
            show_text_message = itemView.findViewById(R.id.show_text_message)
            left_image_view = itemView.findViewById(R.id.left_image_view)
            text_seen = itemView.findViewById(R.id.text_seen)
            right_image_view = itemView.findViewById(R.id.right_image_view)

        }
    }

    override fun getItemViewType(position: Int): Int {



        return if(mChatList[position].getSender().equals(firebaseUser!!.uid)){
            0
        }
        else{
            1
        }
    }

}