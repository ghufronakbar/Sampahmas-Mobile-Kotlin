package com.example.sampahmasgabungan

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupWindow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.sampahmasgabungan.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*
import android.Manifest
import android.view.View
import androidx.core.content.ContextCompat


data class LocationData(val title: String, val latitude: Double, val longitude: Double)

class Nearby : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var mGoogleMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: LatLng? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nearby)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap

        // Meminta izin lokasi jika belum ada
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mGoogleMap?.isMyLocationEnabled = true
            getCurrentLocation()
            addVendingMachineMarkers() // Tambahkan marker untuk lokasi vending machine

            // Tetapkan OnMarkerClickListener
            mGoogleMap?.setOnMarkerClickListener(this)
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }


    override fun onMarkerClick(marker: Marker): Boolean {
        // Menanggapi klik pada marker
        marker.tag?.let { showMarkerInfoWindow(it as LocationData) }
        return true
    }

    private fun showMarkerInfoWindow(location: LocationData) {
        // Menampilkan pop-up window dengan informasi lokasi
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.marker_info_window, null)

        // Inisialisasi elemen UI pada pop-up window
        val tvTitle = popupView.findViewById<TextView>(R.id.tvTitle)
        val tvAddress = popupView.findViewById<TextView>(R.id.tvAddress)
        val btnGetDirections = popupView.findViewById<Button>(R.id.btnGetDirections)

        // Set informasi lokasi pada elemen UI
        tvTitle.text = location.title
        tvAddress.text = getAddress(location.latitude, location.longitude)

        // Buat pop-up window
        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        // Atur animasi dan tata letak
        popupWindow.animationStyle = R.style.PopupAnimation
        popupWindow.showAtLocation(popupView, Gravity.BOTTOM, 0, 50)

        // Menangani klik pada tombol "Get Directions"
        btnGetDirections.setOnClickListener {
            showDirections(location.latitude, location.longitude)
            popupWindow.dismiss() // Tutup pop-up setelah mengklik tombol
        }
    }


    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                // Mengatur pusat peta ke lokasi terkini
                location?.let {
                    currentLocation = LatLng(it.latitude, it.longitude)
                    mGoogleMap?.moveCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(it.latitude, it.longitude),
                            HIGH_ZOOM_LEVEL.toFloat()
                        )
                    )

                    // Menambahkan penanda ke lokasi terkini (merah)
                    addMarker(it.latitude, it.longitude, "Lokasi Saya", Color.RED)
                }
            }
    }

    private fun addMarker(latitude: Double, longitude: Double, title: String, color: Int) {
        mGoogleMap?.addMarker(
            MarkerOptions()
                .position(LatLng(latitude, longitude))
                .title(title)
                .icon(BitmapDescriptorFactory.defaultMarker(getHue(color)))
        )?.tag = LocationData(title, latitude, longitude)
    }

    private fun addVendingMachineMarkers() {
        val vendingMachineLocations = listOf(
            LocationData("Vending Machine 1", -7.744125629388798, 110.37780321094755),
            LocationData("Vending Machine 2", -7.6853386161232935, 110.41386600863144)
            // Anda dapat menambahkan lokasi vending machine lainnya sesuai kebutuhan
        )

        for (location in vendingMachineLocations) {
            location.let {
                // Menambahkan penanda untuk lokasi vending machine (warna Downy)
                val markerOptions = MarkerOptions()
                    .position(LatLng(it.latitude, it.longitude))
                    .title(it.title)
                    .icon(BitmapDescriptorFactory.defaultMarker(getHue(ContextCompat.getColor(this, R.color.Downy))))

                // Menambahkan marker ke peta
                val marker = mGoogleMap?.addMarker(markerOptions)

                // Set tag pada marker
                marker?.tag = it
            }
        }

        // Set custom info window adapter
        mGoogleMap?.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                return null // Gunakan latar info window default
            }

            override fun getInfoContents(marker: Marker): View {
                val view = layoutInflater.inflate(R.layout.custom_info_window, null)

                val markerTitle = view.findViewById<TextView>(R.id.markerTitle)
                val markerSnippet = view.findViewById<TextView>(R.id.markerSnippet)

                val locationData = marker.tag as? LocationData
                locationData?.let {
                    markerTitle.text = it.title
                    markerSnippet.text = getAddress(it.latitude, it.longitude)
                }

                return view
            }
        })
    }



    private fun getAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val addresses: List<Address>? = geocoder.getFromLocation(latitude, longitude, 1)
            if (addresses != null && addresses.isNotEmpty()) {
                val address: Address = addresses[0]
                return address.getAddressLine(0) ?: ""
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return ""
    }


    private fun showDirections(destLatitude: Double, destLongitude: Double) {
        val uri =
            "http://maps.google.com/maps?saddr=${currentLocation?.latitude},${currentLocation?.longitude}&daddr=$destLatitude,$destLongitude"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        intent.setPackage("com.google.android.apps.maps")
        startActivity(intent)
    }

    private fun getHue(color: Int): Float {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        return hsv[0]
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Izin diberikan, dapatkan lokasi terkini
                    getCurrentLocation()
                }
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val HIGH_ZOOM_LEVEL = 17
    }
}
