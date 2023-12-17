package com.example.finalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG

class resetPassword : AppCompatActivity() {

    private lateinit var email:EditText
    private lateinit var  submit: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)
        email =findViewById(R.id.emailReset)
        submit=findViewById(R.id.subButton)

        val ema = email.text.toString()

        submit.setOnClickListener {
            val ema = email.text.toString()
            if (ema.isNotEmpty()) {
                // Proceed with Firebase call
                Firebase.auth.sendPasswordResetEmail(ema)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(TAG, "Email sent.")
                            Toast.makeText(this@resetPassword, "Email sent", Toast.LENGTH_LONG).show()
                        } else {
                            // Handle the error case
                            Toast.makeText(this@resetPassword, "Something went wrong", Toast.LENGTH_LONG).show()
                        }
                    }
            } else {
                // Show an error on the email EditText
                email.error = "Email is required"
            }
        }

    }}