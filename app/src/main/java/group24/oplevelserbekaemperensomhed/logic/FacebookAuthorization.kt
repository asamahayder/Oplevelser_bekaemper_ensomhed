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

    lateinit var callBackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var fb_button_widget : LoginButton

    private val db = FirebaseDAO()
    private val localData = LocalData

    init {
        configure()
    }

    private fun configure() {
        Log.d(TAG, "FacebookAuthorization: configuring")
        firebaseAuth = FirebaseAuth.getInstance()
        callBackManager = CallbackManager.Factory.create()
        fb_button_widget = aLogin.findViewById(R.id.activity_login_fbButton_widget)
        fb_button_widget.setPermissions(readPermissions)
        fb_button_widget.loginBehavior = LoginBehavior.WEB_ONLY
        signInOutCallback()
    }

    private fun signInOutCallback() {
        fb_button_widget.registerCallback(callBackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(TAG, "FacebookAuthorization: callback succeeded")
                val accessToken = result.accessToken
                Log.d(TAG, "Facebook permissions = ${accessToken.permissions}")
                val request = GraphRequest.newMeRequest(accessToken
                ) { `object`, response ->
                    val id = `object`?.getString("id")
                    Log.d(TAG, "Facebook uid is = $id")
                    firebaseAuthWithFacebook(result, id)
                }
                request.executeAsync()
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

    private fun firebaseAuthWithFacebook(result: LoginResult, id: String?) {
        if (id != null) {
            Log.d(TAG, "Check if facebook user already exists in database")
            db.getUser(id, object : MyCallBack {
                override fun onCallBack(`object`: Any) {
                    if (`object` is String) {
                        Log.d(TAG, "facebook user does not exist in database")
                        val credential = FacebookAuthProvider.getCredential(result.accessToken.token)
                        val intent = Intent(aLogin, newActivity)
                        intent.putExtra("facebook",credential)
                        aLogin.startActivity(intent)
                    } else {
                        Log.d(TAG, "facebook user exists in database")
                        val intent = Intent(applicationContext, MainActivity::class.java)
                        localData.userData = `object` as UserDTO
                        startActivity(intent)
                        finish()
                    }
                }
            })
        }
    }

    fun signIn() {
        fb_button_widget.performClick()
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    companion object {
        private const val TAG = "loginFacebook"
        private val readPermissions = listOf("email", "public_profile")
    }
}