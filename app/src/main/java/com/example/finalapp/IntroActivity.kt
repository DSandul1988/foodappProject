package com.example.finalapp

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.finalapp.AdminAct.AdminWelcomePage
import com.example.finalapp.Model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class IntroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro2)
        createNotificationChannel()
            Handler().postDelayed({
                val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

                if (currentUserUid != null) {
                    // Firebase Realtime Database reference
                    val database = FirebaseDatabase.getInstance()
                    val usersRef = database.getReference("users")

                    usersRef.child(currentUserUid).addListenerForSingleValueEvent(object :
                        ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            val userModel = dataSnapshot.getValue(UserModel::class.java)

                            if (userModel != null && userModel.isAdmin) {
                                // Redirect to AdminActivity
                                startActivity(
                                    Intent(
                                        this@IntroActivity,
                                        AdminWelcomePage::class.java
                                    )
                                )
                            } else {
                                // Redirect to MenuActivity
                                startActivity(Intent(this@IntroActivity, MenuActivity::class.java))
                            }
                            finish()
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle possible errors, maybe redirect to login screen or show error message
                        }
                    })
                } else {
                    // If no current user, start MainActivity
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }, 2000)
        }
    private fun createNotificationChannel() {
        // Check for API level 26 and above, as notification channels are not used in lower versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.notChannel) // Human-readable name for the channel
            val descriptionText = getString(R.string.notChannel)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channelId = "444567" // Unique ID for the channel
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    }
