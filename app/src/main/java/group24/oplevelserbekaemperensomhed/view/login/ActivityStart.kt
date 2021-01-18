package group24.oplevelserbekaemperensomhed.view.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.logic.firebase.DBUser
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack

// Start screen when opening the app

class ActivityStart : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseDAO()

    private val localData = LocalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        checkIfLoggedIn()
    }

    // Checks to see if the activity is root, if it is not, call finish on it
    override fun onStart() {
        super.onStart()
        if (!isTaskRoot) {
            finish()
            return
        }
    }

    // Checks if the user is logged in when opening the app
    private fun checkIfLoggedIn() {
        firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        // call an handler to run whilst checking and playing the animation
        Handler().postDelayed( {
            if (currentUser != null) {
                db.getUser(currentUser.uid, object : MyCallBack {
                    override fun onCallBack(`object`: Any) {
                        if (`object` is UserDTO) {
                            // If user is authenticated and a user exists on the database, go straight to the MainActivity
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            localData.userData = `object` as UserDTO
                            localData.id = currentUser.uid
                            startActivity(intent)
                            finish()
                        } else {
                            // If he's authenticated but no user exists on the database move to the login activity
                            val intent = Intent(applicationContext, ActivityLogin::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                })
            } else {
                // User was not authenticated and is moved over to login activity
                val intent = Intent(applicationContext, ActivityLogin::class.java)
                startActivity(intent)
                finish()
            }
        }, 3230)
    }

    companion object {
        private const val TAG = "ActivityStart"
    }
}