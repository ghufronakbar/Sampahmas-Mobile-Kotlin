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
import com.bumptech.glide.Glide
import java.text.NumberFormat
import com.example.sampahmasgabungan.payment.paymentMidtrans
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import java.util.UUID



class Checkout_Detail : AppCompatActivity() {
    private lateinit var username :String
    private lateinit var email :String
    private lateinit var phone:String
    private lateinit var province:String
    private lateinit var nameItem:String
    private lateinit var alamat:String
    private lateinit var kota:String
    private lateinit var kodepos:String
    private var itemPrice:Double = 0.0
    private var totalPrice:Double = 0.0
    private lateinit var balance:String
    private lateinit var idItem:String
    private lateinit var points:String
    private lateinit var auth: FirebaseAuth
    private var selectedItem:String="1"
    private var pointUse:Int = 0
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var transactionId:String=UUID.randomUUID().toString()

    @SuppressLint("MissingInflatedId", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout_detail)

        var tvShipping = findViewById<TextView>(R.id.tvShipping)
        var tvSaldo = findViewById<TextView>(R.id.tvSaldo)
        var sampayPoint = findViewById<TextView>(R.id.sampayPoint)
        var idAlamat = findViewById<TextView>(R.id.idAlamat)
        var editAlamat = findViewById<ImageButton>(R.id.editAlamat)
        var tvNamaPenerima = findViewById<TextView>(R.id.tvNamaPenerima)
        var textPoint = findViewById<TextView>(R.id.textPoint)
        var switchPoin = findViewById<Switch>(R.id.switchPoint)

        editAlamat.setOnClickListener{
            val editIntent = Intent(this, EditAlamat::class.java)
            refreshActivity()
            startActivity(editIntent)
        }

        tvShipping.setText(numberToCurrency(0.0))
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val currentUser = auth.currentUser
        val userUid = currentUser?.uid


        userUid?.let {
            databaseReference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
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
                        val uid = userUid
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

        val backbutton = findViewById<ImageButton>(R.id.iBack)
        backbutton.setOnClickListener {
            onBackPressed()
        }

        val recommended = intent.getParcelableExtra<ProductVariable>("recommended")
        if (recommended != null) {
            val imageObj: ImageView = findViewById(R.id.imageObj)
            val hargaid: TextView = findViewById(R.id.hargaid)
            val desid: TextView = findViewById(R.id.desid)
            val nameObj: TextView = findViewById(R.id.nameObj)
            val titleInside: TextView = findViewById(R.id.titleInside)



            Glide.with(this)
                .load(recommended.image)
                .into(imageObj)
            nameObj.text = recommended.title
            titleInside.text = recommended.title
            hargaid.text = recommended.price
            desid.text = recommended.description

            idItem = recommended.id
            nameItem = recommended.title
            itemPrice = extractNumber(recommended.price)
            totalPrice = extractNumber(recommended.price)


        }


        var totalItemId = findViewById<TextView>(R.id.totalItemId)
        var jmlId = findViewById<TextView>(R.id.jmlId)


        val hargaid: TextView = findViewById(R.id.hargaid)
        val totalId: TextView = findViewById(R.id.totalId)


        var jumlahItem = 1
        totalId.setText(numberToCurrency((itemPrice*jumlahItem)))






        val plusButton = findViewById<ImageButton>(R.id.plusId)
        plusButton.setOnClickListener {
//            var totalItemId = findViewById<TextView>(R.id.totalItemId)
            jumlahItem += 1
            totalItemId.setText(jumlahItem.toString())
            jmlId.setText("x"+jumlahItem.toString())
            totalPrice = itemPrice*jumlahItem
            if(switchPoin.isChecked){
                totalPrice -= points.toInt()
            }

            totalId.setText(numberToCurrency((totalPrice)))
        }

        val minusButton = findViewById<ImageButton>(R.id.minusId)
        minusButton.setOnClickListener {
//            var totalItemId = findViewById<TextView>(R.id.totalItemId)
            if (jumlahItem > 1) {
                jumlahItem -= 1
                totalItemId.text = jumlahItem.toString()
                jmlId.setText("x"+jumlahItem.toString())
                totalPrice = itemPrice*jumlahItem
                if(switchPoin.isChecked){
                    totalPrice -= points.toInt()
                }

                totalId.setText(numberToCurrency((totalPrice)))
            }
        }
        switchPoin.setOnCheckedChangeListener{buttonView, isChecked->
            if(isChecked){
                totalPrice = (itemPrice*jumlahItem)-points.toInt()
                pointUse = points.toInt()
                totalId.setText(numberToCurrency((totalPrice)))
            }
            else{
                totalPrice = (totalPrice)+points.toInt()
                pointUse = pointUse-points.toInt()
                totalId.setText(numberToCurrency((totalPrice)))
            }

        }



        val popupHandler = Popup(this)

        val showPopupButton = findViewById<ImageView>(R.id.iPopUpPayment)
        showPopupButton.setOnClickListener {
            popupHandler.showPopup(totalPrice)
        }

        val overlayView = findViewById<View>(R.id.overlayView)
        overlayView.setOnClickListener {
            popupHandler.hidePopup()
        }


        val pesanId = findViewById<Button>(R.id.pesanId)

        pesanId.setOnClickListener {
            val selectedItem: String = try {
                popupHandler.getSelectedItem()
            } catch (e: Exception) {
                // Handle the exception if needed
                Log.e("Exception", "Error getting selected item: ${e.message}")
                "2" // Default value or value to use in case of exception
            }

            if(selectedItem==(2).toString()){
                val totalharga = jumlahItem*itemPrice
                val intent = Intent(this, paymentMidtrans::class.java).apply {
                    putExtra("idItem", idItem)
                    putExtra("username", username)
                    putExtra("alamat", alamat)
                    putExtra("phone", phone)
                    putExtra("kota", kota)
                    putExtra("kodepos", kodepos)
                    putExtra("email", email)
                    putExtra("phone", phone)
                    putExtra("nameItem", nameItem)
                    putExtra("itemPrice", itemPrice)
                    putExtra("jumlahItem", jumlahItem)
                    putExtra("totalharga", totalharga)
                    putExtra("pointuse", pointUse)
                }
                startActivity(intent)
            }
            else{
                auth = FirebaseAuth.getInstance()
                database = FirebaseDatabase.getInstance()
                val totalharga = jumlahItem*itemPrice
                val historyRef = database.reference.child("history").child(transactionId)

                val newOrder = mapOf(
                    "uid" to auth.currentUser!!.uid,
                    "orderId" to transactionId,
                    "type" to "Payment",
                    "datetime" to getCurrentDateTime(),
                    "totalamount" to totalPrice,
                    "pointuse" to pointUse,
                    "status" to "Paid",
                    "items" to mapOf(
                        idItem to true,
                    )
                )
                updatePointsToZero()
                historyRef.setValue(newOrder)
                var balance= balance.toDouble()-totalPrice
                userUid?.let {
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
    fun refreshActivity() {
        val intent = intent
        finish()  // Menutup aktivitas saat ini
        startActivity(intent)  // Memulai kembali aktivitas
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
}