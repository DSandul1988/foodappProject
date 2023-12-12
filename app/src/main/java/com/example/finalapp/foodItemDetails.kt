package com.example.finalapp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
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
import com.example.finalapp.Model.UserModel
import com.example.finalapp.utils.CartStorage
import com.google.firebase.auth.FirebaseAuth

import com.google.firebase.database.FirebaseDatabase

class foodItemDetails : AppCompatActivity() {

    private lateinit var itemName: TextView
    private lateinit var feedbackEditText: EditText
    private lateinit var feedBack: RecyclerView
    private lateinit var itemPrice: TextView
    private lateinit var itemDescription: TextView
    private lateinit var addToOrder: Button
    private lateinit var checkout: Button
    private lateinit var feedbackButton: Button
    private lateinit var back: Button
    private lateinit var dbRef : FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_item_details)
        feedBack=findViewById(R.id.feedbackRecycler)
        feedbackButton =findViewById(R.id.feedBackBtn)
        checkout = findViewById(R.id.checkoutBtn)
        feedbackEditText =findViewById(R.id.feedbackText)

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
                Toast.makeText(this@foodItemDetails, "Failed to load feedback", Toast.LENGTH_SHORT).show()
            }
        })
        checkout.setOnClickListener {
            val intent = Intent(this@foodItemDetails, CartActivity::class.java)
            startActivity(intent)
        }

        back = findViewById(R.id.backBtn)
        back.setOnClickListener {
            val intent = Intent(this@foodItemDetails, DrinksActivity::class.java)
            startActivity(intent)
        }

        itemName = findViewById(R.id.drinkitemNameTxt)
        itemDescription = findViewById(R.id.drinkItemDescriptionTxt)
        itemPrice = findViewById(R.id.drinkItemPriceTxt)
        addToOrder = findViewById(R.id.addToCartBtn)
        setItemsToViews()

        addToOrder.setOnClickListener {
            val itemNameStr = intent.getStringExtra("itemName")
            val itemDescriptionStr = intent.getStringExtra("itemDescription")
            val itemPriceStr = intent.getStringExtra("itemPrice")

            if (!itemNameStr.isNullOrEmpty() && !itemDescriptionStr.isNullOrEmpty() && !itemPriceStr.isNullOrEmpty()) {
                val item = MenuItemModel(
                    itemName = itemNameStr,
                    itemDescription = itemDescriptionStr,
                    itemPrice = itemPriceStr,
                    itemType = MenuItemType.DRINK// or dynamically set this based on the context
                )
                CartStorage.cartItems.add(item)
                Toast.makeText(this, "$itemNameStr added to cart", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Item details are missing", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun setItemsToViews() {
        itemName.text = intent.getStringExtra("itemName")
        itemDescription.text = intent.getStringExtra("itemDescription")
        itemPrice.text = intent.getStringExtra("itemPrice")
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
    }}