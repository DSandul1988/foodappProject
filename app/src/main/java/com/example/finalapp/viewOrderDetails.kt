package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import okhttp3.MediaType
import okhttp3.OkHttpClient
import android.widget.Button
import android.widget.TextView
import com.example.finalapp.AdminAct.AdminWelcomePage
import com.example.finalapp.Model.ChatMessageModel
import com.example.finalapp.Model.ChatRoom
import com.example.finalapp.Model.OrderModel
import com.example.finalapp.Model.UserModel
import com.example.finalapp.utils.FirebaseUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okio.IOException
import org.json.JSONObject


class viewOrderDetails : AppCompatActivity() {

    private lateinit var orderIDtxt :TextView
    private lateinit var userIDtxt :TextView
    private lateinit var foodstxt :TextView
    private lateinit var drinkstxt :TextView
    private lateinit var AcceptBtn : Button
    private lateinit var DeclineBtn : Button
    private lateinit var ReadyBtn : Button
    private lateinit var viewPast : Button
    private lateinit var ChatRoomId:String
    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var user2ID: String
    private lateinit var user1ID: String
    private lateinit var chatR:ChatRoom

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_order_details)
viewPast=findViewById(R.id.viewPast)
        firebaseUtils = FirebaseUtils()
        orderIDtxt =findViewById(R.id.orderIdTxt)
        userIDtxt =findViewById(R.id.userIdTxt)
        foodstxt =findViewById(R.id.foodItemsTxt)
        drinkstxt =findViewById(R.id.foodItemsTxt)
        AcceptBtn =findViewById(R.id.acceptBtn)
        DeclineBtn =findViewById(R.id.declineBtn)
        ReadyBtn =findViewById(R.id.readyBtn)



        val currentUser = FirebaseAuth.getInstance().currentUser
        user1ID = currentUser?.uid ?: "Unknown"
        user2ID = intent.getStringExtra("userId") ?: "Unknown"
        ChatRoomId = firebaseUtils.getChatRoomId(user1ID, user2ID)
        orderIDtxt.text = intent.getStringExtra("orderId") ?: "Unknown"
        userIDtxt.text = intent.getStringExtra("userId") ?: "Unknown"
        foodstxt.text = intent.getStringExtra("foods") ?: "Unknown"
        getCreateChatRoom()
        viewPast.setOnClickListener{
    val intent =Intent(this@viewOrderDetails,adminPastOrders::class.java)
    startActivity(intent)
}
        AcceptBtn.setOnClickListener{
            val message= "Order accepted"
            //sendMessageToUser(message)
            Log.e("sendNotification", "started")
            sendNotification(message)
        }

        DeclineBtn.setOnClickListener{
            val message= "Order declined"
         //   sendMessageToUser(message)
            sendNotification(message)
        }
        fun moveToPastOrders(orderId: String) {
            val database = FirebaseDatabase.getInstance()
            val currentOrdersRef = database.getReference("orders")
            val pastOrdersRef = database.getReference("PastOrders")
            currentOrdersRef.child(orderId).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val order = dataSnapshot.getValue(OrderModel::class.java)
                    dataSnapshot.ref.removeValue() // Remove from currentOrders

                    // Add to pastOrders
                    order?.let {
                        pastOrdersRef.child(orderId).setValue(it)
                    }

                    val intent = Intent(this@viewOrderDetails, AdminWelcomePage::class.java)
                    startActivity(intent)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle possible errors
                }
            })
        }



        ReadyBtn.setOnClickListener{
            val message= "Order ready please collect "
         //   sendMessageToUser(message)
            sendNotification(message)
            val orderId = intent.getStringExtra("orderId")  // Obtain this from your UI or order object
            if (orderId != null) {
                moveToPastOrders(orderId)
            }
        }


    }

    private fun getCreateChatRoom() {
        firebaseUtils.getChatRoom(ChatRoomId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    // Retrieve and assign the existing chat room data
                    val chatR = dataSnapshot.getValue(ChatRoom::class.java)
                    // Use chatR for further operations
                } else {
                    // Create a new chat room model
                    val newChatRoom = ChatRoom(
                        chatRoomId = ChatRoomId,
                        usersId = listOf( user1ID, user2ID), // Initialize with empty or appropriate user IDs
                        // Set the current timestamp
                    )

                    // Save the new chat room to the database
                    firebaseUtils.getChatRoom(ChatRoomId).setValue(newChatRoom)
                        .addOnSuccessListener {
                            // Handle success, e.g., notify the user or update UI
                        }
                        .addOnFailureListener {
                            // Handle failure, e.g., display error message
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error, e.g., display error message
            }
        })
    }



