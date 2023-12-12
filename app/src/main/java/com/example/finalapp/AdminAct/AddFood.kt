package com.example.finalapp.AdminAct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.finalapp.DrinksActivity
import com.example.finalapp.FoodActivity
import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType
import com.example.finalapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddFood : AppCompatActivity() {

    private lateinit var foodName: EditText
    private lateinit var foodDescription: EditText
    private lateinit var foodPrice: EditText
    private lateinit var saveFoodBtn: Button
    private lateinit var seeFoodAll: Button
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_food)

        foodName =findViewById(R.id.foodNameTxt)
        foodDescription = findViewById(R.id.foodDescTxt)
        foodPrice = findViewById(R.id.foodPriceTxt)
        saveFoodBtn = findViewById(R.id.addFoodBtn)
        seeFoodAll= findViewById(R.id.listFoodBtn)

        dbRef = FirebaseDatabase.getInstance().getReference("menuItems")

        saveFoodBtn.setOnClickListener{
            saveFoodData()
        }

        seeFoodAll.setOnClickListener{
            val intent = Intent(this, FoodActivity::class.java)
            startActivity(intent)
        }

    }
    private fun saveFoodData(){
        val dName = foodName.text.toString()
        val dDescription =foodDescription.text.toString()
        val dprice =foodPrice.text.toString()

        if (dName.isEmpty()){
            foodName.error= "Cant be empty"
        }
        if (dDescription.isEmpty()){
            foodDescription.error= "Cant be empty"
        }
        if (dprice.isEmpty()){
            foodPrice.error= "Cant be empty"
        }
        val foodID = dbRef.push().key!!
        val food = MenuItemModel(foodID,dName,dDescription,dprice, itemType = MenuItemType.FOOD)

        dbRef.child(foodID).setValue(food).addOnCompleteListener{
            Toast.makeText(this, "Succes", Toast.LENGTH_LONG).show()
            foodName.text.clear()
            foodDescription.text.clear()
            foodPrice.text.clear()
        }.addOnFailureListener{err ->
            Toast.makeText(this,"Error ${err.message}", Toast.LENGTH_LONG).show()
        }
}

}