package com.huseyin.chatappp.Activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.huseyin.chatappp.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userRef: DatabaseReference

    private lateinit var profileNameTextView: TextView
    private lateinit var profileAgeTextView: TextView
    private lateinit var profileMailTextView: TextView
    private lateinit var profileImageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        userRef = database.reference.child("Users").child(auth.currentUser!!.uid)

        profileNameTextView = findViewById(R.id.profileNameTextView)
        profileAgeTextView = findViewById(R.id.ageEditText)
        profileMailTextView = findViewById(R.id.mailAdress)
        profileImageView = findViewById(R.id.profileImageView)

        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val name = dataSnapshot.child("name").value.toString()
                val age = dataSnapshot.child("age").value.toString()
                val surname = dataSnapshot.child("surname").value.toString()
                val avatar = dataSnapshot.child("avatar").value.toString()

                profileNameTextView.text = "$name $surname"
                profileAgeTextView.text = "$age"
                profileMailTextView.text = auth.currentUser?.email

                val avatarResource = getAvatarResource(avatar)
                profileImageView.setImageResource(avatarResource)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error if needed
            }
        })
    }

    private fun getAvatarResource(avatar: String): Int {
        return when (avatar) {
            "avatar1" -> R.drawable.avatar1
            "avatar2" -> R.drawable.avatar2
            else -> R.drawable.avatar1
        }
    }
}
