package com.example.sampahmasgabungan

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RecycleViewWishListAdapter constructor(
    private val context: Context,
    private val RecycleViewWishList: List<ProductVariable>) :
    RecyclerView.Adapter<RecycleViewWishListAdapter.MyViewHolder>() {

    var onItemClick : ((ProductVariable) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_wishlist_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return RecycleViewWishList.size
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.tProdukKiri.text = RecycleViewWishList[position].title
        holder.tProdukKanan.text = RecycleViewWishList[position].title
        holder.tHarga.text = RecycleViewWishList[position].price
//        holder.imProduk.setImageResource(RecycleViewWishList[position].image)
        Glide.with(context)
            .load(RecycleViewWishList[position].image)
            .into(holder.imProduk)

        holder.cvOutside.setOnClickListener {
//            Toast.makeText(context, recommendedList[position].title, Toast.LENGTH_LONG).show()
            onItemClick?.invoke(RecycleViewWishList[position])
        }
        holder.totalItemId.text = RecycleViewWishList[position].quantity.toString()

        holder.plusButton.setOnClickListener {
//            var totalItemId = findViewById<TextView>(R.id.totalItemId)
            RecycleViewWishList[position].quantity++
            holder.totalItemId.text = RecycleViewWishList[position].quantity.toString()
            updateQuantityInDatabase(RecycleViewWishList[position].id, RecycleViewWishList[position].quantity)


        }


        holder.minusButton.setOnClickListener {
//            var totalItemId = findViewById<TextView>(R.id.totalItemId)
            if (RecycleViewWishList[position].quantity > 1) {
                RecycleViewWishList[position].quantity--
                holder.totalItemId.text = RecycleViewWishList[position].quantity.toString()
                updateQuantityInDatabase(RecycleViewWishList[position].id, RecycleViewWishList[position].quantity)
            }
        }
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imProduk: ImageView = itemView.findViewById(R.id.imProduk)
        val cvOutside: CardView = itemView.findViewById(R.id.cvOutside)
        val tProdukKiri: TextView = itemView.findViewById(R.id.tProdukKiri)
        val tProdukKanan: TextView = itemView.findViewById(R.id.tProdukKanan)
        val tHarga: TextView = itemView.findViewById(R.id.tHarga)
        val totalItemId: TextView = itemView.findViewById(R.id.totalItemId)
        val plusButton: ImageButton = itemView.findViewById(R.id.plusId)
        val minusButton: ImageButton = itemView.findViewById(R.id.minusId)

    }
    private fun updateQuantityInDatabase(itemId: String, newQuantity: Int) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUserID = firebaseAuth.currentUser?.uid.toString()
        val wishlistReference = FirebaseDatabase.getInstance().reference.child("wishlist").child(currentUserID)
        wishlistReference.child(itemId).child("quantity").setValue(newQuantity)
    }



}