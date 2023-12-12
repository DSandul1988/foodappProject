package com.example.finalapp.AdminAct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.FoodAdapter
import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType
import com.example.finalapp.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class adminActivityFood : AppCompatActivity() {
    private lateinit var DrinkReciclerView: RecyclerView
    private lateinit var foodList: ArrayList<MenuItemModel>
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_food)

        DrinkReciclerView= findViewById(R.id.foodAdminReciclerView)
        DrinkReciclerView.layoutManager = LinearLayoutManager(this)
        DrinkReciclerView.setHasFixedSize(false)

        foodList = arrayListOf<MenuItemModel>()

        getFoodData()
    }

    private fun getFoodData(){
        DrinkReciclerView.visibility= View.GONE

        val dbRef = FirebaseDatabase.getInstance().getReference("menuItems")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                foodList.clear()
                if(snapshot.exists()){
                    for(food in snapshot.children){
                        val foodDat = food.getValue(MenuItemModel::class.java)

                        if (foodDat != null && foodDat.itemType == MenuItemType.FOOD){
                        foodList.add(foodDat!!)}
                    }

                    val Dadapter = FoodAdapter(foodList)

                    DrinkReciclerView.adapter =Dadapter
                    Dadapter.setOnItemClickListener(object: FoodAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent= Intent(this@adminActivityFood, adminFoodItemDetails::class.java)
                            intent.putExtra("foodName",foodList[position].itemName)
                            intent.putExtra("foodDescription",foodList[position].itemDescription)
                            intent.putExtra("foodPrice",foodList[position].itemPrice)
                            startActivity(intent)
                        }
                    })
                    DrinkReciclerView.visibility= View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}