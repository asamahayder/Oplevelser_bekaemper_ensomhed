package group24.oplevelserbekaemperensomhed.view.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.logic.FacebookAuthorization


class ActivityLogin : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var facebookLoginButton: ImageButton
    private lateinit var forgotPassButton: TextView
    private lateinit var registerButton1: TextView
    private lateinit var registerButton2: TextView

    private lateinit var textBlink: Animation

    private lateinit var facebookAuth: FacebookAuthorization
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializeView()

        val testButton: Button = findViewById(R.id.activity_login_testbuutton)
        testButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        Log.d(TAG, "ALogin activity started")
    }

    private fun initializeView() {
        // Initialize views
        loginButton = findViewById(R.id.activity_login_loginButton)
        forgotPassButton = findViewById(R.id.activity_login_forgotPasswordButton)
        facebookLoginButton = findViewById(R.id.activity_login_fbButton)
        registerButton1 = findViewById(R.id.activity_login_registerButton1)
        registerButton2 = findViewById(R.id.activity_login_registerButton2)

        // Handles authorization via firebase
        facebookAuth = FacebookAuthorization(this, NEWACTIVITY)
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize TextView animation
        textBlink = AnimationUtils.loadAnimation(this, R.anim.text_blink)

        //Handles button animations
        onTouchListenerAnimation(facebookLoginButton)
        onTouchListenerAnimation(registerButton1)
        onTouchListenerAnimation(registerButton2)
        onTouchListenerAnimation(forgotPassButton)

        // Handles ActivityChange when clicking forgotten password
        forgotPassButton.setOnClickListener {
            val intent = Intent(this, FORGOTPASSWORDACTIVITY)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode: $resultCode resultCode: $resultCode")
        facebookAuth.callBackManager.onActivityResult(requestCode, resultCode, data)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchListenerAnimation(button: Any) {
        val tempButton: Any
        tempButton = if (button is ImageButton) {
            button as ImageButton
        } else {
            button as TextView
        }
        tempButton.setOnTouchListener { v, event ->
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN)
                    if (v != null) {
                        v.background.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                        v.invalidate()
                    }
                if (event.action == MotionEvent.ACTION_UP) {
                    v.background.clearColorFilter()
                    v.invalidate()
                    if (tempButton == facebookLoginButton) {
                        Log.d(TAG, "Clicking on Facebook Login")
                        facebookAuth.signIn()
                    } else if (tempButton == registerButton1 || tempButton == registerButton2) {
                        registerButton1.startAnimation(textBlink)
                        registerButton2.startAnimation(textBlink)
                        val intent = Intent(this, REGISTERACTIVITY)
                        startActivity(intent)
                    } else if (tempButton == forgotPassButton) {
                        forgotPassButton.startAnimation(textBlink)
                        val intent = Intent(this, FORGOTPASSWORDACTIVITY)
                        startActivity(intent)
                    }
                }
            }
            true
        }
    }

    companion object {
        private const val TAG = "login"
        private val NEWACTIVITY = MainActivity::class.java
        private val REGISTERACTIVITY = ActivityRegister::class.java
        private val FORGOTPASSWORDACTIVITY = ActivityForgotPassword::class.java
    }
}
