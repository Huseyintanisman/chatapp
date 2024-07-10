package com.huseyin.chatappp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ServerValue
import com.huseyin.chatappp.Adapters.RecyclerViewAdapter
import com.huseyin.chatappp.R
import java.util.*

class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerViewAdapter: RecyclerViewAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var messageText: EditText
    private val chatMessages = ArrayList<Pair<String, String>>()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var messageButton: AppCompatButton
    private lateinit var category: String

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.options_menu_sign_out -> {
                auth.signOut()
                val intent = Intent(applicationContext, SignUpActivity::class.java)
                startActivity(intent)
            }
            R.id.options_menu_profile -> {
                val intent = Intent(applicationContext, ProfileActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)

        category = intent.getStringExtra("CATEGORY") ?: "default"

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference.child("Chats").child(category)

        messageText = findViewById(R.id.chatActivtyMessageText)
        recyclerView = findViewById(R.id.recyclerViewChat)
        recyclerViewAdapter = RecyclerViewAdapter(chatMessages)
        val recyclerViewManager = LinearLayoutManager(this)
        recyclerView.layoutManager = recyclerViewManager
        recyclerView.adapter = recyclerViewAdapter

        messageButton = findViewById(R.id.chatActivtyMessageButton)

        messageButton.setOnClickListener {
            val messageToSend = messageText.text.toString().trim()
            if (messageToSend.isNotBlank()) {
                val uuid = UUID.randomUUID().toString()
                val firebaseUser = auth.currentUser
                val userEmail = firebaseUser?.email.toString()

                val newMessageRef = databaseReference.child(uuid)

                val messageMap = mapOf(
                    "userMessage" to messageToSend,
                    "userEmail" to userEmail,
                    "userMessageTime" to ServerValue.TIMESTAMP
                )

                newMessageRef.setValue(messageMap)
                    .addOnSuccessListener {
                        messageText.setText("")
                    }
                    .addOnFailureListener { e ->
                        println("Error sending message: ${e.message}")
                    }
            }
        }

        getData()
    }

    private fun getData() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatMessages.clear()

                for (ds in snapshot.children) {
                    val usermail = ds.child("userEmail").value as? String
                    val usermessage = ds.child("userMessage").value as? String

                    usermail?.let { email ->
                        usermessage?.let { message ->
                            chatMessages.add(email to message)
                        }
                    }
                }
                recyclerViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }
        })
    }
}
