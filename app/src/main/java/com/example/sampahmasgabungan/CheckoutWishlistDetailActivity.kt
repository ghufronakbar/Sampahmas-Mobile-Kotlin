package com.example.sampahmasgabungan

import Popup
import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampahmasgabungan.payment.WishlistMidtrans
import com.example.sampahmasgabungan.payment.paymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID

class CheckoutWishlistDetailActivity : AppCompatActivity() {
    private var recyclerView : RecyclerView? = null
    private var recycleViewWishListAdapter: RecycleViewWishListAdapter? = null
    private var wishList = mutableListOf<ProductVariable>()
    private lateinit var totalId: TextView
    private lateinit var username :String
    private lateinit var email :String
    private lateinit var phone:String
    private lateinit var province:String
    private lateinit var alamat:String
    private lateinit var kota:String
    private lateinit var kodepos:String
    private var totalPrice:Double = 0.0
    private lateinit var balance:String
    private lateinit var points:String
    private lateinit var auth: FirebaseAuth
    private var pointUse:Int = 0
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var userReference: DatabaseReference
    private var transactionId:String= UUID.randomUUID().toString()
    private var totalSet:Double=0.0
    private lateinit var quantityReference: DatabaseReference
    private lateinit var wishRef: DatabaseReference
    private lateinit var currentUserID:String

