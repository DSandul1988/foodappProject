package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.messaging.FirebaseMessaging

class MenuActivity : AppCompatActivity() {
    private lateinit var logOut:ImageView
    private lateinit var userProf :ImageView
    private  lateinit var  auth: FirebaseAuth
    private lateinit var drinks :ImageView
    private lateinit var foods :ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        logOut=findViewById(R.id.logoutIcon)
userProf=findViewById(R.id.profileIcon)
        auth= FirebaseAuth.getInstance()
        userProf.setOnClickListener{
        val intent = Intent(this@MenuActivity,AccountDetails::class.java)
        startActivity(intent)}

        drinks=findViewById(R.id.drinksDirect)
        drinks.setOnClickListener{
            val intent = Intent(this@MenuActivity,DrinksActivity::class.java)
            startActivity(intent)
        }
foods= findViewById(R.id.foodsDirect)
        foods.setOnClickListener{
            val intent = Intent(this@MenuActivity,FoodActivity::class.java)
            startActivity(intent)
        }
        logOut.setOnClickListener {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase token deletion successful, proceed with sign-out
                    auth.signOut()

                    // Start the MainActivity
                    val intent = Intent(this@MenuActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Handle the error in token deletion, if necessary
                }
            }
        }
    }
}