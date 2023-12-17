package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.OrderAdapter
import com.example.finalapp.Adapters.PastOrderAdapter
import com.example.finalapp.Model.OrderModel
import com.example.finalapp.Model.PastOrders
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class adminPastOrders : AppCompatActivity() {
    private lateinit var WelcomeReciclerView: RecyclerView
    private lateinit var orderList: ArrayList<PastOrders>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_past_orders)

        WelcomeReciclerView= findViewById(R.id.welcomeRecycler)
        WelcomeReciclerView.layoutManager = LinearLayoutManager(this)
        WelcomeReciclerView.setHasFixedSize(false)
        orderList = arrayListOf<PastOrders>()

        getOrderData()
    }

    private fun getOrderData(){
        WelcomeReciclerView.visibility= View.GONE

        val dbRef = FirebaseDatabase.getInstance().getReference("PastOrders")
        dbRef.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                if(snapshot.exists()){
                    for(order in snapshot.children){
                        val orderDat = order.getValue(PastOrders::class.java)
                        orderList.add(orderDat!!)
                    }

                    val Dadapter = PastOrderAdapter(orderList)

                    WelcomeReciclerView.adapter =Dadapter
                    Dadapter.setOnItemClickListener(object: PastOrderAdapter.onItemClickListener{
                        override fun onItemClick(position: Int) {
                            val intent= Intent(this@adminPastOrders, pastOrderDetails::class.java)
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