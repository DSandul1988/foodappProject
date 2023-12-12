package com.example.finalapp.Model

data class FeedBackModel(
var itemId: String? = null,           // ID of the menu item to which this feedback relates
var userId: String? = null,           // Unique identifier for the user
var userName: String? = null,         // Name of the user
var feedbackText: String? = null,     // Actual feedback content

)
