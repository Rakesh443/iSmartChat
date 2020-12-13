package com.raxx.ismartchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.android.synthetic.main.activity_welcome.*

class WelcomeActivity : AppCompatActivity() {

    var firebaseUser:FirebaseUser?=null;
    private lateinit var mAuth:FirebaseAuth
    private lateinit var refUser: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        mAuth = FirebaseAuth.getInstance()

        signupId.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        logId.setOnClickListener {
            loginUser()
        }


    }

    private fun loginUser() {
        var flag=0
        val name:String=nameId.text.toString()
        val pas:String=passId.text.toString()
        if(name.isEmpty()) {
            rname.setError("Name cannot be Empty")
            rname.requestFocus()
            flag++
        }
        if(pas.isEmpty()){
            rEmailAddress.setError("Email cannot be Empty")
            rname.requestFocus()
            flag++
        }
        if(flag==0){
            mAuth.signInWithEmailAndPassword(name,pas)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        var intent = Intent(this,MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                    }else{
                        Toast.makeText(applicationContext, "Error : ${task.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if(firebaseUser != null){
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}