private fun sendMessageToUser(message:String){
    val chatMessage = ChatMessageModel(
        message = message,
        senderID = user1ID ,
       // Gets the current timestamp
    )
    val messageRef = firebaseUtils.getChatRoomMessageReference(ChatRoomId )

    messageRef.push().setValue(chatMessage)
        .addOnSuccessListener {
            // Handle success, e.g., message sent successfully
        }
        .addOnFailureListener {
            // Handle failure, e.g., error in sending message
        }
}

    private fun sendNotification(message: String) {
        Log.e("sendNotification", "Starting sendNotification function")
        val databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val userId = user1ID // Replace with the actual user ID
        val otherUserId = user2ID // Replace with the actual other user ID

        Log.d("sendNotification", "Fetching data for current user ID: $userId")

        // Fetch the current user's data
        databaseReference.child(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.e("sendNotification", "Successfully fetched data for current user ID: $userId")
                val currentUser = task.result?.getValue(UserModel::class.java)

                Log.e("sendNotification", "Fetching data for other user ID: $otherUserId")

                // Fetch the other user's data
                databaseReference.child(otherUserId).get().addOnCompleteListener { otherTask ->
                    if (otherTask.isSuccessful) {
                        Log.e("sendNotification", "Successfully fetched data for other user ID: $otherUserId")
                        val otherUser = otherTask.result?.getValue(UserModel::class.java)

                        currentUser?.let { currentUserData ->
                            otherUser?.let { otherUserData ->
                                try {
                                    Log.e("sendNotification", "Preparing notification payload for user ID: $otherUserId")

                                    val jsonObject = JSONObject()
                                    val notificationObj = JSONObject().apply {
                                        put("title", currentUserData.UserName)
                                        put("body", message)
                                    }

                                    val dataObj = JSONObject().apply {
                                        put("userId", currentUserData.orderId)
                                    }

                                    jsonObject.apply {
                                        put("notification", notificationObj)
                                        put("data", dataObj)
                                        put("to", otherUserData.FCMToken) // Use the other user's FCM token
                                    }

                                    callAPI(jsonObject)
                                    Log.e("sendNotification", "Notification payload sent to API")
                                    otherUserData.FCMToken?.let { Log.e("sendNotification", it) }
                                } catch (e: Exception) {
                                    Log.e("sendNotification", "Exception in sending notification", e)
                                }
                            } ?: run {
                                Log.e("sendNotification", "Failed to fetch data for other user ID: $otherUserId - User data is null")
                            }
                        } ?: run {
                            Log.e("sendNotification", "Failed to fetch data for current user ID: $userId - User data is null")
                        }
                    } else {
                        Log.e("sendNotification", "Failed to fetch data for other user ID: $otherUserId")
                    }
                }
            } else {
                Log.e("sendNotification", "Failed to fetch data for current user ID: $userId")
            }
        }
    }



    private fun callAPI(jsonObject: JSONObject) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"
        val body: RequestBody = jsonObject.toString().toRequestBody(JSON)

        // Debugging: Log the JSON payload
        Log.e("sendNotification", "Sending notification with payload: ${jsonObject.toString()}")

        val serverKey = "key=AAAAI2hE_K4:APA91bFS4GsK6zL7cRNCZZb0uXXXRhGABGcV5-UNAr70Q8LxH6jL77Jjo3Ec-Q7hBufQSsvUXazjwcJnrggA2jvtq2D45jkmMWvZoq_iRI-yj5PNchIBupoOLgnYP30h6WJCu6r9OktO" // Replace with your actual server key
        // Debugging: Log part of the server key
        Log.e("sendNotification", "Using server key: ${serverKey.take(10)}...")

        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization", serverKey)
            .build()

        // Perform the network request asynchronously
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                // Log the exception with the stack trace
                Log.e("sendNotification", "Network call failed", e)
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    Log.e("sendNotification", "Notification sent successfully")
                } else {
                    val responseBody = response.body?.string()
                    Log.e("sendNotification", "Failed to send notification, server responded with status: ${response.code}, response: $responseBody")
                }
            }
        })
    }

}
