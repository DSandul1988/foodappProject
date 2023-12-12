package com.example.finalapp.Model


data class MenuItemModel(
    var itemId: String? = null,
    var itemName: String? = null,
    var itemDescription: String? = null,
    var itemPrice: String? = null,
    var itemType: MenuItemType=MenuItemType.DRINK,

)
enum class MenuItemType {
    FOOD,
    DRINK,

}