package com.huseyin.chatappp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.huseyin.chatappp.databinding.ActivityChatBinding


class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
//    private lateinit var binding: ActivityChatBinding

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.options_menu_sign_out)
        {
            auth.signOut()
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        }
        else if (item.itemId == R.id.options_menu_profile)
        {
            val intent = Intent(applicationContext, ProfileActivity::class.java)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        auth = FirebaseAuth.getInstance()

//        binding.chatActivtyMessageButton.setOnClickListener{
//
//        }
    }
}