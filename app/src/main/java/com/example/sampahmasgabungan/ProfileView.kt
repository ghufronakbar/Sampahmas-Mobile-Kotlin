package com.example.sampahmasgabungan

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import de.hdodenhof.circleimageview.CircleImageView

class ProfileView : Fragment() {

    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private lateinit var tvName: TextView
    private lateinit var tvPoints: TextView
    private lateinit var tvNik: TextView
    private lateinit var iProfilePicture: CircleImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inisialisasi Firebase
        database = FirebaseDatabase.getInstance().reference
        auth = FirebaseAuth.getInstance()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_view, container, false)

        // Menghubungkan variabel dengan TextView dari layout XML
        tvName = view.findViewById(R.id.tvName)
        tvPoints = view.findViewById(R.id.tvPoints)
        tvNik = view.findViewById(R.id.tvNik)
        iProfilePicture = view.findViewById(R.id.iProfilePicture)

        // Mendapatkan UID pengguna saat ini
        val user = auth.currentUser
        val uid = user?.uid

        val bUnpaid = view.findViewById<ImageButton>(R.id.iUnpaid)
        val bPackaging = view.findViewById<ImageButton>(R.id.iPackaging)
        val bDelivery = view.findViewById<ImageButton>(R.id.iDelivery)
        val bRating = view.findViewById<ImageButton>(R.id.iRating)
        bUnpaid.setOnClickListener {
            val intent = Intent(requireContext(), MyOrders::class.java)
            intent.putExtra("tabToSelect", 0) // Ganti angka disini sesuai dengan tab yang ingin ditampilkan
            startActivity(intent)

        }
        bPackaging.setOnClickListener {
            val intent = Intent(requireContext(), MyOrders::class.java)
            intent.putExtra("tabToSelect", 1) // Ganti angka disini sesuai dengan tab yang ingin ditampilkan
            startActivity(intent)

        }
        bDelivery.setOnClickListener {
            val intent = Intent(requireContext(), MyOrders::class.java)
            intent.putExtra("tabToSelect", 2) // Ganti angka disini sesuai dengan tab yang ingin ditampilkan
            startActivity(intent)
        }
        bRating.setOnClickListener {
            val intent = Intent(requireContext(), MyOrders::class.java)
            intent.putExtra("tabToSelect", 3) // Ganti angka disini sesuai dengan tab yang ingin ditampilkan
            startActivity(intent)
        }


        val editProfileButton = view.findViewById<ImageView>(R.id.iEditProfile)
        editProfileButton.setOnClickListener {
            val intent = context?.let { Intent(it, profil_edits::class.java) }
            startActivity(intent)
        }

        // ... (Repeat similar setup for other buttons)

        // Mendapatkan data dari Firebase
        uid?.let {
            val userRef = database.child("users").child(uid)
            userRef.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val name = snapshot.child("name").value.toString()
                        val points = snapshot.child("point").value?.toString() ?: "0"
                        val nik = snapshot.child("nik").value?.toString() ?: ""
                        val photoUrl = snapshot.child("imageProfile").value?.toString() ?: ""
                        // Set TextView dengan nilai dari Firebase
                        tvName.text = name
                        tvPoints.text = points
                        tvNik.text = nik

                        Glide.with(this@ProfileView)
                            .load(photoUrl)
                            .into(iProfilePicture)

                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle jika ada kesalahan
                    Log.e("Firebase", "Error: ${error.message}")
                }
            })
        }

        return view
    }
}