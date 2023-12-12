package com.example.finalapp.AdminAct

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.OrderAdapter
import com.example.finalapp.MainActivity
import com.example.finalapp.Model.OrderModel
import com.example.finalapp.R
import com.example.finalapp.viewOrderDetails
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging

class AdminWelcomePage : AppCompatActivity() {
    private lateinit var WelcomeReciclerView: RecyclerView
    private lateinit var orderList: ArrayList<OrderModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var addDrinks: Button
    private lateinit var logOut: Button
    private lateinit var addFood: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_welcome_page)

        WelcomeReciclerView= findViewById(R.id.welcomeRecycler)
        WelcomeReciclerView.layoutManager = LinearLayoutManager(this)
        WelcomeReciclerView.setHasFixedSize(false)
        orderList = arrayListOf<OrderModel>()
    addDrinks=findViewById(R.id.WelcomeDrnkBtn)
        addFood=findViewById(R.id.WelcomeFoodBtn)
        logOut =findViewById(R.id.WelcomeLogOutBtn)
        getOrderData()
addDrinks.setOnClickListener{
    val intent = Intent(this, AddDrinks::class.java)
    startActivity(intent)

    addFood.setOnClickListener{
        val intent = Intent(this@AdminWelcomePage, AddFood::class.java)
        startActivity(intent)}
}
        logOut.setOnClickListener {
            // Delete the FCM token
            FirebaseMessaging.getInstance().deleteToken().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Successfully deleted the token
                    Log.d("Logout", "FCM token deleted successfully.")
                } else {
                    // Handle the failure of token deletion
                    Log.e("Logout", "Failed to delete FCM token.", task.exception)
                }

                // Proceed to sign out from FirebaseAuth
                val auth: FirebaseAuth = FirebaseAuth.getInstance()
                auth.signOut()

                // Redirect to MainActivity
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

    }
    private fun getOrderData(){
        WelcomeReciclerView.visibility= View.GONE

        val dbRef = FirebaseDatabase.getInstance().getReference("orders")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                if(snapshot.exists()){
                    for(order in snapshot.children){
                        val orderDat = order.getValue(OrderModel::class.java)
                        orderList.add(orderDat!!)
                    }

                    val Dadapter = OrderAdapter(orderList)

                    WelcomeReciclerView.adapter =Dadapter
                    Dadapter.setOnItemClickListener(object: OrderAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent= Intent(this@AdminWelcomePage, viewOrderDetails::class.java)
                            intent.putExtra("orderId",orderList[position].orderId)
                            intent.putExtra("userId",orderList[position].userId)
                            intent.putExtra("foods",orderList[position].items.toString())



                            startActivity(intent)
                        }
                    })
                    WelcomeReciclerView.visibility= View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}