package com.raxx.ismartchat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import android.widget.Button
import android.widget.DatePicker
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_register.*
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.view.Window
import com.google.firebase.database.FirebaseDatabase
import java.util.*
import kotlin.collections.HashMap

class RegisterActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener{

    private lateinit var mAuth:FirebaseAuth
    private lateinit var refUser:DatabaseReference
    private var firebassuserId:String = ""
    var day =0
    var month =0
    var year =0

    var savedDay ="DD"
    var savedMonth ="MM"
    var savedYear ="YYYY"
    val context=this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        mAuth=FirebaseAuth.getInstance()
        rdateId.setOnClickListener {
            val cal = Calendar.getInstance()
            day=cal.get(Calendar.DAY_OF_MONTH)
            month=cal.get(Calendar.MONTH)
            year= cal.get((Calendar.YEAR))
            var dialog:DatePickerDialog =DatePickerDialog(this,this, year,month,day)
            dialog.datePicker.maxDate=Calendar.getInstance().timeInMillis
            dialog.show()
        }

        gobacklogin.setOnClickListener {
            var intent = Intent(this,WelcomeActivity::class.java)
            startActivity(intent)
        }

        rsignup.setOnClickListener {
            var flag=0
            if(rname.text.isEmpty()) {
                rname.setError("Name cannot be Empty")
                rname.requestFocus()
                flag++
            }
            if(rEmailAddress.text.isEmpty()){
                rEmailAddress.setError("Email cannot be Empty")
                rname.requestFocus()
                flag++
            }
            if(rdateOfBirth.text.isEmpty()){
                rdateOfBirth.setError("Date of Birth cannot be Empty")
                rdateOfBirth.requestFocus()
                flag++
            }
            if(rpass1.text.isEmpty()){
                rpass1.setError("Password cannot be Empty")
                rpass1.requestFocus()
                flag++
            }
            if (rpass1.text.toString()!=rpass2.text.toString()){
                rpass2.setError("Password didn't match")
                rpass2.requestFocus()
                flag++
                Toast.makeText(applicationContext, "${rpass1.text} ${rpass2.text}", Toast.LENGTH_SHORT).show()
            }

            if(flag==0){
                Toast.makeText(applicationContext, "Testing", Toast.LENGTH_SHORT).show()
                register()
            }

        }


        fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
            savedYear=p1.toString()
            savedMonth=p2.toString()
            savedDay=p3.toString()
            var s:String = savedDay+"/"+savedMonth+"/"+savedYear
            rdateOfBirth.setText(s)
        }



    }

    private fun register() {
       mAuth.createUserWithEmailAndPassword(rEmailAddress.text.toString(), rpass1.text.toString()).addOnCompleteListener{
           task -> if(task.isSuccessful){
                    firebassuserId = mAuth.currentUser?.uid.toString()
                    refUser = FirebaseDatabase.getInstance().reference.child("Users").child(firebassuserId)
                    val userHashMap = HashMap<String, Any>()
                    userHashMap["uid"]=firebassuserId
                   userHashMap["userName"]=rname.text.toString()
                   userHashMap["profile"]="https://firebasestorage.googleapis.com/v0/b/ismartchat-3dc3e.appspot.com/o/profile.png?alt=media&token=ecc47a5c-806c-4dcf-bf6d-7d3318058750"
                   userHashMap["cover"]="https://firebasestorage.googleapis.com/v0/b/ismartchat-3dc3e.appspot.com/o/cover.jfif?alt=media&token=48bd085a-0a71-46eb-9c00-1c128e69a299"
                   userHashMap["status"]="offline"
                   userHashMap["search"]=rname.text.toString().toLowerCase()
                   userHashMap["DOB"]=rdateOfBirth.text.toString()
                   userHashMap["email"]=rEmailAddress.text.toString()

                    refUser.updateChildren(userHashMap)
                        .addOnCompleteListener {
                                task -> if(task.isSuccessful){
                            Toast.makeText(applicationContext, "Successful Registered, Please Login", Toast.LENGTH_SHORT).show()
                            var intent = Intent(this,WelcomeActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                                        }
                        }


                    }
                    else{
                    Toast.makeText(applicationContext, "Error : ${task.exception?.message.toString()}", Toast.LENGTH_SHORT).show()
                    }
       }
    }

    override fun onDateSet(p0: DatePicker?, p1: Int, p2: Int, p3: Int) {
        savedYear=p1.toString()
        savedMonth=p2.toString()
        savedDay=p3.toString()
        var s:String = savedDay+"/"+savedMonth+"/"+savedYear
        rdateOfBirth.setText(s)
    }
}