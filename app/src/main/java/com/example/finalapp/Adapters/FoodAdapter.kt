package com.example.finalapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Model.MenuItemModel
import com.example.finalapp.Model.MenuItemType
import com.example.finalapp.R

class FoodAdapter(private val foodList: ArrayList<MenuItemModel>) :
    RecyclerView.Adapter<FoodAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.food_resource, parent, false)
        return ViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentMenuItem = foodList[position]
        if (currentMenuItem.itemType == MenuItemType.FOOD) {
        holder.foodName.text = currentMenuItem.itemName
        holder.foodDescription.text = currentMenuItem.itemDescription
        holder.foodPrice.text = currentMenuItem.itemPrice}
    }


    override fun getItemCount(): Int {
        return foodList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val foodName: TextView = itemView.findViewById(R.id.foodName)
        val foodDescription: TextView = itemView.findViewById(R.id.foodDescription)
        val foodPrice:TextView =itemView.findViewById(R.id.foodPrice)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

}
