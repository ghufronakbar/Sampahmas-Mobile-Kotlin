package com.example.sampahmasgabungan

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sampahmasgabungan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale

class Balance : AppCompatActivity() {
    private lateinit var tvPoints: TextView
    private lateinit var tvSaldo: TextView
    private var balanceValueEventListener: ValueEventListener? = null
    private var transactionList = mutableListOf<TransactionModel>()
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_balance)
        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val currentUser = auth.currentUser
        val userUid = currentUser?.uid
        tvPoints = findViewById(R.id.tvPoint)
        tvSaldo = findViewById(R.id.tvSaldo)
        val backbutton = findViewById<ImageButton>(R.id.iBack)
        val topUpButton = findViewById<ImageButton>(R.id.iTopUp)

        backbutton.setOnClickListener {
            onBackPressed()
        }

        topUpButton.setOnClickListener {
            val intent = Intent(this, Top_Up::class.java)
            startActivity(intent)
        }

        val recyclerView: RecyclerView = findViewById(R.id.recyclerViewHistoryTopup)

        if (userUid != null) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("history")
            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (transactionSnapshot in dataSnapshot.children) {
                        val transactionKey = transactionSnapshot.key

                        databaseReference.child(transactionKey.toString())
                            .addValueEventListener(object : ValueEventListener {
                                override fun onDataChange(transactionDataSnapshot: DataSnapshot) {
                                    if  (transactionDataSnapshot.exists()) {
                                        val uid = transactionDataSnapshot.child("uid").value.toString()
                                        Log.d("AAAAAAAAT", "UID: $uid")
                                        // Periksa UID
                                        if (uid == userUid) {
                                            val title = transactionDataSnapshot.child("type").value.toString()
                                            val amount = transactionDataSnapshot.child("totalamount").value.toString()
                                            val date = transactionDataSnapshot.child("datetime").value.toString()


                                            val transaction = TransactionModel(title, date, amount)

                                            transactionList.add(transaction)

                                            val adapter = TransactionAdapter(this@Balance, transactionList)
                                            recyclerView.adapter = adapter
                                            recyclerView.layoutManager = LinearLayoutManager(this@Balance)
                                        }
                                    }
                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    // Handle error
                                }
                            })

                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle error
                }
            })
        }
        Log.d("AAAAAAAAT", "UVVU: $transactionList")





        userUid?.let {
            balanceValueEventListener =
                databaseReference.child(it).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            val name = dataSnapshot.child("name").value.toString()
                            val points = dataSnapshot.child("points").value?.toString() ?: "0"
                            val balance = dataSnapshot.child("balance").value?.toString() ?: "0"

                            tvPoints.text = points
                            tvSaldo.text = numberToCurrency(balance.toDouble())
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle database error
                    }
                })
        }
    }

    fun numberToCurrency(number: Double): String {
        val localeID = Locale("id", "ID")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
        val formattedValue = currencyFormat.format(number)
        return formattedValue.replace(",", ".")
    }
}
