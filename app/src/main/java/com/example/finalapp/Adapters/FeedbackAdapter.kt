package com.example.finalapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Model.FeedBackModel
import com.example.finalapp.R


class FeedbackAdapter(private var feedbackList: List<FeedBackModel>) : RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userName: TextView = view.findViewById(R.id.feedName) // Updated ID for user name
        val feedbackText: TextView = view.findViewById(R.id.feedText) // Correct ID for feedback text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback_resource, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.userName.text = feedback.userName
        holder.feedbackText.text = feedback.feedbackText
    }

    fun updateFeedbackList(newFeedbackList: List<FeedBackModel>) {
        feedbackList = newFeedbackList
        notifyDataSetChanged()
    }

    override fun getItemCount() = feedbackList.size
}

