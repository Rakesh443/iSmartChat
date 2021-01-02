package com.raxx.ismartchat.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.raxx.ismartchat.AdapterClasses.UserAdapter
import com.raxx.ismartchat.Models.User
import com.raxx.ismartchat.R
import java.util.ArrayList


class SearchFragment : Fragment() {

    private var userAdapter: UserAdapter? =null
    private var mUsers : List<User>? = null
    private var recyclerView:RecyclerView?=null
    private var searchEditText:EditText?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_search,container,false)

        recyclerView= view.findViewById(R.id.searchList)
        recyclerView!!.setHasFixedSize(true)
        recyclerView!!.layoutManager=LinearLayoutManager(context)
        searchEditText= view.findViewById(R.id.searchUsersET)

        mUsers=ArrayList()
        retrieveAllUsers()
        searchEditText!!.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchForUser(p0.toString().toLowerCase())
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        })
        return view
    }

    private fun retrieveAllUsers() {

        val firebaseUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val  refUsers= FirebaseDatabase.getInstance().reference.child("Users")
        refUsers.addValueEventListener(object : ValueEventListener{
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



                    userAdapter = UserAdapter(context!!,mUsers!!,false)
                    recyclerView!!.adapter = userAdapter
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
                (mUsers as ArrayList<User>).clear()
                for (snapshot in p0.children){
                    val user:User? = snapshot.getValue(User::class.java)
                    if(!(user!!.getUid().equals(firebaseUserID))){
                        (mUsers as ArrayList<User>).add(user)
                    }

                }
                userAdapter = UserAdapter(context!!,mUsers!!,false)
                recyclerView!!.adapter = userAdapter
            }

            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
    }

}