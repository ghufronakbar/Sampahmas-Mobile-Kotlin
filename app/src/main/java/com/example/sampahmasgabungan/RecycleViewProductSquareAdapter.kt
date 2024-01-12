package com.example.sampahmasgabungan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecycleViewProductSquareAdapter constructor(
    private val context: Context,
    private val productSquareList: List<ProductVariable>) :
    RecyclerView.Adapter<RecycleViewProductSquareAdapter.MyViewHolder>() {

    var onItemClick : ((ProductVariable) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_square_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productSquareList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tProdukMenu.text = productSquareList[position].title
        holder.tNamaProdukMenu.text = productSquareList[position].title
        holder.tHargaProdukMenu.text = productSquareList[position].price
        Glide.with(context)
            .load(productSquareList[position].image)
            .into(holder.imProduk)
        holder.cvOutside.setOnClickListener {
//            Toast.makeText(context, recommendedList[position].title, Toast.LENGTH_LONG).show()
            onItemClick?.invoke(productSquareList[position])
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imProduk: ImageView = itemView.findViewById(R.id.imProduk)
        val cvOutside: CardView = itemView.findViewById(R.id.cvOutside)
        val tProdukMenu: TextView = itemView.findViewById(R.id.tProdukMenu)
        val tNamaProdukMenu: TextView = itemView.findViewById(R.id.tNamaProdukMenu)
        val tHargaProdukMenu: TextView = itemView.findViewById(R.id.tHargaProdukMenu)

    }


}