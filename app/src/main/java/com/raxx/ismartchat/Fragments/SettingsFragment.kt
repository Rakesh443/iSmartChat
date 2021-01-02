package com.raxx.ismartchat.Fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.raxx.ismartchat.Models.User
import com.raxx.ismartchat.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlinx.coroutines.flow.callbackFlow
import java.lang.Exception

class SettingsFragment : Fragment() {

    var userReference :DatabaseReference?=null
    var firebaseUser:FirebaseUser?=null
    private var RequestCode = 438
    private var imageUri:Uri?=null
    private var storageRef:StorageReference?=null
    private var coverChecker:String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        firebaseUser=FirebaseAuth.getInstance().currentUser
        userReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Images")
        userReference!!.keepSynced(true)


        userReference!!.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()){
                    val user:User? = p0.getValue(User::class.java)
                    if(context!=null){

                        view.setting_username.text=user!!.getUserName()
                        Picasso.get().load(user.getProfile()).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(view.setting_profile_image, object : Callback{
                                override fun onSuccess() {

                                }

                                override fun onError(e: Exception?) {
                                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile).into(view.setting_profile_image)
                                }

                            })

                        Picasso.get().load(user.getCover()).networkPolicy(NetworkPolicy.OFFLINE)
                            .into(view.setting_cover_image, object : Callback{
                                override fun onSuccess() {

                                }

                                override fun onError(e: Exception?) {
                                    Picasso.get().load(user.getCover()).placeholder(R.drawable.cover1234).into(view.setting_cover_image)
                                }

                            })





                    }
                }
            }



            override fun onCancelled(p0: DatabaseError) {

            }

        })

        view.setting_profile_image.setOnClickListener {
            pickImage()
        }

        view.setting_cover_image.setOnClickListener {
             coverChecker= "cover"
            pickImage()
        }

        return view
    }

    private fun pickImage() {
        val intent:Intent= Intent()
        intent.type="image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent,RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==RequestCode && resultCode == Activity.RESULT_OK && data!!.data !=null){
            imageUri = data.data
            Toast.makeText(context, "uploading...", Toast.LENGTH_LONG).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar=ProgressDialog(context)
        progressBar.setMessage("image uploading...")
        progressBar.show()

        if(imageUri!=null){
            val fileref = storageRef!!.child(System.currentTimeMillis().toString()+".jpg")

            var uploadTask:StorageTask<*>
            uploadTask = fileref.putFile(imageUri!!)

            uploadTask.continueWithTask(Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(task.isSuccessful){
                    task.exception?.let{
                        throw it
                    }

                }
                return@Continuation fileref.downloadUrl
            }).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    val downloadUrl = task.result
                    val url = downloadUrl.toString()

                    if(coverChecker == "cover"){
                        val mapCover = HashMap<String, Any>()
                        mapCover["cover"] = url
                        userReference!!.updateChildren(mapCover)
                        coverChecker=""
                    }
                    else{
                        val mapProfile = HashMap<String, Any>()
                        mapProfile["profile"] = url
                        userReference!!.updateChildren(mapProfile)
                        coverChecker=""

                    }

                }
            }
        }
    }
}