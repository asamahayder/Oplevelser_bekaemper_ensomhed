package group24.oplevelserbekaemperensomhed.login

import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.FacebookSdk
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.AEventSwiper
import group24.oplevelserbekaemperensomhed.R

class FacebookAuthorization(val aLogin: ALogin, val newActivity: Class<AEventSwiper>) : AppCompatActivity() {

    lateinit var callBackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fbLoginButton_widget : LoginButton

    init {
        configure()
    }

    private fun configure() {
        Log.d(TAG, "FacebookAuthorization: configuring")
        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()
        FacebookSdk.sdkInitialize(aLogin.applicationContext)
        fbLoginButton_widget = aLogin.findViewById(R.id.alogin_fbButton_widget)
        fbLoginButton_widget.setReadPermissions(readPermissions)
        fbLoginButton_widget.loginBehavior = LoginBehavior.WEB_ONLY
        signInOutCallback()
    }

    private fun signInOutCallback() {
        fbLoginButton_widget.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "FacebookAuthorization: callback succeeded")
                firebaseAuthWithFacebook(result)
            }

            override fun onCancel() {
                Log.d(TAG, "FacebookAuthorization: callback canceled")
                //Toast.makeText(aLogin.applicationContext, "canceled", Toast.LENGTH_SHORT).show()
                TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "FacebookAuthorization: callback failed", error)
                //Toast.makeText(aLogin.applicationContext, "error", Toast.LENGTH_SHORT).show()
                TODO("Not yet implemented")
            }
        })
    }

    private fun firebaseAuthWithFacebook(result: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(aLogin) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "FacebookAuthorization: firebase auth succeeded")
                    val intent = Intent(aLogin, newActivity)
                    aLogin.startActivity(intent)
                } else {
                    Log.d(TAG, "FacebookAuthorization: firebase auth failed")
                    TODO("Not yet implemented")
                }
            }
    }

    public fun signIn() {
        fbLoginButton_widget.performClick()
    }

    public fun signOut() {
        firebaseAuth.signOut()
    }

    companion object {
        private const val TAG = "loginFacebook"
        private val readPermissions = listOf("email", "public_profile")
    }
}