package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.finalapp.AdminAct.AdminWelcomePage
import com.google.firebase.database.FirebaseDatabase

class pastOrderDetails : AppCompatActivity() {

    private lateinit var delete:Button
    private lateinit var orderIDtxt : TextView
    private lateinit var foodstxt : TextView
    private lateinit var drinkstxt : TextView
    private lateinit var userIDtxt : TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_past_order_details)
        userIDtxt =findViewById(R.id.userIdTxt)
        foodstxt =findViewById(R.id.foodItemsTxt)
        drinkstxt =findViewById(R.id.foodItemsTxt)
        orderIDtxt=findViewById(R.id.orderIdTxt)
        delete=findViewById(R.id.declineBtn)
        orderIDtxt.text = intent.getStringExtra("orderId") ?: "Unknown"
        userIDtxt.text = intent.getStringExtra("userId") ?: "Unknown"
        foodstxt.text = intent.getStringExtra("foods") ?: "Unknown"
        val orderI = orderIDtxt.text.toString()
        delete.setOnClickListener{
            deleteOrderFromDatabase(orderI)
        }
    }

    fun deleteOrderFromDatabase(orderId: String?) {
        orderId?.let {
            val databaseReference = FirebaseDatabase.getInstance().getReference("pastOrders")
            databaseReference.child(orderId).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Task was successful, display a success message
                    Toast.makeText(this@pastOrderDetails, "Order successfully deleted", Toast.LENGTH_SHORT).show()

                    val intent = Intent (this@pastOrderDetails,AdminWelcomePage::class.java)
                    startActivity(intent)
                } else {
                    // Task failed, display an error message
                    Toast.makeText(this@pastOrderDetails, "Failed to delete order", Toast.LENGTH_SHORT).show()
                }
            }
        }
}}