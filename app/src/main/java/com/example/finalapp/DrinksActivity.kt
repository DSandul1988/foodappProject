package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.DrinkAdapter

import com.example.finalapp.Model.MenuItemModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class DrinksActivity : AppCompatActivity() {

    private lateinit var DrinkReciclerView: RecyclerView
    private lateinit var drinkList: ArrayList<MenuItemModel>
    private lateinit var dbRef:DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drinks)

        DrinkReciclerView= findViewById(R.id.AdmindrinksReciclerView)
        DrinkReciclerView.layoutManager =LinearLayoutManager(this)
        DrinkReciclerView.setHasFixedSize(false)

        drinkList = arrayListOf<MenuItemModel>()

        getDrinksData()
    }

    private fun getDrinksData(){
        DrinkReciclerView.visibility= View.GONE

        val dbRef =FirebaseDatabase.getInstance().getReference("menuItems")
        dbRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
               drinkList.clear()
                if(snapshot.exists()){
                    for(drink in snapshot.children){
                        val drinkDat = drink.getValue(MenuItemModel::class.java)
                        drinkList.add(drinkDat!!)
                    }

                    val Dadapter = DrinkAdapter(drinkList)

                    DrinkReciclerView.adapter =Dadapter
                    Dadapter.setOnItemClickListener(object:DrinkAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent= Intent(this@DrinksActivity,foodItemDetails::class.java)
                            intent.putExtra("itemId",drinkList[position].itemId)
                            intent.putExtra("itemName",drinkList[position].itemName)
                            intent.putExtra("itemDescription",drinkList[position].itemDescription)
                            intent.putExtra("itemPrice",drinkList[position].itemPrice)
                            startActivity(intent)
                        }
                    })
                    DrinkReciclerView.visibility=View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}