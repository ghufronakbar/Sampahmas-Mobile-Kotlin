package com.example.sampahmasgabungan

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.text.HtmlCompat

class TermsConditions : AppCompatActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_conditions)

        val textViewKetentuanUmum = findViewById<TextView>(R.id.textViewKetentuanUmum)
        val textViewRegistrasi = findViewById<TextView>(R.id.textViewRegistrasi)
        val textViewAkun = findViewById<TextView>(R.id.textViewAkun)
        val textViewPenggunaanAplikasi = findViewById<TextView>(R.id.textViewPenggunaanAplikasi)
        val textViewPenjualan = findViewById<TextView>(R.id.textViewPenjualan)
        val textViewKebijakanPrivasi = findViewById<TextView>(R.id.textViewKebijakanPrivasi)
        val textViewLayananPelanggan = findViewById<TextView>(R.id.textViewLayananPelanggan)
        val textViewPenyalahgunaanAplikasi = findViewById<TextView>(R.id.textViewPenyalahgunaanAplikasi)
        val textViewPembubaranAkun = findViewById<TextView>(R.id.textViewPembubaranAkun)
        val textViewPenyelesaianSengketa = findViewById<TextView>(R.id.textViewPenyelesaianSengketa)

        val ketentuanUmumText = getString(R.string.ketentuanUmum)
        val registrasiText = getString(R.string.registrasi)
        val akunText = getString(R.string.akun)
        val penggunaanAplikasiText = getString(R.string.penggunaanAplikasi)
        val penjualanText = getString(R.string.penjualan)
        val kebijakanPrivasiText = getString(R.string.kebijakanPrivasi)
        val layananPelangganText = getString(R.string.layananPelanggan)
        val penyalahgunaanAplikasiText = getString(R.string.penyalahgunaanAplikasi)
        val pembubaranAkunText = getString(R.string.pembubaranAkun)
        val penyelesaianSengketaText = getString(R.string.penyelesaianSengketa)
        textViewKetentuanUmum.text = HtmlCompat.fromHtml(ketentuanUmumText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewRegistrasi.text = HtmlCompat.fromHtml(registrasiText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewAkun.text = HtmlCompat.fromHtml(akunText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewPenggunaanAplikasi.text = HtmlCompat.fromHtml(penggunaanAplikasiText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewPenjualan.text = HtmlCompat.fromHtml(penjualanText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewKebijakanPrivasi.text = HtmlCompat.fromHtml(kebijakanPrivasiText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewLayananPelanggan.text = HtmlCompat.fromHtml(layananPelangganText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewPenyalahgunaanAplikasi.text = HtmlCompat.fromHtml(penyalahgunaanAplikasiText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewPembubaranAkun.text = HtmlCompat.fromHtml(pembubaranAkunText, HtmlCompat.FROM_HTML_MODE_LEGACY)
        textViewPenyelesaianSengketa.text = HtmlCompat.fromHtml(penyelesaianSengketaText, HtmlCompat.FROM_HTML_MODE_LEGACY)


        val acceptButton = findViewById<Button>(R.id.btnAccept)
        acceptButton.setOnClickListener {
            onBackPressed()
        }

        val declineButton = findViewById<Button>(R.id.btnDecline)
        declineButton.setOnClickListener {
            finishAffinity()
        }
    }

}