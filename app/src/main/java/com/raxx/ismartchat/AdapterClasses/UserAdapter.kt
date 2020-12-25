package com.raxx.ismartchat.AdapterClasses

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raxx.ismartchat.MessageActivity
import com.raxx.ismartchat.Models.User
import com.raxx.ismartchat.R
import com.squareup.picasso.Picasso
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.android.synthetic.main.activity_main.*

class UserAdapter(mContext:Context,mUsers:List<User>,isChatCheck:Boolean)
    : RecyclerView.Adapter<UserAdapter.ViewHolder?>(){

    private val mContext:Context
    private val mUser:List<User>
    private var isChatCheck:Boolean

    init {
        this.isChatCheck = isChatCheck
        this.mContext = mContext
        this.mUser = mUsers
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view:View = LayoutInflater.from(mContext).inflate(R.layout.user_search_layout,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val user:User?=mUser[position]

        holder.userNameTxt.text = user!!.getUserName()
        Picasso.get().load(user?.getProfile()).into(holder.profileImageView)

        holder.itemView.setOnClickListener {
//            val option = arrayOf<CharSequence>(
//                "Send Message",
//                "Visit Profile"
//            )
//            val builder:AlertDialog.Builder = AlertDialog.Builder(mContext)
//            builder.setTitle("What do you want?")
//            builder.setItems(option,DialogInterface.OnClickListener{
//                dialogInterface, i ->
//                if (i==0){
//                    val intent = Intent(mContext, MessageActivity::class.java)
//                    intent.putExtra("visit_id",user.getUid())
//                    mContext.startActivity(intent)
//                }
//                if (i==1){
//
//                }
//
//            })
//            builder.show()
            val intent = Intent(mContext, MessageActivity::class.java)
            intent.putExtra("visit_id",user.getUid())
            mContext.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return  mUser.size

    }

    class ViewHolder(itemView: View) :RecyclerView.ViewHolder(itemView){
        var userNameTxt: TextView
        var profileImageView: CircleImageView
        var onlineImageView: CircleImageView
        var offlineImageView: CircleImageView
        var lastMessageTxt: TextView

        init {
            userNameTxt = itemView.findViewById(R.id.search_user_name)
            profileImageView = itemView.findViewById(R.id.profile_image)
            onlineImageView = itemView.findViewById(R.id.image_online)
            offlineImageView = itemView.findViewById(R.id.image_offline)
            lastMessageTxt = itemView.findViewById(R.id.message_last)
        }
    }




}