package com.example.finalapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.MediaType
import okhttp3.OkHttpClient
import android.widget.Button
import android.widget.TextView
import com.example.finalapp.Model.ChatMessageModel
import com.example.finalapp.Model.ChatRoom
import com.example.finalapp.Model.UserModel
import com.example.finalapp.utils.FirebaseUtils
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class viewOrderDetails : AppCompatActivity() {

    private lateinit var orderIDtxt :TextView
    private lateinit var userIDtxt :TextView
    private lateinit var foodstxt :TextView
    private lateinit var drinkstxt :TextView
    private lateinit var AcceptBtn : Button
    private lateinit var DeclineBtn : Button
    private lateinit var ReadyBtn : Button
    private lateinit var ChatRoomId:String
    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var user2ID: String
    private lateinit var user1ID: String
    private lateinit var chatR:ChatRoom
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_order_details)

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
        drinkstxt.text = intent.getStringExtra("drinks") ?: "Unknown"
        getCreateChatRoom()

        AcceptBtn.setOnClickListener{
            val message= "Order accepted"
            sendMessageToUser(message)
            sendNotification(message)
        }

        DeclineBtn.setOnClickListener{
            val message= "Order declined"
            sendMessageToUser(message)
            sendNotification(message)
        }

        ReadyBtn.setOnClickListener{
            val message= "Order ready please collect "
            sendMessageToUser(message)
            sendNotification(message)
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
        val databaseReference = FirebaseDatabase.getInstance().getReference("users") // Adjust path as needed
        val userId = user1ID // Replace with the actual user ID
        val otherUserId = user2ID // Replace with the actual other user ID

        // Fetch the current user's data
        databaseReference.child(userId).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = task.result?.getValue(UserModel::class.java)

                // Fetch the other user's data
                databaseReference.child(otherUserId).get().addOnCompleteListener { otherTask ->
                    if (otherTask.isSuccessful) {
                        val otherUser = otherTask.result?.getValue(UserModel::class.java)

                        currentUser?.let { currentUserData ->
                            otherUser?.let { otherUserData ->
                                try {
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
                                } catch (e: Exception) {
                                    // Handle exception
                                }
                            } ?: run {
                                // Handle the case where other user data is not found
                            }
                        } ?: run {
                            // Handle the case where current user data is not found
                        }
                    } else {
                        // Handle other user task failure
                    }
                }
            } else {
                // Handle current user task failure
            }
        }
    }


    private fun callAPI(jsonObject: JSONObject) {
        val JSON = "application/json; charset=utf-8".toMediaType()
        val client = OkHttpClient()
        val url = "https://fcm.googleapis.com/fcm/send"

        // Correct way to create RequestBody from a JSONObject
        val body: RequestBody = jsonObject.toString().toRequestBody(JSON)

        // Example of how to build and execute the request
        val request = Request.Builder()
            .url(url)
            .post(body)
            .header("Authorization","Bearer AAAAI2hE_K4:APA91bFS4GsK6zL7cRNCZZb0uXXXRhGABGcV5-UNAr70Q8LxH6jL77Jjo3Ec-Q7hBufQSsvUXazjwcJnrggA2jvtq2D45jkmMWvZoq_iRI-yj5PNchIBupoOLgnYP30h6WJCu6r9OktO" )
            .build()

        // Execute the request (example in a synchronous way, you might need async)
         client.newCall(request).execute()
        // Handle the response here
    }
}
