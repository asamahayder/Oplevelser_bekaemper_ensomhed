package group24.oplevelserbekaemperensomhed.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R

class AStart : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.astart)
        checkIfLoggedIn()
    }

    override fun onStart() {
        super.onStart()
        if (!isTaskRoot) {
            finish()
            return
        }
    }

    private fun checkIfLoggedIn() {
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        Handler().postDelayed( {
            if (currentUser != null) {
                Log.d(TAG, "AStart: User authenticated")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Log.d(TAG, "AStart: User not authenticated")
                val intent = Intent(this, ALogin::class.java)
                startActivity(intent)
                finish()
            }
        }, 3230)
    }

    companion object {
        private const val TAG = "start"
    }
}