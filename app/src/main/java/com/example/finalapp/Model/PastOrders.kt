package com.example.finalapp.Model

data class PastOrders(
    var orderId: String? = null,
    val userId: String? = null,
    var items: HashMap<String?, MenuItemModel>? = null
)
