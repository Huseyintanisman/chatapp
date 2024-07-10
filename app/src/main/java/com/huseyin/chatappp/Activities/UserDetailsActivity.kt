package com.huseyin.chatappp.Activities

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.huseyin.chatappp.R

class UserDetailsActivity : AppCompatActivity() {
    private lateinit var selectImageButton: ImageButton
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private var selectedAvatar: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_details)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        selectImageButton = findViewById(R.id.SelectImageButton1)
        saveButton = findViewById(R.id.saveButton)
        backButton = findViewById(R.id.backToSignInPageButton)

        selectImageButton.setOnClickListener {
            showSelectAvatarDialog()
        }

        saveButton.setOnClickListener {
            saveUserProfile()
        }

        backButton.setOnClickListener{
            val intent: Intent = Intent(
                this, SignUpActivity::class.java
            )
            startActivity(intent)
            finish()
        }
    }

    private fun showSelectAvatarDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_avatar, null)
        val avatarSelectDialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<ImageButton>(R.id.avatar1).setOnClickListener {
            selectedAvatar = "avatar1"
            Toast.makeText(this, "Male Avatar  selected", Toast.LENGTH_SHORT).show()
            avatarSelectDialog.dismiss()
        }

        dialogView.findViewById<ImageButton>(R.id.avatar2).setOnClickListener {
            selectedAvatar = "avatar2"
            Toast.makeText(this, "Female Avatar selected", Toast.LENGTH_SHORT).show()
            avatarSelectDialog.dismiss()
        }


        avatarSelectDialog.show()
    }

    private fun saveUserProfile() {
        val nameEditText: EditText = findViewById(R.id.nameEditText)
        val surnameEditText: EditText = findViewById(R.id.surnameEditText)
        val ageEditText: EditText = findViewById(R.id.ageEditText)

        val name = nameEditText.text.toString().trim()
        val surname = surnameEditText.text.toString().trim()
        val age = ageEditText.text.toString().trim().toIntOrNull()

        if (name.isEmpty() || surname.isEmpty() || age == null || selectedAvatar.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields and select an avatar", Toast.LENGTH_SHORT).show()
            return
        }

        val user = auth.currentUser
        if (user != null) {
            val userProfile = HashMap<String, Any>()
            userProfile["name"] = name
            userProfile["surname"] = surname
            userProfile["age"] = age
            userProfile["avatar"] = selectedAvatar

            val userRef = database.reference.child("Users").child(user.uid)

            userRef.setValue(userProfile).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User profile saved successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, CategoryActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to save user profile", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
