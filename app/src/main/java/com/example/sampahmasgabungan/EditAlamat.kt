package com.example.sampahmasgabungan


import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class EditAlamat : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_alamat)
        val tvAddress = findViewById<TextInputEditText>(R.id.AAddres)
        val tvCity = findViewById<TextInputEditText>(R.id.ACity)
        val tvKodePos = findViewById<TextInputEditText>(R.id.APostalCode)
        val tvPhone = findViewById<TextInputEditText>(R.id.APhone)
        val tvProvince = findViewById<TextInputEditText>(R.id.AProvince)
        val tvUsername = findViewById<TextInputEditText>(R.id.AName)
        val btnEditAlamat = findViewById<Button>(R.id.tA)

        auth = FirebaseAuth.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference.child("users")
        val currentUser = auth.currentUser
        val userUid = currentUser?.uid

        val backbutton = findViewById<ImageButton>(R.id.iBack)
        backbutton.setOnClickListener {
            onBackPressed()
        }


        btnEditAlamat.setOnClickListener{
            userUid?.let {
                val userReference = databaseReference.child(it)
                userReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        if (dataSnapshot.exists()) {
                            // Periksa apakah "alamat", "name", "province", "kota", dan "phone" ada dalam snapshot sebelum mengakses nilainya
                            val fields = listOf("alamat", "name", "province", "kota", "phone")
                            val editTexts = listOf(tvAddress, tvUsername, tvProvince, tvCity, tvPhone)
                            for (i in fields.indices) {
                                val field = fields[i]
                                val editText = editTexts[i]
                                if (dataSnapshot.hasChild(field)) {
                                    // Dapatkan nilai field yang saat ini ada di database
                                    val currentValue = dataSnapshot.child(field).value.toString()

                                    // Cek apakah nilai saat ini sama dengan nilai baru
                                    if (currentValue != editText.text.toString()) {
                                        // Setel nilai baru ke dalam database
                                        userReference.child(field).setValue(editText.text.toString())

                                        onBackPressed()  // Kembali setelah berhasil memperbarui field
                                    } else {
                                        // Nilai saat ini sudah sama dengan nilai baru, tidak perlu memperbarui
                                        // Tambahkan penanganan lain sesuai kebutuhan
                                    }
                                }
                            }
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Handle failure to retrieve data
                    }
                })
            }
        }






    }
}