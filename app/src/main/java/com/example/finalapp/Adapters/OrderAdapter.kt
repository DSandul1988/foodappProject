package com.example.finalapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Model.OrderModel
import com.example.finalapp.R

class OrderAdapter(private val orderList: ArrayList<OrderModel>) :
    RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private lateinit var mListener: onItemClickListener

    interface onItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(clickListener: onItemClickListener) {
        mListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.order_res, parent, false)
        return ViewHolder(itemView,mListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentOrder = orderList[position]
        holder.orderId.text = currentOrder.orderId
        holder.userId.text = currentOrder.userId
        holder.drinks.text=currentOrder.items.toString()

    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    class ViewHolder(itemView: View, clickListener: onItemClickListener) :
        RecyclerView.ViewHolder(itemView) {

        val orderId: TextView = itemView.findViewById(R.id.orderName)
        val userId: TextView = itemView.findViewById(R.id.orderPrice)
        val drinks: TextView = itemView.findViewById(R.id.orderFood)
        val food:TextView =itemView.findViewById(R.id.orderDrinks)

        init {
            itemView.setOnClickListener {
                clickListener.onItemClick(adapterPosition)
            }
        }
    }

}