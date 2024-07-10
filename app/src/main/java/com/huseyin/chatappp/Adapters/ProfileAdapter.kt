package com.huseyin.chatappp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.huseyin.chatappp.R
import com.squareup.picasso.Picasso

class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private val userRef = database.reference.child("Users").child(auth.currentUser!!.uid)

    private var userProfile: Map<String, Any> = emptyMap()

    init {
        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                userProfile = dataSnapshot.value as Map<String, Any>
                notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_profile, parent, false)
        return ProfileViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProfileViewHolder, position: Int) {
        val name = userProfile["name"].toString()
        val surname = userProfile["surname"].toString()
        val avatar = userProfile["avatar"].toString()
        val email = auth.currentUser?.email

        holder.profileNameTextView.text = "$name $surname"
        holder.profileMailTextView.text = email

        val avatarUrl = getAvatarUrl(avatar)
        Picasso.get().load(avatarUrl).into(holder.profileImageView)
    }

    override fun getItemCount(): Int {
        return if (userProfile.isEmpty()) 0 else 1
    }

    private fun getAvatarUrl(avatar: String): String {
        return when (avatar) {
            "avatar1" -> "https://firebasestorage.googleapis.com/v0/b/chatappp-dfdde.appspot.com/o/path_to_avatar1_image"
            "avatar2" -> "https://firebasestorage.googleapis.com/v0/b/chatappp-dfdde.appspot.com/o/path_to_avatar2_image"
            else -> ""
        }
    }

    class ProfileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileNameTextView: TextView = itemView.findViewById(R.id.profileNameTextView)
        val profileMailTextView: TextView = itemView.findViewById(R.id.mailAdress)
        val profileImageView: ImageView = itemView.findViewById(R.id.profileImageView)
    }
}
