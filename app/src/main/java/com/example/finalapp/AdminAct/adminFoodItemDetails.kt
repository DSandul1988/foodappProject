package com.example.finalapp.AdminAct

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.finalapp.R

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
    }
    private fun setItemsToViews() {
        drinkName.text = intent.getStringExtra("itemName")
        drinkDescription.text = intent.getStringExtra("itemDescription")
        drinkPrice.text=intent.getStringExtra("itemPrice")
    }

    }
