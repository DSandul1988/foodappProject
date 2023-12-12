package com.example.finalapp.Model

data class UserModel(
    var UserId: String? = null,
    var UserName: String? = null,
    var UserEmail: String? = null,
    var UserPassword: String? = null,
    var UserAdress:String? =null,
    var isAdmin: Boolean = false,
    var FCMToken:String?=null,
    var UserProfilePictureUrl:String? =null,
    var orderId:String?=null)
