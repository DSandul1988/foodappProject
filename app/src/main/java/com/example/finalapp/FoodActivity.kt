package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.FoodAdapter
import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FoodActivity : AppCompatActivity() {
    private lateinit var FoodReciclerView: RecyclerView
    private lateinit var foodList: ArrayList<MenuItemModel>
    private lateinit var dbRef: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food)

        FoodReciclerView= findViewById(R.id.foodReciclerView)
        FoodReciclerView.layoutManager = LinearLayoutManager(this)
        FoodReciclerView.setHasFixedSize(false)
        foodList = arrayListOf<MenuItemModel>()

        getFoodData()

    }

    private fun getFoodData(){
        FoodReciclerView.visibility= View.GONE

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

                    FoodReciclerView.adapter =Dadapter
                    Dadapter.setOnItemClickListener(object: FoodAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent= Intent(this@FoodActivity,food_items_forFood::class.java)
                            intent.putExtra("itemId",foodList[position].itemId)
                            intent.putExtra("itemName",foodList[position].itemName)
                            intent.putExtra("itemDescription",foodList[position].itemDescription)
                            intent.putExtra("itemPrice",foodList[position].itemPrice)
                            startActivity(intent)
                        }
                    })
                    FoodReciclerView.visibility= View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}