package com.huseyin.chatappp.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.huseyin.chatappp.Adapters.CategoryAdapter
import com.huseyin.chatappp.Models.Category
import com.huseyin.chatappp.R
import java.util.UUID

class CreateCategoryActivity : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var categoryNameEditText: EditText
    private lateinit var createCategoryButton: Button
    private lateinit var recyclerViewCategories: RecyclerView
    private lateinit var recyclerViewAdapter: CategoryAdapter
    private val categoryList = ArrayList<Category>()

    private lateinit var addImageView: ImageView
    private var selectedImageUri: Uri? = null
    private lateinit var storageReference: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_category)

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("Categories")

        categoryNameEditText = findViewById(R.id.categoryNameEditText)
        createCategoryButton = findViewById(R.id.createCategoryButton)
        recyclerViewCategories = findViewById(R.id.recyclerViewCategories)

        recyclerViewAdapter = CategoryAdapter(categoryList) { category ->
            // Handle category click if needed
        }
        recyclerViewCategories.layoutManager = LinearLayoutManager(this)
        recyclerViewCategories.adapter = recyclerViewAdapter

        addImageView = findViewById(R.id.addImageView)
        storageReference = FirebaseStorage.getInstance().reference

        addImageView.setOnClickListener {
            openGalleryForImage()
        }

        createCategoryButton.setOnClickListener {
            val categoryName = categoryNameEditText.text.toString().trim()
            if (categoryName.isNotEmpty() && selectedImageUri != null) {
                uploadImageToFirebaseStorage(categoryName)
            } else {
            }
        }
        fetchCategories()
    }

    private fun fetchCategories() {
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear()
                for (categorySnapshot in snapshot.children) {
                    val categoryName = categorySnapshot.key ?: ""
                    val imageUrl = categorySnapshot.child("image").value?.toString() ?: ""
                    val category = Category(categoryName, imageUrl)
                    categoryList.add(category)
                }
                recyclerViewAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }
        })
    }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        getContent.launch(intent)


    }

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == AppCompatActivity.RESULT_OK) {
            result.data?.data?.let { uri ->
                selectedImageUri = uri
                addImageView.setImageURI(selectedImageUri)
            }
        }
    }

    private fun uploadImageToFirebaseStorage(categoryName: String) {
        val imageName = UUID.randomUUID().toString()
        val imageRef = storageReference.child("images/$imageName")

        selectedImageUri?.let { uri ->
            imageRef.putFile(uri)
                .addOnSuccessListener { taskSnapshot ->
                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        saveImageUrlToDatabase(categoryName, uri.toString())
                    }
                }
                .addOnFailureListener { e ->
                    // Handle errors
                    println("Error uploading image: ${e.message}")
                }
        }
    }

    private fun saveImageUrlToDatabase(categoryName: String, imageUrl: String) {
        val categoryRef = databaseReference.child(categoryName)
        categoryRef.child("image").setValue(imageUrl)
            .addOnSuccessListener {
                categoryNameEditText.text.clear()
                fetchCategories()
            }
            .addOnFailureListener { e ->
                // Handle errors
                println("Error saving image URL: ${e.message}")
            }
    }
}
