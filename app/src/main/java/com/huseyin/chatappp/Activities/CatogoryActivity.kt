package com.huseyin.chatappp.Activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.huseyin.chatappp.Adapters.CategoryAdapter
import com.huseyin.chatappp.Models.Category
import com.huseyin.chatappp.R

class CategoryActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var categoryAdapter: CategoryAdapter
    private val categories = ArrayList<Category>()
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
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
            R.id.options_menu_create_category -> {
                val intent = Intent(applicationContext, CreateCategoryActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_catogory) // activity_category olarak düzeltildi

        recyclerView = findViewById(R.id.recyclerViewCategories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        categoryAdapter = CategoryAdapter(categories) { category ->
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("CATEGORY_NAME", category.name)
            startActivity(intent)
        }
        recyclerView.adapter = categoryAdapter

        database = FirebaseDatabase.getInstance()
        databaseReference = database.getReference("Categories")
        auth = FirebaseAuth.getInstance()

        loadCategories()
    }

    private fun loadCategories() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categories.clear()
                for (ds in snapshot.children) {
                    val categoryName = ds.key ?: ""
                    val imageUrl = ds.child("image").value?.toString() ?: ""
                    val category = Category(categoryName, imageUrl)
                    categories.add(category)
                }
                categoryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                println("Database Error: ${error.message}")
            }
        })
    }
}
