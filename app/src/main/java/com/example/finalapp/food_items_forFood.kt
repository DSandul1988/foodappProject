package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Adapters.FeedbackAdapter
import com.example.finalapp.Model.FeedBackModel

import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType
import com.example.finalapp.Model.OrderModel
import com.example.finalapp.Model.UserModel
import com.example.finalapp.utils.CartStorage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.UUID

class food_items_forFood : AppCompatActivity() {
    private lateinit var itemName: TextView
    private lateinit var feedbackButton: Button
    private lateinit var feedbackEditText: EditText
    private lateinit var feedBack: RecyclerView
    private lateinit var itemPrice: TextView
    private lateinit var itemDescription: TextView
    private lateinit var addToCart: Button
    private lateinit var checkout: Button
    private lateinit var back: Button
    private lateinit var dbRef : FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_items_for_food)
        checkout = findViewById(R.id.checkoutBtn)
        feedBack=findViewById(R.id.feedbackRecycler)
        feedbackButton =findViewById(R.id.feedBackBtn)
        feedbackEditText =findViewById(R.id.feedbackText)


        checkout.setOnClickListener{


            // Create a new intent to start CartActivity
            val intent = Intent(this@food_items_forFood, CartActivity::class.java)

            startActivity(intent)}
        back=findViewById(R.id.backBtn)
        back.setOnClickListener{
            val intent = Intent(this@food_items_forFood,FoodActivity::class.java)
            startActivity(intent) }
        itemName = findViewById(R.id.fooditemNameTxt)
        itemDescription = findViewById(R.id.foodItemDescriptionTxt)
        itemPrice =findViewById(R.id.foodItemPricetxt)
        addToCart = findViewById(R.id.addToCartBtn)
        setItemsToViews()
        val feedbackAdapter = FeedbackAdapter(emptyList())
        feedBack.adapter = feedbackAdapter
        feedBack.layoutManager = LinearLayoutManager(this)

        feedbackButton.setOnClickListener {
            val feedbackText = feedbackEditText.text.toString()
            val userId = FirebaseAuth.getInstance().currentUser?.uid
            val itemId = intent.getStringExtra("itemId")

            fetchUserName(userId) { userName ->
                val feedback = FeedBackModel(
                    itemId = itemId,
                    userId = userId,
                    userName = userName,
                    feedbackText = feedbackText
                )

                val feedbackRef = itemId?.let {
                    FirebaseDatabase.getInstance().getReference("Feedback").child(it)
                }

                feedbackRef?.push()?.setValue(feedback)?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Feedback submitted", Toast.LENGTH_SHORT).show()
                        feedbackEditText.text.clear()
                    } else {
                        Toast.makeText(this, "Failed to submit feedback", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        val itemId = intent.getStringExtra("itemId")
        val feedbackRef = itemId?.let {
            FirebaseDatabase.getInstance().getReference("Feedback").child(it)
        }

        feedbackRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val feedbacks = mutableListOf<FeedBackModel>()
                snapshot.children.forEach { child ->
                    val feedback = child.getValue(FeedBackModel::class.java)
                    feedback?.let { feedbacks.add(it) }
                }
                feedbackAdapter.updateFeedbackList(feedbacks) // Ensure your adapter is expecting FeedbackModel
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@food_items_forFood, "Failed to load feedback", Toast.LENGTH_SHORT).show()
            }
        })






        addToCart.setOnClickListener {
            val itemNameStr = intent.getStringExtra("itemName")
            val itemDescriptionStr = intent.getStringExtra("itemDescription")
            val itemPriceStr = intent.getStringExtra("itemPrice")
            if (!itemNameStr.isNullOrEmpty() && !itemDescriptionStr.isNullOrEmpty() && !itemPriceStr.isNullOrEmpty()) {
                val item = MenuItemModel(
                    itemName = itemNameStr,
                    itemDescription = itemDescriptionStr,
                    itemPrice = itemPriceStr,
                    itemType = MenuItemType.FOOD// or dynamically set this based on the context
                )
                CartStorage.cartItems.add(item)


                Toast.makeText(this, "$itemNameStr added to cart", Toast.LENGTH_SHORT).show()



            }else{
                Toast.makeText(this, "Item details are missing", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun setItemsToViews() {
        itemName.text = intent.getStringExtra("itemName")
        itemDescription.text = intent.getStringExtra("itemDescription")
        itemPrice.text= intent.getStringExtra("itemPrice")
    }

private fun fetchUserName(userId: String?, onComplete: (String) -> Unit) {
    if (userId == null) {
        onComplete("Anonymous")
        return
    }

    val usersRef = FirebaseDatabase.getInstance().getReference("users").child(userId)
    usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val user = snapshot.getValue(UserModel::class.java)
            onComplete(user?.UserName ?: "Anonymous")
        }

        override fun onCancelled(databaseError: DatabaseError) {
            onComplete("Anonymous")
        }
    })
}
    fun calculateTotalPrice(): Double {
        var total = 0.0
        for (item in CartStorage.cartItems) {
            try {
                // Add the item price to the total, use 0.0 as default if itemPrice is null
                total += item.itemPrice?.toDouble() ?: 0.0
            } catch (e: NumberFormatException) {
                println("Invalid price format for item: ${item.itemPrice}")
            }
        }
        return total
    }
}