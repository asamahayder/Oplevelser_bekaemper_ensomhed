package group24.oplevelserbekaemperensomhed.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.facebook.CallbackManager
import group24.oplevelserbekaemperensomhed.R
import com.facebook.FacebookSdk;
import com.google.firebase.auth.FirebaseAuth


class ALogin : AppCompatActivity() {

    private lateinit var callBackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var fbLoginButton: Button
    private lateinit var gLoginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alogin)
        initializeView()
        initializeAuth()
    }

    private fun initializeView() {
        fbLoginButton = findViewById(R.id.alogin_fbLoginButton)
    }

    private fun initializeAuth() {
        callBackManager = CallbackManager.Factory.create()
        firebaseAuth = FirebaseAuth.getInstance()

        FacebookSdk.sdkInitialize(applicationContext)
    }
}