package com.example.finalapp.utils

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference

class FirebaseUtils {


  public  fun getChatRoom(chatRoomId: String): DatabaseReference {
        return FirebaseDatabase.getInstance().getReference("chatroom/$chatRoomId")
    }

    public fun  getChatRoomId(user1ID:String, user2ID:String):String{

        if (user1ID.hashCode()<user2ID.hashCode()){
            return user1ID +"" +user2ID
        }
        else  return user2ID +"" +user1ID
    }

    public  fun getChatRoomMessageReference(chatRoomId: String): DatabaseReference {
        return getChatRoom(chatRoomId).child("chats")
    }
}