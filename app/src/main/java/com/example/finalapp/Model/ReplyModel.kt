package com.example.finalapp.Model

data class ReplyModel(
    var feedbackId: String? = null,
    var userId: String? = null,
    var userName: String? = null,
    var replyText: String? = null,
    // Time when the reply was posted
)