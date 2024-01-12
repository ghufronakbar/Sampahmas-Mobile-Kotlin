package com.example.sampahmasgabungan

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Detailed_Product : AppCompatActivity() {
    private lateinit var iditem:String
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var currentUserID:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_product)

        val loveId = findViewById<CardView>(R.id.loveId)

        firebaseAuth = FirebaseAuth.getInstance()
        currentUserID = firebaseAuth.currentUser?.uid.toString()

        val wishlistReference = FirebaseDatabase.getInstance().reference.child("wishlist").child(currentUserID)
        val wishListData = intent.getParcelableExtra<ProductVariable>("recommended")
        wishlistReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChild(iditem)) {
                    if (wishListData != null) {
                        WishlistData.addToWishlist(wishListData)
                    }
                    val loveId = findViewById<ImageView>(R.id.loveeid)
                    loveId.setImageResource(R.drawable.lovee)

                } else {
                    // ID item tidak ditemukan di dalam node wishlist
                    Log.d("WishlistCheck", "Item ID $iditem not found in wishlist.")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
                Log.e("WishlistCheck", "Error checking wishlist: ${error.message}")
            }
        })

        loveId.setOnClickListener {
            toggleWishlistStatus()
        }

        val backbutton = findViewById<ImageButton>(R.id.iBack)
        backbutton.setOnClickListener {
            setResult(Activity.RESULT_OK)
            super.onBackPressed()
        }

        val recommended = intent.getParcelableExtra<ProductVariable>("recommended")
        if (recommended !=null){
            val imageObj: ImageView = findViewById(R.id.imageObj)
            val titleid: TextView = findViewById(R.id.titleid)
            val hargaId: TextView = findViewById(R.id.hargaId)
            val desId: TextView = findViewById(R.id.desId)
            val objName: TextView = findViewById(R.id.objName)
            val titleInside: TextView = findViewById(R.id.titleInside)

            Glide.with(this)
                .load(recommended.image)
                .into(imageObj)
            iditem = recommended.id
            titleid.text = recommended.title
            objName.text = recommended.title
            titleInside.text = recommended.title
            hargaId.text = recommended.price
            desId.text = recommended.description
        }

        val buyNowButton = findViewById<Button>(R.id.buyNow)
        buyNowButton.setOnClickListener {
            val intent = Intent(this, Checkout_Detail::class.java)
            intent.putExtra("recommended", recommended)
            startActivity(intent)
            finish()

        }

        val chatButton = findViewById<Button>(R.id.contactSeller)
        chatButton.setOnClickListener {
            val intent = Intent(this, chat::class.java)
            startActivity(intent)
            finish()

        }

    }

    private fun toggleWishlistStatus() {
        val wishListData = intent.getParcelableExtra<ProductVariable>("recommended")
        val wishlistReference = FirebaseDatabase.getInstance().reference.child("wishlist").child(currentUserID)
        if (WishlistData.wishlist.contains(wishListData)) {
            // Menghapus data dari node "wishlist" dengan itemIDToRemove
            wishlistReference.child(iditem).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Data berhasil dihapus dari wishlist
                        Log.d("WishlistRemove", "Item ID $iditem removed from wishlist.")
                    } else {
                        // Gagal menghapus data dari wishlist
                        Log.e("WishlistRemove", "Error removing item from wishlist: ${task.exception?.message}")
                    }
                }
            // Already in wishlist, remove it
            if (wishListData != null) {
                WishlistData.removeFromWishlist(wishListData)
            }
            showToast("Product removed from wishlist")
            val loveId = findViewById<ImageView>(R.id.loveeid)
            loveId.setImageResource(R.drawable.tb_wish0)
        } else {

//            val wishlistData = mapOf(iditem to true)
            val wishlistData = mapOf(
                iditem to mapOf("quantity" to 1)
            )

            wishlistReference.updateChildren(wishlistData)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Data berhasil ditambahkan ke wishlist
                        Log.d("WishlistAdd", "Item ID $iditem added to wishlist.")
                    } else {
                        // Gagal menambahkan data ke wishlist
                        Log.e("WishlistAdd", "Error adding item to wishlist: ${task.exception?.message}")
                    }
                }
            // Not in wishlist, add it
            if (wishListData != null) {
                WishlistData.addToWishlist(wishListData)
            }
            showToast("Product added to wishlist")
            val loveId = findViewById<ImageView>(R.id.loveeid)
            loveId.setImageResource(R.drawable.lovee)
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}