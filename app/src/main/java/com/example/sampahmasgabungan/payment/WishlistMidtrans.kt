package com.example.sampahmasgabungan.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sampahmasgabungan.Loading
import com.example.sampahmasgabungan.ProductVariable
import com.example.sampahmasgabungan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.midtrans.sdk.uikit.api.model.Address
import com.midtrans.sdk.uikit.api.model.CustomColorTheme
import com.midtrans.sdk.uikit.api.model.CustomerDetails
import com.midtrans.sdk.uikit.api.model.ItemDetails
import com.midtrans.sdk.uikit.api.model.SnapTransactionDetail
import com.midtrans.sdk.uikit.api.model.TransactionResult
import com.midtrans.sdk.uikit.external.UiKitApi
import com.midtrans.sdk.uikit.internal.util.UiKitConstants
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

class WishlistMidtrans : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private var wishList = mutableListOf<ProductVariable>()
    private lateinit var wishRef: DatabaseReference
    private var transactionId:String= UUID.randomUUID().toString()
    private lateinit var currentUserID:String
    private var totalharga:Double=0.0
    private var pointUse:Int=0
    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result?.resultCode == RESULT_OK) {
                result.data?.let {
                    val transactionResult = it.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
                    Toast.makeText(this, "${transactionResult?.transactionId}", Toast.LENGTH_LONG).show()
                }
            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val transactionResult = data?.getParcelableExtra<TransactionResult>(UiKitConstants.KEY_TRANSACTION_RESULT)
            if (transactionResult != null) {
                when (transactionResult.status) {
                    UiKitConstants.STATUS_SUCCESS -> {
                        Toast.makeText(this, "Transaction Finished.", Toast.LENGTH_LONG).show()
                        val wishlistReff = database.reference.child("wishlist").child(currentUserID).removeValue()
                        val historyRef = database.reference.child("history").child(transactionId)
                        val dataItem = wishList.associate { "${it.id}" to mapOf( "quantity" to "${it.quantity}") }
                        val newOrder = mapOf(
                            "uid" to auth.currentUser!!.uid,
                            "orderId" to transactionId,
                            "type" to "Payment",
                            "datetime" to getCurrentDateTime(),
                            "totalamount" to totalharga,
                            "pointuse" to pointUse,
                            "status" to "Unpaid",
                            "items" to dataItem
                        )

                        historyRef.setValue(newOrder)
                        updatePointsToZero()
                        val intent = Intent(this, Loading::class.java)
                        startActivity(intent)
                    }
                    UiKitConstants.STATUS_PENDING -> {
                        val wishlistReff = database.reference.child("wishlist").child(currentUserID).removeValue()
                        val historyRef = database.reference.child("history").child(transactionId)
                        val dataItem = wishList.associate { "${it.id}" to mapOf( "quantity" to "${it.quantity}") }
                        val newOrder = mapOf(
                            "uid" to auth.currentUser!!.uid,
                            "orderId" to transactionId,
                            "type" to "Payment",
                            "datetime" to getCurrentDateTime(),
                            "totalamount" to totalharga,
                            "pointuse" to pointUse,
                            "status" to "Unpaid",
                            "items" to dataItem
                        )
                        historyRef.setValue(newOrder)
                        updatePointsToZero()
                        Toast.makeText(this, "Transaction Pending.", Toast.LENGTH_LONG).show()
                    }
                    UiKitConstants.STATUS_FAILED -> {
                        Toast.makeText(this, "Transaction Failed.", Toast.LENGTH_LONG).show()
                    }
                    UiKitConstants.STATUS_CANCELED -> {
                        Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_LONG).show()
                    }
                    UiKitConstants.STATUS_INVALID -> {
                        Toast.makeText(this, "Transaction Invalid.", Toast.LENGTH_LONG).show()
                    }
                    else -> {
                        Toast.makeText(this, "Transaction Error.", Toast.LENGTH_LONG).show()
                    }
                }

            } else {
                Toast.makeText(this, "Transaction Invalid.", Toast.LENGTH_LONG).show()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
        finish()
    }

    private fun initTransactionDetails() : SnapTransactionDetail {
        return SnapTransactionDetail(
            orderId = transactionId,
            grossAmount = totalharga
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist_midtrans)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        auth = FirebaseAuth.getInstance()
        currentUserID = auth.currentUser?.uid.toString()
        wishRef = FirebaseDatabase.getInstance().reference.child("wishlist").child(currentUserID)



        // Menerima intent
        val intent = intent

        // Mendapatkan data dari intent
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        val alamat = intent.getStringExtra("alamat")
        val kota = intent.getStringExtra("kota")
        val kodepos = intent.getStringExtra("kodepos")
        val phone = intent.getStringExtra("phone")
        wishList = (intent.getSerializableExtra("item") as ArrayList<ProductVariable>?)!!
//        Log.d("AAAAAAAAAAAAAAAAAAAAAAAAAAA", "onCreate: $item")
        pointUse = intent.getIntExtra("pointuse", 0)
        totalharga = intent.getDoubleExtra("totalharga", 0.0)
        val shippingAddress = Address(
            username,  // First Name
            null,  // Last Name
            alamat,  // Address
            kota,  // City
            kodepos,  // PostCode
            phone,  // Phone Number
            "IDN" // Country Code
        )


        var customerDetails = CustomerDetails(
            firstName = username,
            customerIdentifier = email,
            email = email,
            shippingAddress = shippingAddress
        )

        val itemDetailsList = mutableListOf<ItemDetails>()
        wishList.forEach { product ->
            val itemDetail = ItemDetails(
                name = product.title,
                price = extractNumber(product.price),
                quantity = product.quantity,
                id = "${product.id}"
            )
            itemDetailsList.add(itemDetail)
        }
        Log.d("PointUsee", "onCreate: $pointUse")
        if (pointUse > 0) {
            totalharga = totalharga-pointUse
            val voucherItemDetail = ItemDetails(
                name = "Point",
                price = -pointUse.toDouble(), // Harga voucher dianggap negatif
                quantity = 1,
                id = "Point"
            )

            // Tambahkan itemDetail voucher ke dalam itemDetailsList
            itemDetailsList.add(voucherItemDetail)
        }

        buildUiKit()
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            activity = this@WishlistMidtrans,
            launcher = launcher,
            transactionDetails = initTransactionDetails(),
            customerDetails = customerDetails,
            itemDetails = itemDetailsList
        )
    }

    private fun buildUiKit() {
        UiKitApi.Builder()
            .withContext(this.applicationContext)
            .withMerchantUrl("https://sampahmastest.000webhostapp.com/index.php/")
            .withMerchantClientKey("SB-Mid-client-Bm1minQ11vE6w4XE")
            .enableLog(true)
            .withColorTheme(CustomColorTheme("#000000", "#000000", "#FF0000"))
            .build()
        uiKitCustomSetting()
//        supportActionBar?.hide()
    }

    private fun uiKitCustomSetting() {
        val uIKitCustomSetting = UiKitApi.getDefaultInstance().uiKitSetting
        uIKitCustomSetting.saveCardChecked = true
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
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }
    fun extractNumber(input: String): Double {
        val numericChars = input.filter { it.isDigit() }
        return numericChars.toDoubleOrNull() ?: 0.0
    }
}