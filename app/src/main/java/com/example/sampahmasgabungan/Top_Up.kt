package com.example.sampahmasgabungan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.cardview.widget.CardView
import java.text.NumberFormat
import java.util.Locale
import androidx.core.content.ContextCompat
import com.example.sampahmasgabungan.payment.paymentMidtrans
import com.example.sampahmasgabungan.payment.topupMidtrans
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class Top_Up : AppCompatActivity() {
    private lateinit var tvName: TextView
    private lateinit var tvBalance: TextView
    private lateinit var balance :String
    private lateinit var uid :String
    private lateinit var name :String
    private lateinit var email :String
    private lateinit var phone:String
    private lateinit var btnBayarTopup: Button
    private var balanceValueEventListener: ValueEventListener? = null

    private var selectedCardView: CardView? = null
    private var selectedTextView: TextView? = null

    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_top_up)


        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")

        val currentUser = auth.currentUser
        val userUid = currentUser?.uid
        tvName = findViewById(R.id.tvNama)
        tvBalance = findViewById(R.id.tvRp)
        btnBayarTopup = findViewById(R.id.btnBayarTopup)
        userUid?.let {
            balanceValueEventListener =
                databaseReference.child(it).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            name = dataSnapshot.child("name").value.toString()
                            balance = dataSnapshot.child("balance").value?.toString() ?: "0"
                            email = dataSnapshot.child("email").value.toString()
                            phone = dataSnapshot.child("phone").value.toString()
                            uid = dataSnapshot.child("uid").value.toString()

                            tvName.text = name
                            tvBalance.text = " (" + numberToCurrency(balance.toDouble()) +  ")"
                        }
                    }
                    fun numberToCurrency(number: Double): String {
                        val localeID = Locale("id", "ID")
                        val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
                        val formattedValue = currencyFormat.format(number)
                        return formattedValue.replace(",", ".")
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle database error
                    }
                })
        }


        val backButton = findViewById<ImageButton>(R.id.iBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        val etValueDisplay: TextInputEditText = findViewById(R.id.etValue)
        val format = NumberFormat.getNumberInstance(Locale("in", "ID"))

        etValueDisplay.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                etValueDisplay.removeTextChangedListener(this)

                try {
                    var originalString = s.toString()

                    // Remove the dots
                    if (originalString.contains(".")) {
                        originalString = originalString.replace(".", "")
                    }

                    // Parse the clean string back to integer
                    val parsed = originalString.toLong()
                    val formatted = format.format(parsed)

                    // Set the text
                    etValueDisplay.setText(formatted)
                    etValueDisplay.text?.let { etValueDisplay.setSelection(it.length) }
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }

                etValueDisplay.addTextChangedListener(this)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })



        val c20000: CardView = findViewById(R.id.c20000)
        val c50000: CardView = findViewById(R.id.c50000)
        val c100000: CardView = findViewById(R.id.c100000)

        val t20000: TextView = findViewById(R.id.t20000)
        val t50000: TextView = findViewById(R.id.t50000)
        val t100000: TextView = findViewById(R.id.t100000)

        c20000.setOnClickListener { selectCardView(c20000, t20000, 20000) }
        c50000.setOnClickListener { selectCardView(c50000, t50000, 50000) }
        c100000.setOnClickListener { selectCardView(c100000, t100000, 100000) }

        btnBayarTopup.setOnClickListener{
            var total = stToDouble(etValueDisplay.text.toString())
            Log.d("HIIIIIIIIIIIIIIIIII", total.toString())
            val intent = Intent(this, topupMidtrans::class.java).apply {
                putExtra("userId", uid)
                putExtra("username", name)
                putExtra("email", email)
                putExtra("balance", balance)
                putExtra("itemPrice", total)
            }
            startActivity(intent)


        }
    }
    fun stToDouble(currencyString: String): Double {
        // Hapus tanda titik sebagai pemisah ribuan, dan ganti koma menjadi titik untuk pecahan
        val cleanString = currencyString.replace(".", "").replace(",", ".")

        // Konversi ke tipe data Double
        return try {
            cleanString.toDouble()
        } catch (e: NumberFormatException) {
            0.0 // Nilai default jika gagal mengonversi
        }
    }


    private fun selectCardView(cardView: CardView, textView: TextView, value: Int) {
        selectedCardView?.setCardBackgroundColor(ContextCompat.getColor(this, R.color.white))
        selectedTextView?.setTextColor(ContextCompat.getColor(this, R.color.grey))

        cardView.setCardBackgroundColor(ContextCompat.getColor(this, R.color.GreenStatus))
        textView.setTextColor(ContextCompat.getColor(this, R.color.white))

        selectedCardView = cardView
        selectedTextView = textView
        updateValueDisplay(value)
    }

    private fun updateValueDisplay(value: Int) {
        val etValueDisplay: TextInputEditText = findViewById(R.id.etValue)
        val format = NumberFormat.getNumberInstance(Locale("in", "ID"))
        val formattedValue = format.format(value)
        etValueDisplay.setText(formattedValue)
    }
}