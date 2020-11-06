package group24.oplevelserbekaemperensomhed.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.Api
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import group24.oplevelserbekaemperensomhed.AEventSwiper
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R


class ALogin : AppCompatActivity() {

    private val TAG = "login"

    private lateinit var callBackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var gso: GoogleSignInOptions
    private lateinit var gsc: GoogleSignInClient

    private lateinit var fbLoginButton_widget: LoginButton
    private lateinit var fbLoginButton: ImageButton
    private lateinit var gLoginButton: ImageButton

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
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)
    }

    private fun initializeView() {
        facebookLogin()
        twitterLogin()
        googleLogin()
    }

    private fun facebookLogin() {
        fbLoginButton = findViewById(R.id.alogin_fbButton)
        onTouchListenerAnimation(fbLoginButton)
        fbLoginButton_widget = findViewById(R.id.alogin_fbButton_widget)
        fbLoginButton_widget.setReadPermissions(listOf("email", "public_profile"))
        fbLoginButton_widget.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
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
                    Log.d(TAG, "Facebook firebase authorization succeeded. User: $currentUser")
                    updateUI(currentUser)
                }
                else {
                    Log.d(TAG, "Facebook firebase authorization failed")
                }
            }
    }

    private fun twitterLogin() {

    }

    private fun googleLogin() {
        gLoginButton = findViewById(R.id.alogin_gButton)
        onTouchListenerAnimation(gLoginButton)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode: $resultCode")
        Log.d(TAG, "resultCode: $resultCode")
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                handleGoogleAccessToken(account.idToken!!)
            } catch (e: ApiException) {

            }
        }
        else {
            callBackManager.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun handleGoogleAccessToken(loginResult: String) {
        val credential = GoogleAuthProvider.getCredential(loginResult, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val currentUser = firebaseAuth.currentUser
                    Log.d(TAG, "Google firebase authorization succeeded. User: $currentUser")
                    updateUI(currentUser)
                }
                else {
                    Log.d(TAG, "Google firebase authorization failed")
                }
            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            Log.d(TAG, "Updating UI, changing activity to AEventSwiper")
            startActivity(
                Intent(
                    applicationContext,
                    MainActivity::class.java
                )
            )
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchListenerAnimation(button: ImageButton) {
        button.setOnTouchListener { v, event ->
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN)
                    if (v != null) {
                        v.background.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                        v.invalidate()
                    }
                if (event.action == MotionEvent.ACTION_UP) {
                    v.background.clearColorFilter()
                    v.invalidate()
                    if (button == findViewById(R.id.alogin_fbButton)) {
                        Log.d(TAG, "Clicking on Facebook Login")
                        fbLoginButton_widget.performClick()
                    }
                    if (button == findViewById(R.id.alogin_gButton)) {
                        Log.d(TAG, "Clicking on Google Login")
                        gLoginButton.playSoundEffect(0)
                        val signInIntent = gsc.signInIntent
                        startActivityForResult(signInIntent, RC_SIGN_IN)
                    }
                }
            }
            true
        }
    }
    companion object {
        private const val RC_SIGN_IN = 0
    }
}