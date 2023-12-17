package com.example.finalapp.Model

data class OrderModel(
    var orderId: String? = null,
    val userId: String? = null,
    var items: HashMap<String?, MenuItemModel>? = null

)