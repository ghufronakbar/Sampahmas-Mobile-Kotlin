package com.example.sampahmasgabungan

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlin.math.sign

class Sign_Up : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var  database: FirebaseDatabase
    private  val emailPattern ="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val termsButton = findViewById<TextView>(R.id.termsConditions)
        termsButton.setOnClickListener {
            val Intent = Intent(this,TermsConditions::class.java)
            startActivity(Intent)
        }

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()

        val signUpName : TextInputEditText = findViewById(R.id.signUpName)
        val signUpEmail : TextInputEditText = findViewById(R.id.signUpEmail)
        val signUpNik : TextInputEditText = findViewById(R.id.signUpNik)
        val signUpPassword : TextInputEditText = findViewById(R.id.signUpPassword)
        val signUpCpassword : TextInputEditText = findViewById(R.id.signUpCpassword)
        val signUpButton : Button = findViewById(R.id.tSignUp)
        val signInButton : TextView = findViewById(R.id.tSignIn)

        signInButton.setOnClickListener {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
        }

        signUpButton.setOnClickListener {
            val name = signUpName.text.toString()
            val email = signUpEmail.text.toString()
            val nik = signUpNik.text.toString()
            val password = signUpPassword.text.toString()
            val cPassword = signUpCpassword.text.toString()


            if(name.isEmpty() || email.isEmpty() || nik.isEmpty() || password.isEmpty() || cPassword.isEmpty()){
                if(name.isEmpty()){
                    signUpName.error = "Please Enter Your Name"
                }
                if(email.isEmpty()){
                    signUpEmail.error = "Please Enter Your Email"
                }
                if(nik.isEmpty()){
                    signUpNik.error = "Please Enter Your Nik"
                }
                if(password.isEmpty()){
                    signUpPassword.error = "Please Enter Your Password"
                }
                if(cPassword.isEmpty()){
                    signUpCpassword.error = "Please Re Enter Your Password"
                }
                Toast.makeText(this, "Enter Valid Details", Toast.LENGTH_SHORT).show()

            }else if(!email.matches(emailPattern.toRegex())){
                signUpEmail.error = "Enter Valid email address"
                Toast.makeText(this, "Enter valid email address", Toast.LENGTH_SHORT).show()
            }else if(nik.length <7){
                signUpNik.error = "Enter Valid Nik"
                Toast.makeText(this, "Enter Valid Nik", Toast.LENGTH_SHORT).show()
            }else if(password.length <4){
                signUpPassword.error = "Enter Password more than 4 characters"
                Toast.makeText(this, "Enter Password more than 4 characters", Toast.LENGTH_SHORT).show()
            }else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val databaseRef = database.reference.child("users").child(auth.currentUser!!.uid)
                        val users = Users(name, email, nik, auth.currentUser!!.uid, 0, 0,0, null, "", "","","", "", "")
                        databaseRef.setValue(users).addOnCompleteListener { innerTask ->
                            if (innerTask.isSuccessful) {
                                val intent = Intent(this, Login::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(this, "Something went Wrong, Try Again!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this, "Something went Wrong, Try Again!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}