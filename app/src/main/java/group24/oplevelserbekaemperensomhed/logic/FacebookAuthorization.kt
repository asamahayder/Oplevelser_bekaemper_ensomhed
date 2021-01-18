package group24.oplevelserbekaemperensomhed.logic

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.facebook.*
import com.facebook.login.LoginBehavior
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack
import group24.oplevelserbekaemperensomhed.view.login.ActivityLogin
import group24.oplevelserbekaemperensomhed.view.login.ActivityRegisterDetails

class FacebookAuthorization(private val aLogin: ActivityLogin, private val newActivity: Class<ActivityRegisterDetails>) : AppCompatActivity() {

    // Facebook authorization logic

    lateinit var callBackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fb_button_widget : LoginButton

    init {
        configure()
    }

    // configures the needed variables to handle signin with facebook

    private fun configure() {
        Log.d(TAG, "FacebookAuthorization: configuring")
        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()
        fb_button_widget = aLogin.findViewById(R.id.activity_login_fbButton_widget)
        fb_button_widget.setPermissions(readPermissions)
        fb_button_widget.loginBehavior = LoginBehavior.WEB_ONLY
        signInOutCallback()
    }

    // Calls facebook to see if the login token is valid or not
    // If it is valid the callback returns the result and the login activity can get an credential out of it
    // which is then used to authenticate via firebase authentication

    private fun signInOutCallback() {
        fb_button_widget.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "FacebookAuthorization: callback succeeded")
                val accessToken = result.accessToken
                Log.d(TAG, "Facebook permissions = ${accessToken.permissions}")
                firebaseAuthWithFacebook(result)
            }

            override fun onCancel() {
                Log.d(TAG, "FacebookAuthorization: callback canceled")
                //Toast.makeText(aLogin.applicationContext, "canceled", Toast.LENGTH_SHORT).show()
                //TODO("Not yet implemented")
            }

            override fun onError(error: FacebookException?) {
                Log.d(TAG, "FacebookAuthorization: callback failed", error)
                //Toast.makeText(aLogin.applicationContext, "error", Toast.LENGTH_SHORT).show()
                //TODO("Not yet implemented")
            }
        })
    }

    // Gets the credential and sends the user over to registerDetails activity

    private fun firebaseAuthWithFacebook(result: LoginResult) {
        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
        val intent = Intent(aLogin, newActivity)
        intent.putExtra("facebook",credential)
        aLogin.startActivity(intent)
    }

    // Presses the facebook widget which handles opening the facebook login activity

    fun signIn() {
        fb_button_widget.performClick()
    }

    companion object {
        private const val TAG = "loginFacebook"
        private val readPermissions = listOf("email", "public_profile")
    }
}