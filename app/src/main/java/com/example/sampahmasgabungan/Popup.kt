import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import com.example.sampahmasgabungan.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.NumberFormat
import java.util.Locale



class Popup(private val context: Context) {

    private var dialog: Dialog? = null
    private var selectedText:String = "2"
    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private var isRadioButtonSelected = false


    fun showPopup(total:Double) {
        // Inflate the popup layout
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.activity_popup, null)
        val sampaySaldoTextView = popupView.findViewById<TextView>(R.id.sampaySaldo)
        val rbSampay = popupView.findViewById<RadioButton>(R.id.rbSampay)


        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        val userUid = currentUser?.uid
        val databaseReference = FirebaseDatabase.getInstance().reference.child("users")
        userUid?.let {
            databaseReference.child(it).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    if (dataSnapshot.exists()) {
                        val balance = dataSnapshot.child("balance").value?.toString() ?: "0"

                        var curBalance = numberToCurrency(balance.toDouble())
                        sampaySaldoTextView.text = curBalance
                        if (balance.toDouble() < total) {
                            rbSampay.isEnabled = false
                            // Optionally, you can show a warning message
                            Toast.makeText(context, "Insufficient balance to choose this option", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle failure to retrieve data
                }
            })
        }




        // Create the custom dialog
        dialog = Dialog(context)
        dialog?.setContentView(popupView)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        // Show the dialog
        val layoutParams = dialog?.window?.attributes
        layoutParams?.gravity = Gravity.BOTTOM
        layoutParams?.width = WindowManager.LayoutParams.MATCH_PARENT
        dialog?.window?.attributes = layoutParams

        dialog?.show()
        val overlayView = (context as Activity).findViewById<View>(R.id.overlayView)
        overlayView.visibility = View.VISIBLE

        val bSavePayment = popupView.findViewById<Button>(R.id.bSavePayment)
        val radioGroup = popupView.findViewById<RadioGroup>(R.id.radioGroup)

        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = popupView.findViewById<RadioButton>(checkedId)
            selectedText = radioButton.text.toString()
            isRadioButtonSelected = true
        }
        bSavePayment.setOnClickListener {
            hidePopup()
        }
    }

    fun hidePopup() {
        if (isRadioButtonSelected) {
            // Dismiss the dialog
            dialog?.dismiss()
            dialog = null
            val overlayView = (context as Activity).findViewById<View>(R.id.overlayView)
            overlayView.visibility = View.GONE
        } else {
            // Tampilkan pesan bahwa pengguna harus memilih radio button
            Toast.makeText(context, "Please select a radio button", Toast.LENGTH_SHORT).show()
        }
    }

    fun getSelectedItem(): String {
        return selectedText
    }
    fun numberToCurrency(number: Double): String {
        val localeID = Locale("id", "ID")
        val currencyFormat = NumberFormat.getCurrencyInstance(localeID)
        val formattedValue = currencyFormat.format(number)
        return formattedValue.replace(",", ".")
    }
}
