// src/main/java/com/example/basededatos/MainActivity.kt
package com.example.basededatos

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var addProductButton: Button
    private lateinit var queryProductsButton: Button
    private lateinit var outputTextView: TextView
    private lateinit var productNameEditText: EditText
    private lateinit var productPriceEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Firebase Realtime Database reference
        database = FirebaseDatabase.getInstance().reference

        addProductButton = findViewById(R.id.addProductButton)
        queryProductsButton = findViewById(R.id.queryProductsButton)
        outputTextView = findViewById(R.id.outputTextView)
        productNameEditText = findViewById(R.id.productNameEditText)
        productPriceEditText = findViewById(R.id.productPriceEditText)

        addProductButton.setOnClickListener {
            addProduct()
        }

        queryProductsButton.setOnClickListener {
            queryProducts()
        }
    }

    private fun addProduct() {
        val productName = productNameEditText.text.toString()
        val productPriceText = productPriceEditText.text.toString()

        if (productName.isNotEmpty() && productPriceText.isNotEmpty()) {
            val productPrice = productPriceText.toDouble()
            val productId = database.child("products").push().key
            val product = mapOf(
                "name" to productName,
                "price" to productPrice
            )

            if (productId != null) {
                database.child("products").child(productId).setValue(product)
                    .addOnSuccessListener {
                        outputTextView.text = "Product added with ID: $productId"
                        productNameEditText.text.clear()
                        productPriceEditText.text.clear()
                    }
                    .addOnFailureListener { e ->
                        outputTextView.text = "Error adding product: $e"
                    }
            }
        } else {
            outputTextView.text = "Please enter both name and price"
        }
    }

    private fun queryProducts() {
        database.child("products").get()
            .addOnSuccessListener { snapshot ->
                val products = snapshot.children.joinToString("\n") { child ->
                    "${child.key}: ${child.value}"
                }
                outputTextView.text = products
            }
            .addOnFailureListener { e ->
                outputTextView.text = "Error fetching products: $e"
            }
    }
}
