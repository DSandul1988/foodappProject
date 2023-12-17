package com.example.finalapp.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.finalapp.Model.FeedBackModel
import com.example.finalapp.Model.ReplyModel
import com.example.finalapp.R
import com.google.firebase.database.FirebaseDatabase


class FeedbackAdapter(private var feedbackList: List<FeedBackModel> ,
                      private val currentUserId: String,
                      private val currentUserName: String) :
    RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder>() {

    class FeedbackViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val userName: TextView = view.findViewById(R.id.feedName) // Updated ID for user name
        val feedbackText: TextView = view.findViewById(R.id.feedText)
        val replyInput :EditText = view.findViewById(R.id.replyInput)
         val replyBtn: Button = view.findViewById(R.id.replyButton)
        val replyTxt : TextView = view.findViewById(R.id.replyDisplay)// Correct ID for feedback text
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.feedback_resource, parent, false)
        return FeedbackViewHolder(view)
    }

    override fun onBindViewHolder(holder: FeedbackViewHolder, position: Int) {
        val feedback = feedbackList[position]
        holder.userName.text = feedback.userName
        holder.feedbackText.text = feedback.feedbackText

        holder.replyBtn.setOnClickListener {
            // Get the reply text from EditText
            val replyText = holder.replyInput.text.toString()
            if (replyText.isNotEmpty()) {
                // Post the reply to Firebase and handle the response
                postReply(feedback.itemId, replyText, holder)
            } else {
                // Handle empty reply case, perhaps show a Toast message
            }
        }
    }

    private fun postReply(itemId: String?, replyText: String, holder: FeedbackViewHolder) {
        val replyRef = itemId?.let {
            FirebaseDatabase.getInstance().getReference("Replies").child(it)
        }

        // Use the passed userId and userName
        val reply = ReplyModel(feedbackId = itemId, userId = currentUserId, userName = currentUserName, replyText = replyText)
        replyRef?.push()?.setValue(reply)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Update UI with the reply
                holder.replyTxt.text = replyText
                holder.replyTxt.visibility = View.VISIBLE
                holder.replyInput.text.clear()
            } else {
                // Handle failure
            }
        }
    }

    fun updateFeedbackList(newFeedbackList: List<FeedBackModel>) {
        feedbackList = newFeedbackList
        notifyDataSetChanged()
    }

    override fun getItemCount() = feedbackList.size
}

