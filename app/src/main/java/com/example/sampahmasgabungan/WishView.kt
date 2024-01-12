package com.example.sampahmasgabungan

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale


class WishView : Fragment() {
    private var recyclerView : RecyclerView? = null
    private var recycleViewWishListAdapter: RecycleViewWishListAdapter? = null
    private var WishList = mutableListOf<ProductVariable>()
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var quantityReference: DatabaseReference
    private lateinit var currentUserID:String
    private lateinit var pesanId: Button
    private lateinit var total: TextView
    private val REFRESH_REQUEST_CODE = 123
    private var totalSet:Double=0.0

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_wish_view, container, false)
        firebaseAuth = FirebaseAuth.getInstance()
        currentUserID = firebaseAuth.currentUser?.uid.toString()
        databaseReference = FirebaseDatabase.getInstance().reference.child("wishlist").child(currentUserID)

        pesanId = view.findViewById(R.id.pesanId)
        total = view.findViewById(R.id.total)

        WishList = ArrayList()
        recyclerView = view.findViewById(R.id.rvWishLists) as RecyclerView
        recycleViewWishListAdapter = RecycleViewWishListAdapter(requireContext(), WishList)
        val layoutManager : RecyclerView.LayoutManager = GridLayoutManager(context, 1)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recycleViewWishListAdapter


        loadWishListData()

        recycleViewWishListAdapter!!.onItemClick = {
            val intent = Intent(context, Detailed_Product::class.java)
            intent.putExtra("recommended", it)
            startActivityForResult(intent, REFRESH_REQUEST_CODE)
        }
        pesanId.setOnClickListener{
            val intent = Intent(context, CheckoutWishlistDetailActivity::class.java)
            startActivity(intent)
        }



        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REFRESH_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Refresh data when the result is received
            loadWishListData()
        }
    }

    fun numberToCurrency(number: Double): String {
        val localeID = Locale("id", "ID")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
        val formattedValue = currencyFormat.format(number)
        return formattedValue.replace(",", ".")
    }

    fun extractNumber(input: String): Double {
        val numericChars = input.filter { it.isDigit() }
        return numericChars.toDoubleOrNull() ?: 0.0
    }

    private fun calculateAndSetTotal() {
        totalSet = 0.0 // Reset totalSet before calculating the total
        for (product in WishList) {
            val price = extractNumber(product.price)
            val quantity = product.quantity

            totalSet += (price * quantity)
        }

        total.text = numberToCurrency(totalSet)
    }

    private fun loadWishListData() {
        databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                WishList.clear()
                // Jika ada penambahan data
                val productID = snapshot.key
                if (productID != null) {
                    quantityReference = FirebaseDatabase.getInstance().reference.child("wishlist").child(currentUserID!!)
                    quantityReference.addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(quantitySnapshot: DataSnapshot) {
                            if (quantitySnapshot.exists()) {

                                val quantity = quantitySnapshot.child(productID).child("quantity").value
//                                val quantity = snapshot.child("quantity").value.toString().toInt()
                                val itemReference = FirebaseDatabase.getInstance().reference.child("item").child(productID)
                                itemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                                    override fun onDataChange(itemSnapshot: DataSnapshot) {
                                        if (itemSnapshot.exists()) {
                                            val title = itemSnapshot.child("title").value.toString()
                                            val image = itemSnapshot.child("image").value.toString()
                                            val price = itemSnapshot.child("price").value.toString()
                                            val description = itemSnapshot.child("description").value.toString()
//                                val quantity = itemSnapshot.child("quantity").value.toString()

                                            val product = ProductVariable(productID, title, image, price, description,quantity.toString().toInt())

                                            WishList.add(product)
                                            recycleViewWishListAdapter?.notifyDataSetChanged()
                                            calculateAndSetTotal()                                        }
                                    }

                                    override fun onCancelled(error: DatabaseError) {
                                        // Handle error
                                    }
                                })
                            }
                        }

                        override fun onCancelled(error: DatabaseError) {
                            // Handle error
                        }
                    })



                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                // Jika ada perubahan pada data
                // Tidak perlu di-handle jika tidak relevan dalam konteks aplikasi Anda
                calculateAndSetTotal()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                // Jika ada penghapusan data
                // Tidak perlu di-handle jika tidak relevan dalam konteks aplikasi Anda
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                // Jika ada perpindahan data
                // Tidak perlu di-handle jika tidak relevan dalam konteks aplikasi Anda
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}

