package com.example.finalapp.AdminAct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.example.finalapp.R
import com.google.firebase.database.FirebaseDatabase

class adminFoodItemDetails : AppCompatActivity() {
    private lateinit var drinkName: TextView
    private lateinit var drinkDescription: TextView
    private lateinit var drinkPrice: TextView
    private lateinit var delete: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_food_item_details)

        drinkName = findViewById(R.id.foodNameTxt)
        drinkDescription = findViewById(R.id.foodDescriptionTxt)
        delete = findViewById(R.id.deleteFoodBtn)
        drinkPrice = findViewById(R.id.foodPriceTxt)
        setItemsToViews()
        fun deleteItemFromDatabase(itemId: String?) {
            itemId?.let {
                val databaseReference = FirebaseDatabase.getInstance().getReference("menuItems")
                databaseReference.child(itemId).removeValue().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Task was successful, display a success message
                        Toast.makeText(this@adminFoodItemDetails, "Item successfully deleted", Toast.LENGTH_SHORT).show()

                        val intent = Intent (this@adminFoodItemDetails,AdminWelcomePage::class.java)
                        startActivity(intent)
                    } else {
                        // Task failed, display an error message
                        Toast.makeText(this@adminFoodItemDetails, "Failed to delete item", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        delete.setOnClickListener{deleteItemFromDatabase(itemId = intent.getStringExtra("foodId")).toString()}
    }
    private fun setItemsToViews() {
        drinkName.text = intent.getStringExtra("foodName")
        drinkDescription.text = intent.getStringExtra("foodDescription")
        drinkPrice.text=intent.getStringExtra("foodPrice")
    }

    }
