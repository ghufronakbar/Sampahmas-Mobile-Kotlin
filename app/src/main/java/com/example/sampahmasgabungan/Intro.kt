package com.example.sampahmasgabungan

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

class Intro : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private  lateinit var googleSignInClient: GoogleSignInClient
    lateinit var progressDialog: ProgressDialog
    companion object{
        private const val RC_SIGN_IN = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        progressDialog = ProgressDialog(this)
        auth = FirebaseAuth.getInstance()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        supportActionBar?.hide()

        val animationRotate = findViewById<ImageView>(R.id.introAnimation)
        val rotate = RotateAnimation(
            -10f, 10f,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        )
        rotate.duration = 500
        rotate.interpolator = LinearInterpolator()
        rotate.repeatMode = Animation.REVERSE
        rotate.repeatCount = Animation.INFINITE

        animationRotate.startAnimation(rotate)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.webclientid))
            .requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        val signinGoogleButton = findViewById<CardView>(R.id.cSignInGoogle)
        signinGoogleButton.setOnClickListener {
            val signIntent = googleSignInClient.signInIntent
            startActivityForResult(signIntent, Intro.RC_SIGN_IN)
        }

        val signinButton = findViewById<Button>(R.id.bSignIn)
        signinButton.setOnClickListener {
            val Intent = Intent(this,Login::class.java)
            startActivity(Intent)
        }

        val signupButton = findViewById<TextView>(R.id.tSignUp)
        signupButton.setOnClickListener {
            val Intent = Intent(this,Sign_Up::class.java)
            startActivity(Intent)
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode== Intro.RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            }catch (e: ApiException){
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