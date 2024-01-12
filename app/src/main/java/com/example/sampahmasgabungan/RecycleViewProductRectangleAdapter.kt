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

class RecycleViewProductRectangleAdapter constructor(
    private val context: Context,
    private val productRectangleList: List<ProductVariable>) :
    RecyclerView.Adapter<RecycleViewProductRectangleAdapter.MyViewHolder>() {

    var onItemClick : ((ProductVariable) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_rectangle_list_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return productRectangleList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tProdukKiri.text = productRectangleList[position].title
        holder.tProdukKanan.text = productRectangleList[position].title
        holder.tHarga.text = productRectangleList[position].price
        Glide.with(context)
            .load(productRectangleList[position].image)
            .into(holder.imProduk)

        holder.cvOutside.setOnClickListener {
//            Toast.makeText(context, recommendedList[position].title, Toast.LENGTH_LONG).show()
            onItemClick?.invoke(productRectangleList[position])
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imProduk: ImageView = itemView.findViewById(R.id.imProduk)
        val cvOutside: CardView = itemView.findViewById(R.id.cvOutside)
        val tProdukKiri: TextView = itemView.findViewById(R.id.tProdukKiri)
        val tProdukKanan: TextView = itemView.findViewById(R.id.tProdukKanan)
        val tHarga: TextView = itemView.findViewById(R.id.tHarga)

    }


}