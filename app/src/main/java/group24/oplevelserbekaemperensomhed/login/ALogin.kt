package group24.oplevelserbekaemperensomhed.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import group24.oplevelserbekaemperensomhed.AEventSwiper
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R


class ALogin : AppCompatActivity() {

    private val TAG = "login"

    private lateinit var googleAuth: GoogleAuthorization

    private lateinit var callBackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth

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

    private fun initializeAuth() {
        googleAuth = GoogleAuthorization(this)
        //callBackManager = CallbackManager.Factory.create()
        firebaseAuth = FirebaseAuth.getInstance()
        //FacebookSdk.sdkInitialize(applicationContext)
    }

    private fun initializeView() {
        //facebookLogin()
        gLoginButton = findViewById(R.id.alogin_gButton)
        onTouchListenerAnimation(gLoginButton)
    }

//    private fun facebookLogin() {
//        fbLoginButton = findViewById(R.id.alogin_fbButton)
//        onTouchListenerAnimation(fbLoginButton)
//        fbLoginButton_widget = findViewById(R.id.alogin_fbButton_widget)
//        fbLoginButton_widget.loginBehavior = LoginBehavior.WEB_ONLY
//        fbLoginButton_widget.setReadPermissions(listOf("email", "public_profile"))
//        fbLoginButton_widget.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
//            override fun onSuccess(loginResult: LoginResult) {
//                Log.d(TAG, "Facebook login success. Token: " + loginResult.accessToken)
//                handleFacebookAccessToken(loginResult)
//            }
//
//            override fun onCancel() {
//                Log.d(TAG, "Facebook login canceled")
//                Toast.makeText(applicationContext, "canceled", Toast.LENGTH_SHORT).show()
//            }
//
//            override fun onError(error: FacebookException?) {
//                Log.d(TAG, "Facebook login failed")
//                Toast.makeText(applicationContext, "error", Toast.LENGTH_SHORT).show()
//            }
//        })
//    }
//
//    private fun handleFacebookAccessToken(loginResult: LoginResult) {
//        val credential = FacebookAuthProvider.getCredential(loginResult.accessToken.token)
//        firebaseAuth.signInWithCredential(credential)
//            .addOnCompleteListener(this) { task ->
//                if (task.isSuccessful) {
//                    val currentUser = firebaseAuth.currentUser
//                    Log.d(TAG, "Facebook firebase authorization succeeded. User: $currentUser")
//                }
//                else {
//                    Log.d(TAG, "Facebook firebase authorization failed")
//                }
//            }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode: $resultCode resultCode: $resultCode")
        if (requestCode == googleAuth.RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                googleAuth.firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                print(e.stackTrace)
            }
        }
        else {
            //callBackManager.onActivityResult(requestCode, resultCode, data)
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
                        //fbLoginButton_widget.performClick()
                    }
                    if (button == findViewById(R.id.alogin_gButton)) {
                        Log.d(TAG, "Clicking on Google Login")
                        googleAuth.signIn()
                    }
                }
            }
            true
        }
    }
}
