package com.example.sampahmasgabungan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class MarketView : Fragment() {
    private var recyclerViewOfficial : RecyclerView? = null
    private var recyclerViewNewest : RecyclerView? = null
    private var recycleViewOfficialAdapter: RecycleViewProductSquareAdapter? = null
    private var recycleViewNewestAdapter: RecycleViewProductRectangleAdapter? = null
    private var productSquareOfficialList = mutableListOf<ProductVariable>()
    private var productRectangleNewestList = mutableListOf<ProductVariable>()
    private val database: FirebaseDatabase = Firebase.database
    private val reference: DatabaseReference = database.getReference("item")
//    private val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("item")

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_market_view, container, false)

        // Official Products RecyclerView
        productSquareOfficialList = ArrayList()
        recyclerViewOfficial = view.findViewById(R.id.rvOfficialLists) as RecyclerView
        recycleViewOfficialAdapter = RecycleViewProductSquareAdapter(requireContext(), productSquareOfficialList)
        val layoutManagerOfficial: RecyclerView.LayoutManager = GridLayoutManager(context, 2)
        recyclerViewOfficial!!.layoutManager = layoutManagerOfficial
        recyclerViewOfficial!!.adapter = recycleViewOfficialAdapter


        // Newest Products RecyclerView
        productRectangleNewestList = ArrayList()
        recyclerViewNewest = view.findViewById(R.id.rvNewestList) as RecyclerView
        recycleViewNewestAdapter = RecycleViewProductRectangleAdapter(requireContext(), productRectangleNewestList)
        val layoutManagerNewest: RecyclerView.LayoutManager = GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false)
        recyclerViewNewest!!.layoutManager = layoutManagerNewest
        recyclerViewNewest!!.adapter = recycleViewNewestAdapter

        prepareOfficialListData()
        prepareNewestListData()

        recycleViewOfficialAdapter!!.onItemClick = {
            val intent = Intent(context, Detailed_Product::class.java)
            intent.putExtra("recommended", it)
            startActivity(intent)
        }

        recycleViewNewestAdapter!!.onItemClick = {
            val intent = Intent(context, Detailed_Product::class.java)
            intent.putExtra("recommended", it)
            startActivity(intent)
        }

        return view
    }

    private fun prepareOfficialListData() {
        // Menambahkan listener untuk mendengarkan perubahan pada database
        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Bersihkan data sebelum menambahkan data baru
                productSquareOfficialList.clear()

                // Iterasi melalui setiap item dalam snapshot
                for (itemSnapshot in dataSnapshot.children) {
                    // Mengambil nilai dari Firebase
                    val officialStatus = itemSnapshot.child("official").getValue(String::class.java)

                    // Jika officialStatus == "1", tambahkan item ke daftar
                    if (officialStatus == "1") {
                        val title = itemSnapshot.child("title").getValue(String::class.java)
                        val image = itemSnapshot.child("image").getValue(String::class.java)
                        val price = itemSnapshot.child("price").getValue(String::class.java)
                        val description = itemSnapshot.child("description").getValue(String::class.java)

                        // Menambahkan item ke daftar
                        val productSquare = ProductVariable(itemSnapshot.key.toString(),title ?: "", image ?: "", price ?: "", description ?: "", 1)
                        productSquareOfficialList.add(productSquare)
                    }
                }

                // Memberitahu adapter bahwa data telah berubah
                recycleViewOfficialAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan
            }
        })
    }

    private fun prepareNewestListData() {
        reference.orderByChild("date").limitToLast(3).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (productSnapshot in dataSnapshot.children) {
                        val title = productSnapshot.child("title").getValue(String::class.java)
                        val image = productSnapshot.child("image").getValue(String::class.java)
                        val price = productSnapshot.child("price").getValue(String::class.java)
                        val description = productSnapshot.child("description").getValue(String::class.java)
                        val NewestList = ProductVariable(productSnapshot.key.toString(), title ?: "", image ?: "", price ?: "", description ?: "",1)
                        if (NewestList != null) {
                            productRectangleNewestList.add(NewestList)
                        }
                    }
                    recycleViewNewestAdapter!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Penanganan kesalahan, jika diperlukan
            }
        })

    }


}