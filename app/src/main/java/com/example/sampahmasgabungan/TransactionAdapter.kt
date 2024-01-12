package com.example.sampahmasgabungan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(private val context: Context, private val transactionList: List<TransactionModel>) :
    RecyclerView.Adapter<TransactionAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView4: TextView = itemView.findViewById(R.id.textView4)
        val image: ImageView = itemView.findViewById(R.id.iTopUp2)
        val textView6: TextView = itemView.findViewById(R.id.textView6)
        val textView7: TextView = itemView.findViewById(R.id.textView7)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_history_topup, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val transaction = transactionList[position]
        val title = transaction.title
        holder.textView4.text = title
        holder.textView6.text = transaction.date
        holder.textView7.text = transaction.amount
        if (title=="Payment"){
            holder.image.setImageResource(R.drawable.credit_card)
        }else if(title=="Top Up"){
            holder.image.setImageResource(R.drawable.point)
        }


    }

    override fun getItemCount(): Int {
        return transactionList.size
    }
}
