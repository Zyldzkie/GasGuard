package com.itech.gasguard

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = FirebaseAuth.getInstance()
        database = Firebase.database

        val emailEditText = findViewById<TextInputEditText>(R.id.email)

        val gobackButton = findViewById<FloatingActionButton>(R.id.go_back_bttn)
        val sendButton = findViewById<AppCompatButton>(R.id.verify_bttn)

        sendButton.setOnClickListener {
            val email = emailEditText.text.toString()

            if (email.isEmpty()) {
                emailEditText.error = "Enter email address"
                Toast.makeText(this, "Enter email address", Toast.LENGTH_SHORT).show()
            } else if (!email.matches(emailPattern.toRegex())) {
                emailEditText.error = "Enter valid email address"
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            } else {
                checkIfEmailExists(email)
            }
        }

        gobackButton.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun checkIfEmailExists(email: String) {
        val usersRef = database.getReference("UsersTbl")

        usersRef.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    // Email exists in the database, send password reset email
                    sendPasswordResetEmail(email)
                } else {
                    // Email does not exist in the database
                    Toast.makeText(this@ForgotPasswordActivity, "Email is not registered in the system", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ForgotPasswordActivity, "Failed to check email: ${error.message}", Toast.LENGTH_SHORT).show()
            }

        })
    }

    private fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }
    }
}