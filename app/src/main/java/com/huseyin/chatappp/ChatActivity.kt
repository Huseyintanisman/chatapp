package com.huseyin.chatappp
import RecyclerViewAdapter
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import java.util.UUID


class ChatActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var recyclerViewAdapter : RecyclerViewAdapter
    private lateinit var recyclerview : RecyclerView
    private lateinit var messageText : EditText
    private val chatMessages = ArrayList<Pair<String, String>>()
    private lateinit var database : FirebaseDatabase
    private lateinit var databaseReferance : DatabaseReference
    private lateinit var newdatabaseReferance : DatabaseReference
    private lateinit var MessageButton : AppCompatButton

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.option_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.options_menu_sign_out) {
            auth.signOut()
            val intent = Intent(applicationContext, SignUpActivity::class.java)
            startActivity(intent)
        } else if (item.itemId == R.id.options_menu_profile) {
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
        database = FirebaseDatabase.getInstance()
        databaseReferance = database.getReference()

        messageText = findViewById(R.id.chatActivtyMessageText)
        recyclerview = findViewById(R.id.recyclerViewChat)
        recyclerViewAdapter = RecyclerViewAdapter(chatMessages)
        val recyclerViewManager = LinearLayoutManager(this)
        recyclerview.layoutManager = recyclerViewManager
        recyclerview.adapter = recyclerViewAdapter

        MessageButton = findViewById(R.id.chatActivtyMessageButton)

        MessageButton.setOnClickListener {
            val messageToSend = messageText.text.toString()
            val uuid = UUID.randomUUID().toString()
            val firebaseUser = auth.currentUser
            val userEmail = firebaseUser?.email.toString()

            databaseReferance.child("Chats").child(uuid).child("userMessage").setValue(messageToSend)
            databaseReferance.child("Chats").child(uuid).child("userEmail").setValue(userEmail)
            databaseReferance.child("Chats").child(uuid).child("userMessageTime").setValue(ServerValue.TIMESTAMP)
            messageText.setText("")

            getData()
        }

        getData()
    }

    fun getData() {
        newdatabaseReferance = database.getReference("Chats")
        val query: Query = newdatabaseReferance.orderByChild("userMessageTime")
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatMessages.clear()

                for (ds in snapshot.children) {
                    val hashMap = ds.value as? Map<String, Any>
                    val usermail = hashMap?.get("userEmail") as? String
                    val usermessage = hashMap?.get("userMessage") as? String

                    if (usermail != null && usermessage != null) {
                        chatMessages.add(usermail to usermessage)
                        recyclerViewAdapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }
        })
    }
}
