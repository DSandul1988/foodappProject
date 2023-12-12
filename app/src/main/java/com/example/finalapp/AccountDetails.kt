package com.example.finalapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.finalapp.Model.UserModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.ktx.Firebase
import android.view.LayoutInflater
import android.widget.EditText

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.ktx.storage

class AccountDetails : AppCompatActivity() {

    private lateinit var userN: TextView
    private lateinit var userE: TextView
    private lateinit var userNa: TextView
    private lateinit var userAddress: TextView
    private lateinit var updateProfilePicLink: TextView
    private lateinit var updateProfile: Button
    private lateinit var profileImageView: ImageView
    private lateinit var backBtn: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account_details)

        // Initialize views
        userN = findViewById(R.id.profileNAMETxt)
        userE = findViewById(R.id.profileEmail)
        userNa = findViewById(R.id.profileAttName)
        userAddress = findViewById(R.id.profileAdressTxt)
        updateProfilePicLink = findViewById(R.id.updatePicture)
        updateProfile = findViewById(R.id.updateProfilebtn)
        profileImageView = findViewById(R.id.profilePicture)
        backBtn =findViewById(R.id.backArrow)

        // Get current user
        val user = Firebase.auth.currentUser
        user?.let { currentUser ->
            // Fetch user data from Realtime Database
            fetchUserData(currentUser.uid)
        }
        updateProfile.setOnClickListener{
            showUpdateProfileDialog()
        }
backBtn.setOnClickListener{
    val intent =Intent(this@AccountDetails,MenuActivity::class.java)
    startActivity(intent)
}
        updateProfilePicLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_PICK_CODE)
        }
    }

    companion object {
        private const val IMAGE_PICK_CODE = 1001
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_PICK_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { imageUri ->
                uploadImageToFirebaseStorage(imageUri)
            }
        }


    }

    private fun uploadImageToFirebaseStorage(imageUri: Uri) {
        val user = Firebase.auth.currentUser
        user?.let {
            val uid = user.uid
            val storageRef = Firebase.storage.reference
            val profileImageRef = storageRef.child("userPictures/$uid/profile_picture.jpg")

            profileImageRef.putFile(imageUri)
                .addOnSuccessListener {
                    profileImageRef.downloadUrl.addOnSuccessListener { uri ->
                        val url = uri.toString()
                        updateUserProfilePictureUrl(uid, url)
                        loadProfileImage(url)
                    }
                }
                .addOnFailureListener {
                    Log.e("UploadError", "Failed to upload image", it)
                }
        }
    }

    private fun updateUserProfilePictureUrl(uid: String, url: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")
        userRef.child("UserProfilePictureUrl").setValue(url)
            .addOnSuccessListener {
                // Handle successful update
            }
            .addOnFailureListener {
                Log.e("DatabaseError", "Failed to update user profile picture URL", it)
            }
    }

    private fun fetchUserData(uid: String) {
        val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val userModel = dataSnapshot.getValue(UserModel::class.java)
                userModel?.let { model ->
                    userN.text = model.UserName
                    userE.text = model.UserEmail
                    userNa.text = model.UserName
                    userAddress.text = model.UserAdress
                    model.UserProfilePictureUrl?.let { imageUrl ->
                        loadProfileImage(imageUrl)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("DatabaseError", "Error fetching user data", error.toException())
            }
        })
    }

    private fun loadProfileImage(imageUrl: String) {
        Glide.with(this)
            .load(imageUrl)
            .placeholder(R.drawable.baseline_account_box_24) // Replace with your placeholder image
            .into(profileImageView)
    }

    private fun showUpdateProfileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.update_dialog, null)
        val userNameEditText = dialogView.findViewById<EditText>(R.id.editTextUserName)
        val userAddressEditText = dialogView.findViewById<EditText>(R.id.editTextUserAddress)

        AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("Update Profile")
            .setPositiveButton("Confirm") { dialog, which ->
                val newUserName = userNameEditText.text.toString().trim()
                val newUserAddress = userAddressEditText.text.toString().trim()

                if (newUserName.isNotEmpty() && newUserAddress.isNotEmpty()) {
                    updateUserData(newUserName, newUserAddress)
                } else {
                    Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun updateUserData(newUserName: String, newUserAddress: String) {
        val user = Firebase.auth.currentUser
        user?.let {
            val uid = user.uid
            val userRef = FirebaseDatabase.getInstance().getReference("users/$uid")

            val updates = hashMapOf<String, Any>(
                "UserName" to newUserName,
                "UserAdress" to newUserAddress
            )

            userRef.updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    fetchUserData(uid)
                }

                .addOnFailureListener {
                    Toast.makeText(this, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
        }
    }
}


