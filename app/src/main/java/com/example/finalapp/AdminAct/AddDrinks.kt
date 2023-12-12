package com.example.finalapp.AdminAct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.finalapp.DrinksActivity

import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType
import com.example.finalapp.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AddDrinks : AppCompatActivity() {

     private lateinit var drinkName:EditText
    private lateinit var drinkPrice:EditText
     private lateinit var drinkDescription:EditText
     private lateinit var saveBtn: Button
     private lateinit var seeAll: Button
     private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_drinks)
        drinkPrice =findViewById(R.id.drinkPrice)
        drinkName=findViewById(R.id.addDrinkNameTxt)
        drinkDescription=findViewById(R.id.addDrinkDescriptionTxt)
        saveBtn=findViewById(R.id.saveDrinksbtn)
        seeAll=findViewById(R.id.listDrinksBtn)

        dbRef =FirebaseDatabase.getInstance().getReference("menuItems")

        saveBtn.setOnClickListener{
            saveDrinksData()
        }

        seeAll.setOnClickListener{
            val intent = Intent(this, adminActivityDrinks::class.java)
            startActivity(intent)
        }
    }

    private fun saveDrinksData(){
        val dName = drinkName.text.toString()
        val dDescription =drinkDescription.text.toString()
        val drinkP =drinkPrice.text.toString()
        if (dName.isEmpty()){
            drinkName.error= "Cant be empty"
        }

        if (dDescription.isEmpty()){
            drinkDescription.error= "Cant be empty"
        }
        if (drinkP.isEmpty()){
            drinkPrice.error= "Cant be empty"
        }
        val drinkID = dbRef.push().key!!
        val drink = MenuItemModel(drinkID,dName,dDescription,drinkP, itemType = MenuItemType.DRINK)

        dbRef.child(drinkID).setValue(drink).addOnCompleteListener{
            Toast.makeText(this, "Succes",Toast.LENGTH_LONG).show()
            drinkName.text.clear()
            drinkDescription.text.clear()
            drinkPrice.text.clear()
        }.addOnFailureListener{err ->
            Toast.makeText(this,"Error ${err.message}",Toast.LENGTH_LONG).show()
        }
    }
}