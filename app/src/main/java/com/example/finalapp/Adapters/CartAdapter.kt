package com.example.finalapp.Adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.R

class CartAdapter(
    private var menuItems: MutableList<MenuItemModel>,
    private val onDeleteClicked: (MenuItemModel) -> Unit
) : RecyclerView.Adapter<CartAdapter.MenuItemViewHolder>() {

    class MenuItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.itemName)
        val itemDescription: TextView = itemView.findViewById(R.id.itemDescription)
        val itemPrice: TextView = itemView.findViewById(R.id.itemPrice)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_layout, parent, false)
        return MenuItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: MenuItemViewHolder, position: Int) {
        val menuItem = menuItems[position]

        holder.itemName.text = menuItem.itemName ?: "N/A"
        holder.itemDescription.text = menuItem.itemDescription ?: "N/A"
        holder.itemPrice.text = menuItem.itemPrice ?: "N/A"

        holder.itemView.findViewById<Button>(R.id.deleteButton).setOnClickListener {
            onDeleteClicked(menuItem)
        }
    }

    fun removeItem(item: MenuItemModel) {
        val position = menuItems.indexOf(item)
        if (position >= 0) {
            menuItems.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, menuItems.size)
        }
    }
    override fun getItemCount(): Int {
        return menuItems.size
    }

    fun setMenuItems(newMenuItems: MutableList<MenuItemModel>) {
        menuItems = newMenuItems
        notifyDataSetChanged()
    }
}







