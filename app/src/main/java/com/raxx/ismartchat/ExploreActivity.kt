package com.raxx.ismartchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raxx.ismartchat.AdapterClasses.UserAdapter
import com.raxx.ismartchat.Models.User
import kotlinx.android.synthetic.main.activity_explore.*
import java.util.ArrayList


private var userAdapter: UserAdapter? =null
private var mUsers : List<User>? = null
private var recyclerView: RecyclerView?=null
private var searchEditText: EditText?=null
class ExploreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_explore)



        recyclerView= findViewById(R.id.explore_searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager= LinearLayoutManager(this)
        searchEditText= findViewById(R.id.explore_search)

        mUsers=ArrayList()
        retrieveAllUsers()

        explore_back.setOnClickListener {
            onBackPressed()
        }

        searchEditText!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUser(p0.toString().toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
    }






    private fun retrieveAllUsers() {

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val  refUsers= FirebaseDatabase.getInstance().reference.child("Users")
        refUsers.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                (mUsers as ArrayList<User>).clear()
                if(searchEditText!!.text.toString()==""){
                    for (snapshot in p0.children) {
                        val user: User? = snapshot.getValue(User::class.java)
                        if (firebaseUserID.equals("Y3MDAIn9fyUNL8mYYorwQl0z5Cu2"))
                            (mUsers as ArrayList).add(user!!)
                        else if (!(user!!.getUid().equals(firebaseUserID)) && !user!!.getPrivateAccount()) {
                            (mUsers as ArrayList<User>).add(user)
                        }
                    }



                    userAdapter = UserAdapter(this@ExploreActivity!!,mUsers!!,false)
//                    recyclerView!!.adapter = userAdapter
                }

            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })



    }


    private fun searchForUser(str:String){

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val quertUsers = FirebaseDatabase.getInstance()
            .reference.child("Users").orderByChild("search")
            .startAt(str)
            .endAt(str+"\uf8ff")

        quertUsers.addValueEventListener(object : ValueEventListener{

            override fun onDataChange(p0: DataSnapshot) {
                recyclerView!!.setVisibility(View.VISIBLE)
                (mUsers as ArrayList<User>).clear()
                for (snapshot in p0.children){
                    val user:User? = snapshot.getValue(User::class.java)
                    if (firebaseUserID.equals("Y3MDAIn9fyUNL8mYYorwQl0z5Cu2"))
                        (mUsers as ArrayList).add(user!!)
                    else if (!(user!!.getUid().equals(firebaseUserID)) && !user!!.getPrivateAccount())
                        (mUsers as ArrayList<User>).add(user)

                }
                userAdapter = UserAdapter(this@ExploreActivity!!,mUsers!!,false)
                if(str=="")
                    recyclerView!!.setVisibility(View.GONE)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }



    override fun onBackPressed() {
        super.onBackPressed()
        var intent1 = Intent(this,MainActivity::class.java)
        intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent1)
    }
}