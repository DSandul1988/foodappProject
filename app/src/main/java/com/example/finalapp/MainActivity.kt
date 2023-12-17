package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.finalapp.AdminAct.AdminWelcomePage
import com.example.finalapp.Model.UserModel
import com.example.finalapp.utils.FirebaseUtils
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.Constants.MessageNotificationKeys.TAG

import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity() {

    private lateinit var Email: EditText
    private lateinit var Password: EditText
    private lateinit var loginButton: Button
    private lateinit var registerLink: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var FirebaseUtil:FirebaseUtils
    private lateinit var resetEmail:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.i(TAG, "Fetching FCM registration token failed", task.exception)
//                return@OnCompleteListener
//            }
//
//            // Get new FCM registration token
//            val token = task.result
//
//            // Log and toast
//            val msg =  token
//            Log.i("TOKEN", msg)
//            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
//        })
        Email = findViewById(R.id.emailLogTXT)
        Password = findViewById(R.id.passwordLogTXT)
        loginButton = findViewById(R.id.LogInbtn)
        auth = FirebaseAuth.getInstance()
        registerLink = findViewById(R.id.regTxT)
        resetEmail =findViewById(R.id.resetPass)
        registerLink.setOnClickListener {
            val intent = Intent(this@MainActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        resetEmail.setOnClickListener{
            val intent =Intent(this, resetPassword::class.java)
            startActivity(intent)
        }
        loginButton.setOnClickListener {
            val email = Email.text.toString().trim()
            val password = Password.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                if (email.isEmpty()) Email.error = "Please enter email"
                if (password.isEmpty()) Password.error = "Please enter password"
                return@setOnClickListener
            }

            // Authenticate the user
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {

                      updateCurrentUserFCMToken()
                        // User is successfully logged in
                        val firebaseUser = auth.currentUser

                        firebaseUser?.uid?.let { userId ->
                            // Retrieve user data from Firebase Realtime Database
                            val database = FirebaseDatabase.getInstance()
                            val usersRef = database.getReference("users")

                            usersRef.child(userId).addListenerForSingleValueEvent(object :
                                ValueEventListener {
                                override fun onDataChange(dataSnapshot: DataSnapshot) {
                                    val userModel = dataSnapshot.getValue(UserModel::class.java)

                                    // Navigate based on isAdmin status
                                    if (userModel != null) {
                                        if ( userModel.isAdmin) {
                                            // Navigate to WelcomeAdminActivity
                                            val intent = Intent(this@MainActivity, AdminWelcomePage::class.java)
                                            startActivity(intent)
                                        } else {
                                            // Navigate to MenuActivity
                                            val intent = Intent(this@MainActivity, MenuActivity::class.java)
                                            startActivity(intent)
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle possible errors
                                }
                            })
                        }
                    } else {
                        // Authentication failed
                        Toast.makeText(baseContext, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }


    }

    private fun updateCurrentUserFCMToken() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    task.result?.let { token ->
                        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
                        databaseReference.child(userId).addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val user = snapshot.getValue(UserModel::class.java)
                                user?.FCMToken = token
                                snapshot.ref.setValue(user)
                                    .addOnSuccessListener {
                                        Log.i("Token:", token, task.exception)  // Handle successful update
                                    }
                                    .addOnFailureListener {
                                        // Handle failed update
                                    }
                            }

                            override fun onCancelled(databaseError: DatabaseError) {
                                // Handle possible errors
                            }
                        })
                    }
                } else {
                    // Optionally handle the failure to retrieve the token
                }
            }
        } else {
            // Handle the case where there is no logged-in user
        }
    }
private fun getToken (){
    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
        if (!task.isSuccessful) {
            Log.i(TAG, "Fetching FCM registration token failed", task.exception)
            return@OnCompleteListener
        }

        // Get new FCM registration token
        val token = task.result

        // Log and toast
        val msg =  token
        Log.e("My token", msg)
        Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
    })
}
}

