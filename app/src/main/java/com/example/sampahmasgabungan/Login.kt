package com.example.sampahmasgabungan

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private  lateinit var googleSignInClient: GoogleSignInClient
    lateinit var progressDialog: ProgressDialog
    private  val emailPattern ="[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
    companion object{
        private const val RC_SIGN_IN = 1001
    }

    override fun onStart() {
        super.onStart()
        if(auth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        progressDialog = ProgressDialog(this)
        auth = FirebaseAuth.getInstance()

        //config email pw signin
        val signInEmail: TextInputEditText = findViewById(R.id.signInEmail)
        val signInPassword : TextInputEditText = findViewById(R.id.signInPassword)
        val signInBtn : Button = findViewById(R.id.signInBtn)
        val signUpText : TextView = findViewById(R.id.tSignUp)
        val signInGoogleButton : CardView = findViewById(R.id.cSignInGoogle)

        //Config google signin
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclientid))
            .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        signInGoogleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            startActivityForResult(signIntent, RC_SIGN_IN)
        }
        ///to SignUp
        signUpText.setOnClickListener {
            val intent = Intent(this,Sign_Up::class.java)
            startActivity(intent)
        }
//Sign in email pw
        signInBtn.setOnClickListener {
            val email = signInEmail.text.toString()
            val password = signInPassword.text.toString()

            if(email.isEmpty() || password.isEmpty()){
                if(email.isEmpty()){
                    signInEmail.error = "Enter Your Email address"
                }
                if(password.isEmpty()){
                    signInPassword.error = "Enter Your Password"
                }
                Toast.makeText(this, "Enter Valid Details", Toast.LENGTH_SHORT).show()
            }else if(!email.matches(emailPattern.toRegex())){
                signInEmail.error = "Enter Valid email address"
                Toast.makeText(this, "Enter Valid email address", Toast.LENGTH_SHORT).show()
            }else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if(it.isSuccessful){
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }else{
                        Toast.makeText(this, "Something went wrong, Try Again!",Toast.LENGTH_SHORT).show()
                    }
                }
            }

        }

    }
//sign in google
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e:ApiException){
                e.printStackTrace()
            }
        }

    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        progressDialog.show()
        val credentian = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credentian)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    progressDialog.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                } else {
                    progressDialog.dismiss()
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}