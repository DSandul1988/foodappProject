package com.example.finalapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.R

import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType

class DrinkAdapter(private val drinkList: ArrayList<MenuItemModel>) :
    RecyclerView.Adapter<DrinkAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.drink_resource, parent, false)
        return ViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentMenuItem = drinkList[position]
        if (currentMenuItem.itemType == MenuItemType.DRINK) {
            holder.drinksName.text = currentMenuItem.itemName
            holder.drinksDescription.text = currentMenuItem.itemDescription
            holder.drinkPrice.text = currentMenuItem.itemPrice
        }
    }

    override fun getItemCount(): Int {
        return drinkList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val drinksName: TextView = itemView.findViewById(R.id.drinkName)
        val drinksDescription: TextView = itemView.findViewById(R.id.drinkDescription)
        val drinkPrice: TextView = itemView.findViewById(R.id.drinkPrice)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

}