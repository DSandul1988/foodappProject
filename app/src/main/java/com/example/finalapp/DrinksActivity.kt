package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.DrinkAdapter

import com.example.finalapp.Model.MenuItemModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class DrinksActivity : AppCompatActivity() {

    private lateinit var DrinkReciclerView: RecyclerView
    private lateinit var drinkList: ArrayList<MenuItemModel>
    private lateinit var cart: ImageView
    private lateinit var logout: ImageView
    private lateinit var profile: ImageView
    private lateinit var home: ImageView
    private  lateinit var  auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drinks)
        logout =findViewById(R.id.logoutIcon)
        home=findViewById(R.id.homeIcon)
        auth= FirebaseAuth.getInstance()
        profile=findViewById(R.id.profileIcon)
        cart =findViewById(R.id.cartIcon)
        DrinkReciclerView= findViewById(R.id.AdmindrinksReciclerView)
        DrinkReciclerView.layoutManager =LinearLayoutManager(this)
        DrinkReciclerView.setHasFixedSize(false)

        drinkList = arrayListOf<MenuItemModel>()
        getDrinksData()

        cart.setOnClickListener{
            val intent =Intent(this@DrinksActivity, CartActivity::class.java)
            startActivity(intent)
        }

        home.setOnClickListener{   val intent =Intent(this@DrinksActivity, MenuActivity::class.java)
            startActivity(intent)}
        profile.setOnClickListener{   val intent =Intent(this@DrinksActivity, AccountDetails::class.java)
            startActivity(intent)}
        logout.setOnClickListener {
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Firebase token deletion successful, proceed with sign-out
                    auth.signOut()

                    // Start the MainActivity
                    val intent = Intent(this@DrinksActivity, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    // Handle the error in token deletion, if necessary
                }
            }
        }




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

                            intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                            startActivity(intent)
                        }
                    })
                    DrinkReciclerView.visibility=View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {

                Log.e("getDrinksData", "Data loading cancelled or failed: ${error.toException()}")
            }})
    }
}
