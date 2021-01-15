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

class ActivityStart : AppCompatActivity() {

    private lateinit var firebaseAuth: FirebaseAuth
    private val db = FirebaseDAO()

    private val localData = LocalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
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
                Log.d(TAG, "User authenticated")
                db.getUser(currentUser.uid, object : MyCallBack {
                    override fun onCallBack(`object`: Any) {
                        if (`object` is UserDTO) {
                            Log.d(TAG, "User authenticated")
                            val intent = Intent(applicationContext, MainActivity::class.java)
                            localData.userData = `object` as UserDTO
                            startActivity(intent)
                            finish()
                        } else {
                            Log.d(TAG, "User not authenticated")
                            val intent = Intent(applicationContext, ActivityLogin::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                })
            } else {
                Log.d(TAG, "User not authenticated")
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