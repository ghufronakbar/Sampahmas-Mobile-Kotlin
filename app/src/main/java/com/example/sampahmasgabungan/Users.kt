package com.example.sampahmasgabungan

data class Users(
    var name : String? = null,
    var email : String? = null,
    var nik : String? = null,
    var uid : String? = null,
    val points: Int, // Menambahkan Points
    val balance: Int,
    val botolTerkumpul: Int, // Menambahkan Botol terkumpul
    val phoneNumber: String? = null, // Tambahkan nomor telepon
    val imageProfile: String = "",
    val alamat: String? = null,
    val kota: String? = null,
    val kodepos: String? = null,
    val province: String? = null,
    val phone: String? = null
)
