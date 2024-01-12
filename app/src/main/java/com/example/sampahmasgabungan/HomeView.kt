package com.example.sampahmasgabungan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale

class HomeView : Fragment() {

    private var recyclerView : RecyclerView? = null
    private var recyclerViewOfficial : RecyclerView? = null
    private var recycleViewProductSquareAdapter: RecycleViewProductSquareAdapter? = null
    private var recommendedList = mutableListOf<ProductVariable>()

    private var recycleViewOfficialAdapter: RecycleViewProductRectangleAdapter? = null
    private var officialList = mutableListOf<ProductVariable>()

    private var balanceValueEventListener: ValueEventListener? = null

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var tvName: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvSaldo: TextView
    private lateinit var tvUid: TextView
    private lateinit var iCopy: ImageView
    private lateinit var uid: String

    private var isHidden = false

    fun numberToCurrency(number: Double): String {
        val localeID = Locale("id", "ID")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
        val formattedValue = currencyFormat.format(number)

        // Remove trailing ",00" if the value has no decimal places
        val cleanValue = if (number % 1 == 0.0) {
            formattedValue.replace(",00", "")
        } else {
            formattedValue
        }

        return cleanValue.replace(",", ".")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_view, container, false)


        // Official Products RecyclerView
        officialList = ArrayList()
        recyclerViewOfficial = view.findViewById(R.id.rvOfficialList) as RecyclerView
        recycleViewOfficialAdapter = RecycleViewProductRectangleAdapter(requireContext(), officialList)
        val layoutManagerOfficial: RecyclerView.LayoutManager = GridLayoutManager(context, 1, RecyclerView.HORIZONTAL, false)
        recyclerViewOfficial!!.layoutManager = layoutManagerOfficial
        recyclerViewOfficial!!.adapter = recycleViewOfficialAdapter

        recommendedList = ArrayList()
        recyclerView = view.findViewById(R.id.rvRecommendedLists) as RecyclerView
        recycleViewProductSquareAdapter = RecycleViewProductSquareAdapter(requireContext(), recommendedList)
        val layoutManager : RecyclerView.LayoutManager = GridLayoutManager(context, 2)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recycleViewProductSquareAdapter


        prepareRecommendedListData()
        prepareOfficialListData()

        recycleViewOfficialAdapter!!.onItemClick = {
            val intent = Intent(context, Detailed_Product::class.java)
            intent.putExtra("recommended", it)
            startActivity(intent)
        }

        recycleViewProductSquareAdapter!!.onItemClick = {
            val intent = Intent(context, Detailed_Product::class.java)
            intent.putExtra("recommended", it)
            startActivity(intent)
        }

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        tvName = view.findViewById(R.id.tvName)
        tvPoints = view.findViewById(R.id.tvPoints)
        tvSaldo = view.findViewById(R.id.tvSaldo)
        tvUid = view.findViewById(R.id.tvUid)
        iCopy = view.findViewById(R.id.iCopy)


        val seeAllbutton = view.findViewById<ImageButton>(R.id.iSeeAll)
        seeAllbutton.setOnClickListener {
            val intent = context?.let { Intent(it, more_menu::class.java) }
            startActivity(intent)
        }

        val exchangeButton = view.findViewById<ImageButton>(R.id.iExchange)
        exchangeButton.setOnClickListener {
            val intent = context?.let { Intent(it, activity_QR_NFC::class.java) }
            startActivity(intent)
        }

        val balanceButton = view.findViewById<CardView>(R.id.cvKartu)
        balanceButton.setOnClickListener {
            val intent = context?.let { Intent(it, Balance::class.java) }
            startActivity(intent)
        }

        val settingsButton = view.findViewById<ImageView>(R.id.ibSettings)
        settingsButton.setOnClickListener {
            val intent = context?.let { Intent(it, Settings::class.java) }
            startActivity(intent)
        }

        val dailyMissionButton = view.findViewById<ImageButton>(R.id.iDailyMission)
        dailyMissionButton.setOnClickListener {
            val intent = context?.let { Intent(it, Daily_Mission::class.java) }
            startActivity(intent)
        }

