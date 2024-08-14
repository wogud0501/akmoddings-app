package com.example.akmoddings

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.akmoddings.R
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var productsRef: CollectionReference

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var productNameEditText: EditText
    private lateinit var productLocEditText: EditText
    private lateinit var availableEditText: EditText
    private lateinit var resetEmailEditText: EditText
    private lateinit var newEmailEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var logoutButton: Button
    private lateinit var addProductButton: Button
    private lateinit var resetPasswordButton: Button
    private lateinit var changeEmailButton: Button
    private lateinit var authStatus: TextView
    private lateinit var loginForm: LinearLayout
    private lateinit var productSection: LinearLayout
    private lateinit var passwordResetSection: LinearLayout
    private lateinit var emailChangeSection: LinearLayout

    private var selectedRowNum: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)

        // Initialize Firebase Auth and Firestore
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()
        productsRef = db.collection("products")

        // Initialize UI elements
        emailEditText = findViewById(R.id.email)
        passwordEditText = findViewById(R.id.password)
        productNameEditText = findViewById(R.id.productName)
        productLocEditText = findViewById(R.id.productLoc)
        availableEditText = findViewById(R.id.available)
        resetEmailEditText = findViewById(R.id.resetEmail)
        newEmailEditText = findViewById(R.id.newEmail)
        loginButton = findViewById(R.id.loginButton)
        logoutButton = findViewById(R.id.logoutButton)
        addProductButton = findViewById(R.id.addProductButton)
        resetPasswordButton = findViewById(R.id.resetPasswordButton)
        changeEmailButton = findViewById(R.id.changeEmailButton)
        authStatus = findViewById(R.id.authStatus)
        loginForm = findViewById(R.id.loginForm)
        productSection = findViewById(R.id.productSection)
        passwordResetSection = findViewById(R.id.passwordResetSection)
        emailChangeSection = findViewById(R.id.emailChangeSection)

        loginButton.setOnClickListener { login() }
        logoutButton.setOnClickListener { logout() }
        addProductButton.setOnClickListener { addProduct() }
        resetPasswordButton.setOnClickListener { resetPassword() }
        changeEmailButton.setOnClickListener { changeEmail() }
    }

    private fun login() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success
                    val user = mAuth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails
                    Toast.makeText(this, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    updateUI(null)
                }
            }
    }

    private fun logout() {
        mAuth.signOut()
        updateUI(null)
    }

    private fun addProduct() {
        val name = productNameEditText.text.toString()
        val location = productLocEditText.text.toString()
        val available = availableEditText.text.toString()

        val product = hashMapOf(
            "name" to name,
            "location" to location,
            "available" to available
        )

        productsRef.add(product)
            .addOnSuccessListener {
                Toast.makeText(this, "Product added.", Toast.LENGTH_SHORT).show()
                productNameEditText.text.clear()
                productLocEditText.text.clear()
                availableEditText.text.clear()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to add product.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun resetPassword() {
        val email = resetEmailEditText.text.toString()

        mAuth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Reset email sent.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to send reset email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun changeEmail() {
        val newEmail = newEmailEditText.text.toString()
        val user = mAuth.currentUser

        user?.updateEmail(newEmail)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Email updated.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Failed to update email.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            loginForm.visibility = View.GONE
            productSection.visibility = View.VISIBLE
            passwordResetSection.visibility = View.GONE
            emailChangeSection.visibility = View.GONE
        } else {
            loginForm.visibility = View.VISIBLE
            productSection.visibility = View.GONE
            passwordResetSection.visibility = View.GONE
            emailChangeSection.visibility = View.GONE
        }
    }
}
