package com.example.sampahmasgabungan.payment

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.sampahmasgabungan.HomeView
import com.example.sampahmasgabungan.Loading
import com.example.sampahmasgabungan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
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

class topupMidtrans : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private var transactionId:String=UUID.randomUUID().toString()
    private lateinit var userUid:String
    private var itemPrice:Double=0.0
    private var balance:Int=0
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
            Log.d("Cek Notif", transactionResult.toString())
            if (transactionResult != null) {
                when (transactionResult.status) {
                    UiKitConstants.STATUS_SUCCESS -> {
                        Toast.makeText(this, "Transaction Finished.", Toast.LENGTH_LONG).show()
                        val data = database.reference.child("users").child(userUid).child("balance").setValue(itemPrice.toInt()+balance)
                        val historyRef = database.reference.child("history").child(transactionId)

                        val newOrder = mapOf(
                            "uid" to auth.currentUser!!.uid,
                            "orderId" to transactionId,
                            "type" to "Top Up",
                            "datetime" to getCurrentDateTime(),
                            "totalamount" to itemPrice,
                            "status" to "paid",
                        )
                        historyRef.setValue(newOrder)
                    }
                    UiKitConstants.STATUS_PENDING -> {
                        val historyRef = database.reference.child("history").child(transactionId)

                        val newOrder = mapOf(
                            "uid" to auth.currentUser!!.uid,
                            "orderId" to transactionId,
                            "type" to "Top Up",
                            "datetime" to getCurrentDateTime(),
                            "totalamount" to itemPrice,
                            "status" to "Unpaid"
                        )
                        historyRef.setValue(newOrder)
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
            grossAmount = itemPrice
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_midtrans2)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()


        // Menerima intent
        val intent = intent

        // Mendapatkan data dari intent
        val username = intent.getStringExtra("username")
        val email = intent.getStringExtra("email")
        userUid = intent.getStringExtra("userId").toString()
        balance = (intent.getStringExtra("balance"))?.toInt() ?: 0
        itemPrice = intent.getDoubleExtra("itemPrice", 0.0)


        val shippingAddress = Address(
            username,  // First Name
            null,  // Last Name
            null,  // Address
            null,  // City
            null,  // PostCode
            null,  // Phone Number
            null // Country Code
        )


        var customerDetails = CustomerDetails(
            firstName = username,
            customerIdentifier = email,
            email = email,
            shippingAddress = shippingAddress
        )
        var itemDetails = listOf(ItemDetails("barang-"+email, itemPrice, 1, "topUp"))



        buildUiKit()
        UiKitApi.getDefaultInstance().startPaymentUiFlow(
            activity = this@topupMidtrans,
            launcher = launcher,
            transactionDetails = initTransactionDetails(),
            customerDetails = customerDetails,
            itemDetails = itemDetails
        )
    }
    fun getCurrentDateTime(): String {
        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
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
        supportActionBar?.hide()
    }

    private fun uiKitCustomSetting() {
        val uIKitCustomSetting = UiKitApi.getDefaultInstance().uiKitSetting
        uIKitCustomSetting.saveCardChecked = true
    }
}