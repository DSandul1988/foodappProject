package com.example.finalapp.utils

import com.google.firebase.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.storage

class FirebaseStorage {

     val storage = Firebase.storage
     var storageRef = storage.reference

     var imagesRef: StorageReference? = storageRef.child("images")
}