        val nearbyButton = view.findViewById<ImageButton>(R.id.iNearby)
        nearbyButton.setOnClickListener {
            val intent = context?.let { Intent(it, Nearby::class.java) }
            startActivity(intent)
        }

        val currentUser = auth.currentUser
        val userUid = currentUser?.uid



        userUid?.let {
            balanceValueEventListener = databaseReference.child(it).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val name = dataSnapshot.child("name").value.toString()
                        val points = dataSnapshot.child("points").value?.toString() ?: "0"
                        val balance = dataSnapshot.child("balance").value?.toString() ?: "0"
                        uid = userUid

                        tvName.text = name
                        tvPoints.text = points
                        tvSaldo.text = numberToCurrency(balance.toDouble())
                        tvUid.text = uid
                        hideUid()
                        // Set OnClickListener for iCopy
                        iCopy.setOnClickListener {
                            copyToClipboard(uid)
                        }

                        // Set OnClickListener for tvUid
                        tvUid.setOnClickListener {
                            if (isHidden) {
                                showUid(uid)
                            } else {
                                hideUid()
                            }
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle failure to retrieve data
                }
            })
        }

        return view
    }
    override fun onDestroyView() {
        // Check if uid is initialized before using it
        if (this::uid.isInitialized) {
            // Remove the ValueEventListener when the fragment is destroyed
            balanceValueEventListener?.let {
                databaseReference.child(this.uid).removeEventListener(it)
            }
        }
        super.onDestroyView()
    }



    private fun prepareRecommendedListData() {
        // Referensi ke tabel produk pada database Firebase
        val productsRef = FirebaseDatabase.getInstance().reference.child("item")

        // Ambil 8 jenis barang secara acak dari Firebase
        productsRef.limitToFirst(8).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                recommendedList.clear() // Bersihkan data sebelum menambahkan data baru

                for (productSnapshot in dataSnapshot.children) {
                    // Baca data produk dari snapshot
                    val title = productSnapshot.child("title").value?.toString() ?: ""
                    val image = productSnapshot.child("image").value?.toString() ?: ""
                    val price = productSnapshot.child("price").value?.toString() ?: ""
                    val description = productSnapshot.child("description").value?.toString() ?: ""

                    // Tambahkan produk ke recommendedList
                    val recommendedProduct = ProductVariable(productSnapshot.key.toString(),title, image, price, description,1)
                    recommendedList.add(recommendedProduct)
                }

                // Notifikasi adapter bahwa data telah berubah
                recycleViewProductSquareAdapter?.notifyDataSetChanged()
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle failure to retrieve data
            }
        })
    }


    private fun prepareOfficialListData() {
        // Referensi ke tabel produk pada database Firebase
        val productsRef = FirebaseDatabase.getInstance().reference.child("item")

        // Hanya ambil produk yang official = "1" dan batasi hanya 3 data
        productsRef.orderByChild("official").equalTo("1").limitToFirst(3)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    officialList.clear() // Bersihkan data sebelum menambahkan data baru

                    for (productSnapshot in dataSnapshot.children) {
                        // Baca data produk dari snapshot
                        val title = productSnapshot.child("title").value?.toString() ?: ""
                        val image = productSnapshot.child("image").value?.toString() ?: ""
                        val price = productSnapshot.child("price").value?.toString() ?: ""
                        val description = productSnapshot.child("description").value?.toString() ?: ""

                        // Tambahkan produk ke officialList
                        val officialProduct = ProductVariable(productSnapshot.key.toString(), title, image, price, description,1)
                        officialList.add(officialProduct)
                    }

                    // Notifikasi adapter bahwa data telah berubah
                    recycleViewOfficialAdapter?.notifyDataSetChanged()
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle failure to retrieve data
                }
            })
    }


    private fun copyToClipboard(text: String) {
        val clipboardManager =
            context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("UID", text)
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "UID copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    private fun hideUid() {
        val uid = tvUid.text.toString()
        if (uid.length > 4) {
            val visiblePart = uid.substring(0, 4)
            val hiddenPart = uid.substring(4).replace(Regex("[0-9A-Za-z]"), "*")
            tvUid.text = "$visiblePart$hiddenPart"
            isHidden = true
        }
    }


    private fun showUid(uid: String) {
        tvUid.text = uid
        isHidden = false
    }
}