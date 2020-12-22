package com.raxx.ismartchat

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.raxx.ismartchat.AdapterClasses.ChatsAdapter
import com.raxx.ismartchat.Models.Chat
import com.raxx.ismartchat.Models.User
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import java.lang.Exception

class MessageActivity : AppCompatActivity() {

    var userIdVisit :String = ""
    var firebaseUser:FirebaseUser? = null
    var chatsAdapter:ChatsAdapter?=null
    var mChatList:List<Chat>?=null
    lateinit var recyclerView_view_chats:RecyclerView
    var reference :DatabaseReference?=null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        intent = intent
        userIdVisit = intent.getStringExtra("visit_id").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser



        recyclerView_view_chats = findViewById(R.id.recycler_view_charts)
        recyclerView_view_chats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView_view_chats.layoutManager = linearLayoutManager

         reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.keepSynced(true)
        reference!!.addValueEventListener(object :ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                val user:User? = p0.getValue((User::class.java))

                msg_chat_user_name.text=user!!.getUserName()

                Picasso.get().load(user.getProfile()).networkPolicy(NetworkPolicy.OFFLINE)
                    .into(msg_chat_profile_image, object : Callback {
                        override fun onSuccess() {

                        }

                        override fun onError(e: Exception?) {
                            Picasso.get().load(user.getProfile()).into(msg_chat_profile_image)
                        }

                    })



                retriveMessages(firebaseUser!!.uid,userIdVisit,user.getProfile())
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })



        msg_send_btn.setOnClickListener {
            val message=text_message.text.toString()
            if(message==""){
                Toast.makeText(applicationContext, "write something", Toast.LENGTH_SHORT).show()
            }
            else{
                sendMessageToUser(firebaseUser!!.uid , userIdVisit,message)

            }
            text_message.setText("")
        }

        attach_file.setOnClickListener {
            val intent =Intent()
            intent.action = Intent.ACTION_GET_CONTENT
            intent.type = "image/*"
            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }

        seenMessage(userIdVisit)
    }



    private fun sendMessageToUser(senderid: String, receiverId: String, message: String) {

        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderid
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isSeen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey

        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val chatListReference = FirebaseDatabase.getInstance()
                        .reference.child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatListReference.addValueEventListener(object:ValueEventListener{
                        override fun onDataChange(p0: DataSnapshot) {
                            if(!p0.exists()){
                                chatListReference.child("id").setValue(userIdVisit)
                            }
                            val chatListReceiverReference = FirebaseDatabase.getInstance()
                                .reference.child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatListReceiverReference.child("id").setValue(firebaseUser!!.uid)

                        }

                        override fun onCancelled(p0: DatabaseError) {
                            TODO("Not yet implemented")
                        }

                    })




                    val reference = FirebaseDatabase.getInstance().reference
                        .child("Users").child(firebaseUser!!.uid)

                }
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode==438 && resultCode==RESULT_OK && data !=null && data!!.data!=null){

            val progressBar=ProgressDialog(this)
            progressBar.setMessage("image uploading...")
            progressBar.show()

            val fileUri = data.data
            val storageReference = FirebaseStorage.getInstance().reference.child("Chat Images")
            val ref = FirebaseDatabase.getInstance().reference
            val messageId = ref.push().key
            val filePath = storageReference.child("$messageId.jpg")

            var uploadTask: StorageTask<*>
            uploadTask = filePath.putFile(fileUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }

                }
                return@Continuation filePath.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    val messageHashMap = HashMap<String, Any?>()
                    messageHashMap["sender"] = firebaseUser!!.uid
                    messageHashMap["message"] = "sent you an image."
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)


                }

            }
        }
    }

    private fun retriveMessages(senderId: String, receiverId: String, receiverImageUrl: String?) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for(snapshot in p0.children){
                    val chat = snapshot.getValue(Chat::class.java)

                    if(chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender().equals(senderId)){
                        (mChatList as ArrayList<Chat>).add(chat)
                    }

                    chatsAdapter = ChatsAdapter(this@MessageActivity,
                        mChatList as ArrayList<Chat>,receiverImageUrl!!)
                    recyclerView_view_chats.adapter=chatsAdapter

                }
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })

    }

    var seenListener : ValueEventListener?= null

    private fun seenMessage(userId:String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children){
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver()!!.equals(firebaseUser!!.uid) && chat!!.getSender().equals(userId)){
                        val hashMap = HashMap<String,Any>()
                        hashMap["isseen"] = true
                        snapshot.ref.updateChildren(hashMap)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
    }

    override fun onPause() {
        super.onPause()

        reference!!.removeEventListener(seenListener!!)
    }
}