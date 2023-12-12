package com.example.finalapp.AdminAct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

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

        drinkName = findViewById(R.id.drinkNameTxt)
        drinkDescription = findViewById(R.id.drinkDescriptionTxt)
        delete = findViewById(R.id.deleteBtn)
        drinkPrice = findViewById(R.id.drinkPriceTxt)
        setItemsToViews()
    }

    private fun setItemsToViews() {
        drinkName.text = intent.getStringExtra("itemName")
        drinkDescription.text = intent.getStringExtra("itemDescription")
        drinkPrice.text=intent.getStringExtra("itemPrice")
    }
}