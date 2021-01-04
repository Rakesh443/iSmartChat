package com.raxx.ismartchat

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
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
import com.raxx.ismartchat.Fragments.APIService
import com.raxx.ismartchat.Models.Chat
import com.raxx.ismartchat.Models.User
import com.raxx.ismartchat.Notifications.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_message.*
import retrofit2.Call
import retrofit2.Response
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class MessageActivity : AppCompatActivity() {

    var userIdVisit :String = ""
    var firebaseUser:FirebaseUser? = null
    var chatsAdapter:ChatsAdapter?=null
    var mChatList:List<Chat>?=null
    lateinit var recyclerView_view_chats:RecyclerView
    var reference :DatabaseReference?=null
    var name :String = ""
    var notify=false
    var apiService:APIService?=null
//    private val encryptionKey =
//        byteArrayOf(12, 45, 67, 89, 32, 65, 87, -5, 11, -52, 5, -6, 87, 33, 3, -2)
//    private lateinit var cipher:Cipher
//    private lateinit var decipher:Cipher
//    private lateinit var secretKey: SecretKeySpec






    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)


//        cipher = Cipher.getInstance("AES")
//        decipher = Cipher.getInstance("AES")
//        secretKey = SecretKeySpec(encryptionKey,"AES")

        intent = intent
        name = intent.getStringExtra("userid").toString()

        userIdVisit = intent.getStringExtra("visit_id").toString()


        if(name!="null"){
            userIdVisit=name
        }

        firebaseUser = FirebaseAuth.getInstance().currentUser


        apiService = Client.Client.getClient("https://fcm.googleapis.com/")!!.create(APIService::class.java)

        recyclerView_view_chats = findViewById(R.id.recycler_view_charts)
        recyclerView_view_chats.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView_view_chats.layoutManager = linearLayoutManager

        reference = FirebaseDatabase.getInstance().reference
            .child("Users").child(userIdVisit)
        reference!!.keepSynced(true)
        reference!!.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                val user: User? = p0.getValue((User::class.java))

                msg_chat_user_name.text = user!!.getUserName()

                Picasso.get().load(user.getProfile()).into(msg_chat_profile_image)



                retriveMessages(firebaseUser!!.uid, userIdVisit, user.getProfile())
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })


        msg_send_btn.setOnClickListener {
            val current = LocalDateTime.now()

            val formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
            val formatted = current.format(formatter)


            notify=true
//            val msg = AESEncryption(text_message.text.toString())
            val message=text_message.text.toString()
            if(message==""){
                Toast.makeText(applicationContext, "write something", Toast.LENGTH_SHORT).show()
            }
            else{
                sendMessageToUser(firebaseUser!!.uid, userIdVisit, message,formatted)

            }
            text_message.setText("")
        }

        attach_file.setOnClickListener {
//            notify=true
//            val intent =Intent()
//            intent.action = Intent.ACTION_GET_CONTENT
//            intent.type = "image/*"
//            startActivityForResult(Intent.createChooser(intent,"Pick Image"), 438)
        }

        seenMessage(userIdVisit)
    }


