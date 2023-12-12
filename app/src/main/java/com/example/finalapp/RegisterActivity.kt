package com.example.finalapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.example.finalapp.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore


class RegisterActivity : AppCompatActivity() {

    private lateinit var Name: EditText
    private lateinit var Email: EditText
    private lateinit var Adress: EditText
    private lateinit var Password: EditText
    private lateinit var ConfirmPass: EditText
    private lateinit var regButton: Button
    private lateinit var login: TextView
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        Name = findViewById(R.id.regNameTxt)
        Email = findViewById(R.id.regEmailTxt)
        Password = findViewById(R.id.regPasswordTxt)
        Adress=findViewById(R.id.adressTxt)
        ConfirmPass = findViewById(R.id.regConfPasswordTxt)
        regButton = findViewById(R.id.regConfBtn)
        login = findViewById(R.id.loginText)
        auth = FirebaseAuth.getInstance()

        regButton.setOnClickListener {
            val userName = Name.text.toString().trim()
            val email = Email.text.toString().trim()
            val password = Password.text.toString()
            val confirmPassword = ConfirmPass.text.toString()
            val adress =Adress.text.toString()

            // Initialize Firebase Realtime Database Reference
            val database = FirebaseDatabase.getInstance()
            val usersRef = database.getReference("users")

            if (userName.isEmpty()) {
                Name.error = "Please enter your name"
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Email.error = "Please enter email"
                return@setOnClickListener
            }

            if (password.isEmpty() || confirmPassword.isEmpty()) {
                if (password.isEmpty()) Password.error = "Please enter password"
                if (confirmPassword.isEmpty()) ConfirmPass.error = "Please confirm your password"
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this@RegisterActivity, "Passwords do not match", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Firebase Authentication to create a user
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        val isAdmin = email =="admin@gmail.com"
                        val userModel = UserModel(
                            UserId = firebaseUser?.uid,
                            UserName = userName,
                            UserEmail = email,
                            UserAdress = adress,
                            isAdmin = isAdmin ,
                            orderId = null
                        )

                        // Save user data in Firebase Realtime Database
                        firebaseUser?.uid?.let { userId ->
                            usersRef.child(userId).setValue(userModel)
                                .addOnSuccessListener {
                                    Toast.makeText(
                                        baseContext,
                                        "Registration successful",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    startActivity(intent) // Redirect the user to another activity or perform other actions
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(
                                        baseContext,
                                        "Failed to register: ${e.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }
                    } else {
                        Toast.makeText(
                            baseContext,
                            "Registration failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

        }
        }

    }





