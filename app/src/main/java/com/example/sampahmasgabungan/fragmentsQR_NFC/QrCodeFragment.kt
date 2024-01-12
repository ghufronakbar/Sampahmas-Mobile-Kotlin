package com.example.sampahmasgabungan.fragmentsQR_NFC

import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.sampahmasgabungan.R
import com.google.android.play.core.integrity.e
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder

class QrCodeFragment : Fragment() {
    private lateinit var iQrUser: ImageView
    private lateinit var tvNamaUser: TextView
    private lateinit var tvPoint: TextView
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_qr_code, container, false)
        iQrUser = view.findViewById(R.id.iQrUser)
        tvNamaUser = view.findViewById(R.id.tvNamaUser)
        tvPoint = view.findViewById(R.id.tvPoint)

        // Mendapatkan UID pengguna yang berhasil login
        val uid = FirebaseAuth.getInstance().currentUser?.uid

        // Inisialisasi DatabaseReference untuk mengakses "users"
        databaseReference = FirebaseDatabase.getInstance().getReference("users")

        // Mendapatkan data pengguna dari Realtime Database
        uid?.let { fetchUserData(it) }

        // Menggunakan UID sebagai content QR code
        val qrContent = uid ?: "DefaultUID"

        // Generate QR code
        val bitmap = generateQrCode(qrContent)

        // Set QR code to ImageView
        iQrUser.setImageBitmap(bitmap)

        // Menambahkan onClickListener untuk ImageView
        iQrUser.setOnClickListener {
            showPopup(bitmap)
        }

        return view
    }

    private fun fetchUserData(uid: String) {
        databaseReference.child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Mengambil data nama dan poin dari "users"
                val nama = dataSnapshot.child("name").getValue(String::class.java)
                val points = dataSnapshot.child("points").getValue(Long::class.java)

                // Menetapkan nilai nama ke TextView
                tvNamaUser.text = nama

                // Menetapkan nilai poin ke TextView
                tvPoint.text = points?.toString() ?: "0"
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error
                tvNamaUser.text = "Error fetching data"
                tvPoint.text = "0"
            }
        })
    }

    private fun generateQrCode(content: String): Bitmap {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix =
                multiFormatWriter.encode(content, com.google.zxing.BarcodeFormat.QR_CODE, 400, 400)
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            // Handle exception
            throw RuntimeException(e)
        }
    }

    private fun showPopup(bitmap: Bitmap) {
        val blurDialogFragment = BlurDialogFragment.newInstance()
        blurDialogFragment.setQrCodeBitmap(bitmap)
        blurDialogFragment.show(childFragmentManager, "BlurDialogFragment")
    }

}