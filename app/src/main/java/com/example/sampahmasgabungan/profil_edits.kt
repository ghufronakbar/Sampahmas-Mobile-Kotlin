package com.example.sampahmasgabungan

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class profil_edits : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    private lateinit var storageReference: StorageReference

    private lateinit var editTextName: TextInputEditText
    private lateinit var editTextEmail: TextInputEditText
    private lateinit var editTextPhone: TextInputEditText
    private lateinit var editTextAlamat: TextInputEditText
    private lateinit var iAddPhoto: ImageView
    private var imageUri: Uri? = null
    private lateinit var iProfilePicture: ImageView
    private var useruid:String = ""

    // ...
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil_edits)

        editTextName = findViewById(R.id.editUsername)
        editTextEmail = findViewById(R.id.editEmail)
        editTextPhone = findViewById(R.id.editPhoneNumber)
        editTextAlamat = findViewById(R.id.editAlamat)
        iProfilePicture = findViewById(R.id.iProfilePicture)
        iAddPhoto = findViewById(R.id.iAddPhoto)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        useruid = user?.uid.toString()
        database = FirebaseDatabase.getInstance().reference
        storage = FirebaseStorage.getInstance()
        storageReference = storage.reference
        val backButton: ImageButton = findViewById(R.id.iBack)
        backButton.setOnClickListener {
            onBackPressed()
        }

        iAddPhoto.setOnClickListener {
            chooseImage()
        }



        database.child("users").child(useruid).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.exists()) {
                    val name = dataSnapshot.child("name").getValue(String::class.java) ?: ""
                    val email = dataSnapshot.child("email").getValue(String::class.java) ?: ""
                    var phone = dataSnapshot.child("phone").getValue(String::class.java) ?: ""

                    val alamat = dataSnapshot.child("alamat").getValue(String::class.java) ?: ""
                    val kota = dataSnapshot.child("kota").getValue(String::class.java) ?: ""
                    val province = dataSnapshot.child("province").getValue(String::class.java) ?: ""
                    val kodepos = dataSnapshot.child("kodepos").getValue(String::class.java) ?: ""
                    val photoUrl = dataSnapshot.child("imageProfile").value?.toString() ?: ""

                    // Menambahkan teks "+62" ke nomor telepon jika nomor tidak kosong

                    // Mengisi data ke EditText
                    editTextName.setText(name)
                    editTextEmail.setText(email)
                    editTextPhone.setText(phone)
                    editTextAlamat.setText(alamat + " , " + kota + " , " + province + " , " + kodepos )

                    Glide.with(this@profil_edits)
                        .load(photoUrl)
                        .into(iProfilePicture)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle error jika terjadi pembatalan
            }
        })
        val saveChangesButton = findViewById<Button>(R.id.btSaveChanges)
        saveChangesButton.setOnClickListener {

            val newName = editTextName.text.toString().trim()
            val newEmail = editTextEmail.text.toString().trim()
            val newPhone = editTextPhone.text.toString().trim()

            val addressParts = editTextAlamat.text.toString().split(",")
            if (addressParts.size == 4) {
                val newAlamat = addressParts[0].trim()
                val newKota = addressParts[1].trim()
                val newProvince = addressParts[2].trim()
                val newkodepos = addressParts[3].trim()

                // Update data pada Firebase Realtime Database

                val userData = HashMap<String, Any>()
                userData["alamat"] = newAlamat
                userData["kota"] = newKota
                userData["province"] = newProvince
                userData["kodepos"] = newkodepos
                userData["name"] = newName
                userData["email"] = newEmail
                userData["phone"] = newPhone

                database.child("users").child(useruid).updateChildren(userData)
                    .addOnSuccessListener {
                        uploadImage()
                    }
                    .addOnFailureListener {

                    }
                onBackPressed()
                // Update the database here
            } else {
                Toast.makeText(this, "Please enter a valid address with three parts separated by commas", Toast.LENGTH_LONG).show()
            }

        }
        // ...
    }


    private fun chooseImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            imageUri = data.data
            iProfilePicture.setImageURI(imageUri)
        }
    }
    private fun uploadImage() {
        if (imageUri != null) {
            val imageRef = storageReference.child("profiles/${auth.currentUser?.uid}")
            Log.d("TAG", "uploadImage: $imageRef")
            imageRef.putFile(imageUri!!)
                .addOnSuccessListener {

                    imageRef.downloadUrl.addOnSuccessListener { uri ->
                        // Simpan link foto ke Firebase Realtime Database
                        val photoUrl = uri.toString()
                        database.child("users").child(auth.currentUser?.uid!!).child("imageProfile").setValue(photoUrl)
//                        Toast.makeText(this, "Image uploaded successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    // Handle failure
//                    Toast.makeText(this, "Failed to upload image", Toast.LENGTH_SHORT).show()
                }
        }
    }
// ...

}