package com.example.finalapp.AdminAct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.DrinkAdapter

import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType
import com.example.finalapp.R
import com.example.finalapp.foodItemDetails
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class adminActivityDrinks : AppCompatActivity() {
    private lateinit var DrinkReciclerView: RecyclerView
    private lateinit var drinkList: ArrayList<MenuItemModel>
    private lateinit var dbRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_drinks)

            DrinkReciclerView= findViewById(R.id.AdmindrinksReciclerView)
            DrinkReciclerView.layoutManager = LinearLayoutManager(this)
            DrinkReciclerView.setHasFixedSize(false)

            drinkList = arrayListOf<MenuItemModel>()

            getDrinksData()
        }

    private fun getDrinksData() {
        DrinkReciclerView.visibility = View.GONE

        dbRef = FirebaseDatabase.getInstance().getReference("menuItems")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                drinkList.clear()
                if (snapshot.exists()) {
                    for (drinkSnapshot in snapshot.children) {
                        val drinkData = drinkSnapshot.getValue(MenuItemModel::class.java)

                        // Check if the item type is a drink before adding it to the list
                        if (drinkData != null && drinkData.itemType == MenuItemType.DRINK) {
                            drinkList.add(drinkData)
                        }
                    }

                    val adapter = DrinkAdapter(drinkList)
                    DrinkReciclerView.adapter = adapter
                    adapter.setOnItemClickListener(object: DrinkAdapter.onItemClickListener {
                        override fun onItemClick(position: Int) {
                            val intent = Intent(this@adminActivityDrinks, DrinkItemDetails::class.java)
                            intent.putExtra("itemName", drinkList[position].itemName)
                            intent.putExtra("itemDescription", drinkList[position].itemDescription)
                            intent.putExtra("itemPrice", drinkList[position].itemPrice)
                            startActivity(intent)
                        }
                    })
                    DrinkReciclerView.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle potential errors
            }
        })
    }

}