package com.example.finalapp.AdminAct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import com.example.finalapp.R
import com.google.firebase.database.FirebaseDatabase

class DrinkItemDetails : AppCompatActivity() {
    private lateinit var drinkName: TextView
    private lateinit var drinkDescription: TextView
    private lateinit var drinkPrice: TextView
    private lateinit var delete: Button
    private lateinit var dbRef : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drink_item_details)


        fun deleteOrderFromDatabase(itemId: String?) {
            itemId?.let {
                val databaseReference = FirebaseDatabase.getInstance().getReference("menuItems")
                databaseReference.child(itemId).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Task was successful, display a success message
                        Toast.makeText(this@DrinkItemDetails, "Item successfully deleted", Toast.LENGTH_SHORT).show()

                        val intent = Intent (this@DrinkItemDetails,AdminWelcomePage::class.java)
                        startActivity(intent)
                    } else {
                        // Task failed, display an error message
                        Toast.makeText(this@DrinkItemDetails, "Failed to delete item", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        drinkName = findViewById(R.id.drinkNameTxt)
        drinkDescription = findViewById(R.id.drinkDescriptionTxt)
        delete = findViewById(R.id.deleteBtn)
        drinkPrice = findViewById(R.id.drinkPriceTxt)
        setItemsToViews()


       delete.setOnClickListener{deleteOrderFromDatabase(itemId = intent.getStringExtra("itemId")).toString()}
    }

    private fun setItemsToViews() {
        drinkName.text = intent.getStringExtra("itemName")
        drinkDescription.text = intent.getStringExtra("itemDescription")
        drinkPrice.text=intent.getStringExtra("itemPrice")
    }
}