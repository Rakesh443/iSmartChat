package com.raxx.ismartchat.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.raxx.ismartchat.AdapterClasses.CardChatAdapter
import com.raxx.ismartchat.AdapterClasses.UserAdapter
import com.raxx.ismartchat.Models.Chatlist
import com.raxx.ismartchat.Models.User
import com.raxx.ismartchat.Notifications.Token
import com.raxx.ismartchat.R



class ChatsFragment : Fragment() {

    private var userAdapter: UserAdapter? =null
    private var cardChatAdapter: CardChatAdapter? =null
    private var mUsers : List<User>? = null
    private var userChatList : List<Chatlist>? = null
    lateinit var recycler_view_chartlist:RecyclerView
    private var firebaseUser: FirebaseUser?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view= inflater.inflate(R.layout.fragment_chats, container, false)

        recycler_view_chartlist = view.findViewById(R.id.recycler_view_chartlist)
        recycler_view_chartlist.setHasFixedSize(true)
        recycler_view_chartlist.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userChatList = ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("ChatList").child(firebaseUser!!.uid)
        ref!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (userChatList as ArrayList).clear()
                for(snapshot in p0.children){
                    val chatlist = snapshot.getValue(Chatlist::class.java)
                    (userChatList as ArrayList).add(chatlist!!)
                    retriveChatList()

                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })
        updateToken(FirebaseInstanceId.getInstance().token)
        return view
    }

    private fun updateToken(token: String?) {
        val ref = FirebaseDatabase.getInstance().reference.child("Tokens")
        val token1 = Token(token!!)
        ref.child(firebaseUser!!.uid).setValue(token1)
    }

    private fun retriveChatList(){
        mUsers=ArrayList()

        val ref = FirebaseDatabase.getInstance().reference.child("Users")

        ref.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList).clear()
                for (snapShot in p0.children){
                    val user=snapShot.getValue(User::class.java)
                    for (eachChatList in userChatList!!){
                        if(user!!.getUid().equals(eachChatList.getId())){

                            (mUsers as ArrayList).add(user!!)
                        }
                    }
                }
//                userAdapter = UserAdapter(context!!, mUsers as ArrayList<User>,true)
                cardChatAdapter = CardChatAdapter(context!!, mUsers as ArrayList<User>,true)
                recycler_view_chartlist.adapter=cardChatAdapter
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }
}