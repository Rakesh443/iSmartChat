package com.raxx.ismartchat


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.raxx.ismartchat.Fragments.ChatsFragment
import com.raxx.ismartchat.Fragments.GroupsFragment
import com.raxx.ismartchat.Fragments.SearchFragment
import com.raxx.ismartchat.Fragments.SettingsFragment
import com.raxx.ismartchat.Models.User
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? =null
    var firebaseUser: FirebaseUser? =null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar_main))

        firebaseUser= FirebaseAuth.getInstance().currentUser
        refUsers=FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUsers!!.keepSynced(true)

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""



        val tableLayout: TabLayout =findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)
        val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)



        viewPagerAdapter.addFragment(ChatsFragment(), "Chats")
//        viewPagerAdapter.addFragment(GroupsFragment(), "Groups")
        viewPagerAdapter.addFragment(SearchFragment(), "Search")
        viewPagerAdapter.addFragment(SettingsFragment(), "Settings")


        viewPager.adapter=viewPagerAdapter
        tableLayout.setupWithViewPager(viewPager)

        //display DP
        refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: User? = p0.getValue(User::class.java)

                    user_name.text = user?.getUserName()
                    Picasso.get().load(user?.getProfile()).placeholder(R.drawable.profile).into(
                        profile_image
                    )
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }

        })

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.logout -> {
                FirebaseAuth.getInstance().signOut()
                val intent1 = Intent(this, WelcomeActivity::class.java)
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent1)
                return true
            }
            R.id.menu_settings -> {
                var intent1 = Intent(this, ProfileActivity::class.java)
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent1)
                return true
            }
            R.id.menu_explore -> {
                var intent1 = Intent(this, ExploreActivity::class.java)
                intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent1)
                return true
            }
//            R.id.menu_group -> {
//                val builder = AlertDialog.Builder(this)
//                builder.setTitle("Create Group")
//                builder.setMessage("Enter group name")
//                val input = EditText(this)
//                builder.setView(input)
//
//
//                builder.setPositiveButton(android.R.string.yes) { dialog, which ->
//                    Toast.makeText(
//                        applicationContext,
//                        android.R.string.yes, Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                builder.setNegativeButton(android.R.string.no) { dialog, which ->
//                    Toast.makeText(
//                        applicationContext,
//                        android.R.string.no, Toast.LENGTH_SHORT
//                    ).show()
//                }
//
//                builder.setNeutralButton("Maybe") { dialog, which ->
//                    Toast.makeText(
//                        applicationContext,
//                        "Maybe", Toast.LENGTH_SHORT
//                    ).show()
//                }
//                builder.show()
//                return true
//            }

        }
        return false
    }

    internal class ViewPagerAdapter(fragmentManager: FragmentManager) :
            FragmentPagerAdapter(fragmentManager){

        private  val fragments:ArrayList<Fragment> = ArrayList<Fragment>()
        private val titles:ArrayList<String> = ArrayList<String>()


         override fun getCount(): Int {
            return fragments.size
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        fun addFragment(fragment: Fragment, title: String){
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {

            return titles[position]
        }

    }
}