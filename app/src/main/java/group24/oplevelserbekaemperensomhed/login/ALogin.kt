package group24.oplevelserbekaemperensomhed.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import group24.oplevelserbekaemperensomhed.AEventSwiper
import group24.oplevelserbekaemperensomhed.R


class ALogin : AppCompatActivity() {

    private val TAG = "login"

    private lateinit var callBackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var fbLoginButton: LoginButton
    private lateinit var gLoginButton: LoginButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alogin)
        Log.d(TAG, "ALogin activity started")
        initializeAuth()
        initializeView()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
        updateUI(currentUser)
    }

    private fun initializeAuth() {
        callBackManager = CallbackManager.Factory.create()
        firebaseAuth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(applicationContext)
    }

    private fun initializeView() {
        facebookLogin()
        twitterLogin()
        googleLogin()
    }

    private fun facebookLogin() {
        fbLoginButton = findViewById(R.id.alogin_fbLoginButton)
        fbLoginButton.setReadPermissions(listOf("email", "public_profile"))
        fbLoginButton.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d(TAG, "Facebook login success. Token: " + loginResult.accessToken)
                handleFacebookAccessToken(loginResult)
            }

            override fun onCancel() {
                Log.d(TAG, "Facebook login canceled")
                Toast.makeText(applicationContext, "canceled", Toast.LENGTH_SHORT).show()
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "Facebook login failed")
                Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun handleFacebookAccessToken(loginResult: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    Log.d(TAG, "Firebase authorization succeeded. User: $currentUser")
                    updateUI(currentUser)
                }
                else {
                    Log.d(TAG, "Firebase authorization failed. User not registered")
                }
            }
    }

    private fun twitterLogin() {

    }

    private fun googleLogin() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callBackManager.onActivityResult(requestCode, resultCode, data)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Log.d(TAG, "Updating UI, changing activity to AEventSwiper")
            startActivity(
                Intent(
                    applicationContext,
                    AEventSwiper::class.java
                )
            )
        }
    }
}