    private lateinit var pesanId: Button
    private lateinit var total: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_wishlist_detail)

        var tvShipping = findViewById<TextView>(R.id.tvShipping)
        var tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        var sampayPoint = findViewById<TextView>(R.id.sampayPoint)
        var idAlamat = findViewById<TextView>(R.id.idAlamat)
        var editAlamat = findViewById<ImageButton>(R.id.editAlamat)
        var tvNamaPenerima = findViewById<TextView>(R.id.tvNamaPenerima)
        var textPoint = findViewById<TextView>(R.id.textPoint)
        var switchPoin = findViewById<Switch>(R.id.switchPoint)

        totalId = findViewById(R.id.totalId)

        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid.toString()
        userReference = FirebaseDatabase.getInstance().reference.child("users")
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        currentUserID?.let {
            userReference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        username = dataSnapshot.child("name").value.toString()
                        email = dataSnapshot.child("email").value.toString()
                        phone = dataSnapshot.child("phone").value.toString()
                        alamat = dataSnapshot.child("alamat").value.toString()
                        kota = dataSnapshot.child("kota").value.toString()
                        province = dataSnapshot.child("province").value.toString()
                        phone = dataSnapshot.child("phone").value.toString()
                        kodepos = dataSnapshot.child("kodepos").value.toString()
                        points = dataSnapshot.child("points").value?.toString() ?: "0"
                        balance = dataSnapshot.child("balance").value?.toString() ?: "0"
                        tvNamaPenerima.text = username
                        idAlamat.text = "+62" + phone + " " +alamat+", Kota "+kota+", "+province+", "+kodepos
                        textPoint.text = "Tukarkan " + points + " Poin Sampay"
                        sampayPoint.text = points
                        tvSaldo.text = numberToCurrency(balance.toDouble())
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle failure to retrieve data
                }
            })
        }

        val popupHandler = Popup(this)

        val showPopupButton = findViewById<ImageView>(R.id.iPopUpPayment)
        showPopupButton.setOnClickListener {
            popupHandler.showPopup(totalSet)
        }

        val overlayView = findViewById<View>(R.id.overlayView)
        overlayView.setOnClickListener {
            popupHandler.hidePopup()
        }

        wishRef = FirebaseDatabase.getInstance().reference.child("wishlist").child(currentUserID)


        wishList = ArrayList()
        recyclerView = findViewById(R.id.scrollView)
        recycleViewWishListAdapter = RecycleViewWishListAdapter(this, wishList)
        val layoutManager : RecyclerView.LayoutManager = GridLayoutManager(this, 1)
        recyclerView!!.layoutManager = layoutManager
        recyclerView!!.adapter = recycleViewWishListAdapter

        loadWishListData()



        val backbutton = findViewById<ImageButton>(R.id.iBack)
        backbutton.setOnClickListener {
            onBackPressed()
        }

        switchPoin.setOnCheckedChangeListener{buttonView, isChecked->
            if(isChecked){
                totalPrice = totalSet-points.toInt()
                pointUse = points.toInt()
                totalId.setText(numberToCurrency((totalPrice)))
            }
            else{
                totalPrice = (totalPrice)+points.toInt()
                pointUse = pointUse-points.toInt()
                totalId.setText(numberToCurrency((totalPrice)))
            }

        }

        pesanId = findViewById(R.id.pesanId)

        pesanId.setOnClickListener {
            val selectedItem: String = try {
                popupHandler.getSelectedItem()
            } catch (e: Exception) {
                // Handle the exception if needed
                Log.e("Exception", "Error getting selected item: ${e.message}")
                "2" // Default value or value to use in case of exception
            }

            if(selectedItem==(2).toString()){
                val intent = Intent(this, WishlistMidtrans::class.java).apply {
                    putExtra("username", username)
                    putExtra("alamat", alamat)
                    putExtra("phone", phone)
                    putExtra("kota", kota)
                    putExtra("kodepos", kodepos)
                    putExtra("email", email)
                    putExtra("phone", phone)
                    putExtra("totalharga", totalSet)
                    putExtra("pointuse", pointUse)
                    putExtra("item", wishList as ArrayList<ProductVariable>)
                }
                finish()
                startActivity(intent)
            }
            else{
                auth = FirebaseAuth.getInstance()
                database = FirebaseDatabase.getInstance()
                val historyRef = database.reference.child("history").child(transactionId)

                val newOrder = mapOf(
                    "uid" to auth.currentUser!!.uid,
                    "orderId" to transactionId,
                    "type" to "Payment",
                    "datetime" to getCurrentDateTime(),
                    "totalamount" to totalPrice,
                    "pointuse" to pointUse,
                    "status" to "Paid",
                    "items" to wishList.associate { "${it.id}" to true }
                )
                updatePointsToZero()
                historyRef.setValue(newOrder)
                var balance= balance.toDouble()-totalPrice
                currentUserID?.let {
                    val userReference = databaseReference.child(it)
                    databaseReference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            if (dataSnapshot.exists()) {
                                userReference.child("balance").setValue(balance)

                            }
                        }

                        override fun onCancelled(databaseError: DatabaseError) {
                            // Handle failure to retrieve data
                        }
                    })
                }
                onBackPressed()
                finish()
            }

        }
    }

    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
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
        for (product in wishList) {
            val price = extractNumber(product.price)
            val quantity = product.quantity

            totalSet += (price * quantity)
        }

        totalId.text = numberToCurrency(totalSet)
    }
    private fun updatePointsToZero() {

        if ( auth.currentUser!!.uid != null) {
            // Assuming your database structure is "users" -> "userId" -> "points"
            val userPointsRef = database.reference.child("users").child(auth.currentUser!!.uid).child("points")

            // Set points to 0
            userPointsRef.setValue(0)
                .addOnSuccessListener {
                    // Update successful
                    // You can handle success here
                }
                .addOnFailureListener {
                    // Handle failure
                }
        }
    }

    private fun loadWishListData() {
        wishRef.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                wishList.clear()
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
                                itemReference.addListenerForSingleValueEvent(object :
                                    ValueEventListener {
                                    override fun onDataChange(itemSnapshot: DataSnapshot) {
                                        if (itemSnapshot.exists()) {
                                            val title = itemSnapshot.child("title").value.toString()
                                            val image = itemSnapshot.child("image").value.toString()
                                            val price = itemSnapshot.child("price").value.toString()
                                            val description = itemSnapshot.child("description").value.toString()
//                                val quantity = itemSnapshot.child("quantity").value.toString()

                                            val product = ProductVariable(productID, title, image, price, description,quantity.toString().toInt())

                                            wishList.add(product)
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