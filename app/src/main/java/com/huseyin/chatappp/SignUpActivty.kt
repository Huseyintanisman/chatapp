package com.huseyin.chatappp

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
//import com.google.android.gms.auth.api.identity.BeginSignInRequest
//import com.google.android.gms.auth.api.identity.Identity
//import com.google.android.gms.auth.api.identity.SignInClient
//import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.huseyin.chatappp.databinding.ActivitySignUpBinding
import com.huseyin.chatappp.util.CustomSharedPreferences

class SignUpActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var customSharedPreferences: CustomSharedPreferences
//    private lateinit var oneTapClient: SignInClient
//    private lateinit var signInRequest: BeginSignInRequest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        customSharedPreferences = CustomSharedPreferences(this)
        auth = FirebaseAuth.getInstance()
        auth.signOut()
        val currentUser = auth.currentUser
        updateUI(currentUser)
        loadCredentials()

//        signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    .setFilterByAuthorizedAccounts(false)
//                    .build()
//            )
//            .build()


        binding.buttonSignIn.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword2.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                signIn(email, password)
            } else {
                Toast.makeText(this, "E-mail veya şifre alanı boş olamaz", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonSignUp.setOnClickListener {
            val email = binding.editTextTextEmailAddress.text.toString()
            val password = binding.editTextTextPassword2.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                createAccount(email, password)
            } else {
                Toast.makeText(this, "E-mail veya şifre alanı boş olamaz", Toast.LENGTH_LONG).show()
            }
        }

        binding.buttonSignInWithGoogle.setOnClickListener {
           // startGoogleSignIn()
        }

        binding.resetPasswordText.setOnClickListener {
            showResetPasswordDialog()
        }

        binding.rememberMeCheckBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                saveCredentials()
            } else {
                clearCredentials()
            }
        }

//        // Google Sign-In İstemcisini ve İsteğini Başlat
//        oneTapClient = Identity.getSignInClient(this)
//        signInRequest = BeginSignInRequest.builder()
//            .setGoogleIdTokenRequestOptions(
//                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
//                    .setSupported(true)
//                    .setServerClientId(getString(R.string.your_web_client_id))
//                    .setFilterByAuthorizedAccounts(false)
//                    .build()
//            )
//            .build()
    }

    override fun onStart() {
        super.onStart()
    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SignUpActivity", "createUserWithEmail:success")
                    Toast.makeText(this, "User Created Succesfully", Toast.LENGTH_LONG).show()
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    Log.w("SignUpActivity", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    updateUI(null)
                }
            }
    }

//    private fun startGoogleSignIn() {
//        oneTapClient.beginSignIn(signInRequest)
//            .addOnSuccessListener(this) { result ->
//                try {
//                    googleSignInLauncher.launch(
//                        IntentSenderRequest.Builder(result.pendingIntent.intentSender).build()
//                    )
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//            .addOnFailureListener(this) { e ->
//                // Handle the error
//                Toast.makeText(this, "Google Sign-In Failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
//            }
//    }

//    private val googleSignInLauncher = registerForActivityResult(
//        ActivityResultContracts.StartIntentSenderForResult()
//    ) { result ->
//        if (result.resultCode == RESULT_OK) {
//            try {
//                val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
//                val idToken = credential.googleIdToken
//                if (idToken != null) {
//                    val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
//                    auth.signInWithCredential(firebaseCredential)
//                        .addOnCompleteListener(this) { task ->
//                            if (task.isSuccessful) {
//                                val user = auth.currentUser
//                                updateUI(user)
//                            } else {
//                                Toast.makeText(this, "Google Sign-In Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
//                            }
//                        }
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
//    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Şifre sıfırlama e-postası gönderildi.", Toast.LENGTH_LONG).show()
                } else {
                    val error = task.exception?.message ?: "Bilinmeyen hata"
                    Toast.makeText(this, "Hata: $error", Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("SignUpActivity", "signInWithEmail:success")
                    val user = auth.currentUser
                    val usermail = user?.email.toString()
                    Toast.makeText(applicationContext, "Log in successfully with $usermail", Toast.LENGTH_LONG).show()
                    updateUI(user)
                } else {
                    Log.w("SignUpActivity", "signInWithEmail:failure", task.exception)
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    //updateUI(null)
                }
            }
    }

    private fun showResetPasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_reset_password, null)
        val resetEmailEditText = dialogView.findViewById<EditText>(R.id.resetEmailEditText)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialogView.findViewById<Button>(R.id.sendButton).setOnClickListener {
            val email = resetEmailEditText.text.toString().trim()
            if (email.isNotEmpty()) {
                sendPasswordResetEmail(email)
            } else {
                Toast.makeText(this, "Please enter your email.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        dialogView.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadCredentials() {
        val email = customSharedPreferences.getString("email", "")
        val password = customSharedPreferences.getString("password", "")
        binding.editTextTextEmailAddress.setText(email)
        binding.editTextTextPassword2.setText(password)
        binding.rememberMeCheckBox.isChecked = email!!.isNotEmpty() && password!!.isNotEmpty()
    }

    private fun saveCredentials() {
        val email = binding.editTextTextEmailAddress.text.toString()
        val password = binding.editTextTextPassword2.text.toString()
        if (email.isNotEmpty() && password.isNotEmpty()) {
            customSharedPreferences.saveString("email", email)
            customSharedPreferences.saveString("password", password)
        }
    }

    private fun clearCredentials() {
        customSharedPreferences.remove("email")
        customSharedPreferences.remove("password")
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(applicationContext, ChatActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
