package com.example.finalapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.finalapp.Model.ChatRoom
import com.example.finalapp.utils.FirebaseUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class OrderStatusActivity : AppCompatActivity() {

    private lateinit var status: TextView
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUtils: FirebaseUtils
    private lateinit var ChatRoomId:String
    private lateinit var user2ID: String
    private lateinit var user1ID: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_status)

        val messageText = intent.getStringExtra("order_status")
        status = findViewById(R.id.orderStatusTxt)


//        auth =FirebaseAuth.getInstance()
//        val currentUser = auth.currentUser?.uid ?:""
//        firebaseUtils = FirebaseUtils()
//        user1ID = currentUser
//        user2ID = "URvJoAyPCNMxeZvfPHQy4XtGwHp2"
//        ChatRoomId = firebaseUtils.getChatRoomId(user1ID, user2ID)
//
//        getCreateChatRoom()
//
//        fetchMessages()

    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MenuActivity::class.java)

        startActivity(intent)
        finish()
    }
    private fun getCreateChatRoom() {
        firebaseUtils.getChatRoom(ChatRoomId).addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val chatR = dataSnapshot.getValue(ChatRoom::class.java)
                    // Use chatR for further operations
                } else {
                    val newChatRoom = ChatRoom(
                        chatRoomId = ChatRoomId,
                        usersId = listOf(user1ID, user2ID),
                        // Set the current timestamp
                    )

                    firebaseUtils.getChatRoom(ChatRoomId).setValue(newChatRoom)
                        .addOnSuccessListener {
                            // Handle success
                        }
                        .addOnFailureListener {
                            // Handle failure
                        }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle the error
            }
        })
    }
    private fun fetchMessages() {

        val databaseReference = FirebaseDatabase.getInstance().getReference("chatroom/${ChatRoomId}/chats")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val stringBuilder = StringBuilder()

                for (messageSnapshot in snapshot.children) {
                    val message = messageSnapshot.child("message").getValue(String::class.java)
                    stringBuilder.append(message).append("\n")
                }

                status.text = stringBuilder.toString()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle possible errors
            }
        })
    }
}