//    private fun AESEncryption(msg:String) : String{
//        var str = msg.toByteArray()
//        val returnStr:String=""
//        var encryptedBytes = byteArrayOf(str.size.toByte())
//        cipher.init(Cipher.ENCRYPT_MODE,secretKey)
//        encryptedBytes=cipher.doFinal(str)
//
//        return returnStr
//    }




    private fun sendMessageToUser(
        senderid: String,
        receiverId: String,
        message: String,
        tym: String
    ) {



        val reference = FirebaseDatabase.getInstance().reference
        val messageKey = reference.push().key

        val messageHashMap = HashMap<String, Any?>()
        messageHashMap["sender"] = senderid
        messageHashMap["message"] = message
        messageHashMap["receiver"] = receiverId
        messageHashMap["isSeen"] = false
        messageHashMap["url"] = ""
        messageHashMap["messageId"] = messageKey
        messageHashMap["tymdate"] = tym

        reference.child("Chats")
            .child(messageKey!!)
            .setValue(messageHashMap).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val chatListReference = FirebaseDatabase.getInstance()
                        .reference.child("ChatList")
                        .child(firebaseUser!!.uid)
                        .child(userIdVisit)

                    chatListReference.addValueEventListener(object : ValueEventListener {
                        override fun onDataChange(p0: DataSnapshot) {
                            if (!p0.exists()) {
                                chatListReference.child("id").setValue(userIdVisit)
                            }
                            val chatListReceiverReference = FirebaseDatabase.getInstance()
                                .reference.child("ChatList")
                                .child(userIdVisit)
                                .child(firebaseUser!!.uid)
                            chatListReceiverReference.child("id").setValue(firebaseUser!!.uid)

                        }

                        override fun onCancelled(p0: DatabaseError) {

                        }

                    })



                }
            }
        //dfghjkl;
        val userreference = FirebaseDatabase.getInstance().reference
            .child("Users").child(firebaseUser!!.uid)

        userreference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val user = p0.getValue(User::class.java)

                if (notify) {
                    sendNotification(receiverId, user!!.getUserName(), message)
                }
                notify = false
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })



    }

    private fun sendNotification(receiverId: String, userName: String?, message: String) {


        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")

        val query = ref.orderByKey().equalTo(receiverId)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {

                for (snapshot in p0.children) {
                    val token: Token? = snapshot.getValue(Token::class.java)

                    val data = Data(
                        firebaseUser!!.uid,
                        R.drawable.ic_baseline_message_24,
                        message,
                        "$userName",
                        userIdVisit
                    )
                    val sender = Sender(data!!, token!!.getToken().toString())


                    apiService!!.sendNotification(sender)
                        .enqueue(object : retrofit2.Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()!!.success !== 1) {
                                        Toast.makeText(
                                            this@MessageActivity,
                                            "Failed, Nothing happen...",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }

                        })
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

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

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (task.isSuccessful) {
                    task.exception?.let {
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
                    messageHashMap["message"] = "sent"
                    messageHashMap["receiver"] = userIdVisit
                    messageHashMap["isseen"] = false
                    messageHashMap["url"] = url
                    messageHashMap["messageId"] = messageId

                    ref.child("Chats").child(messageId!!).setValue(messageHashMap)
                        .addOnCompleteListener { task ->
                            if(task.isSuccessful){
                                progressBar.dismiss()
                                val reference = FirebaseDatabase.getInstance().reference
                                    .child("Users").child(firebaseUser!!.uid)


                                reference.addValueEventListener(object : ValueEventListener {
                                    override fun onDataChange(p0: DataSnapshot) {
                                        val user = p0.getValue(User::class.java)

                                        if (notify) {
                                            sendNotification(
                                                userIdVisit,
                                                user!!.getUserName(),
                                                "Sent you an Image"
                                            )
                                        }

                                        notify = false
                                    }

                                    override fun onCancelled(p0: DatabaseError) {

                                    }

                                })
                            }
                        }


                }

            }
        }
    }

    private fun retriveMessages(senderId: String, receiverId: String, receiverImageUrl: String?) {
        mChatList = ArrayList()
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mChatList as ArrayList<Chat>).clear()
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)

                    if (chat!!.getReceiver().equals(senderId) && chat.getSender().equals(receiverId)
                        || chat.getReceiver().equals(receiverId) && chat.getSender()
                            .equals(senderId)
                    ) {
                        (mChatList as ArrayList<Chat>).add(chat)
                    }

                    chatsAdapter = ChatsAdapter(
                        this@MessageActivity,
                        mChatList as ArrayList<Chat>, receiverImageUrl!!
                    )

                    recyclerView_view_chats.adapter = chatsAdapter

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    var seenListener : ValueEventListener?= null

    private fun seenMessage(userId: String){
        val reference = FirebaseDatabase.getInstance().reference.child("Chats")
        seenListener = reference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                for (snapshot in p0.children) {
                    val chat = snapshot.getValue(Chat::class.java)
                    if (chat!!.getReceiver()!!.equals(firebaseUser!!.uid) && chat!!.getSender()
                            .equals(
                                userId
                            )
                    ) {
                        val hashMap = HashMap<String, Any>()
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