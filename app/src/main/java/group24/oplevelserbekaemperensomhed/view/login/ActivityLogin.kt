package group24.oplevelserbekaemperensomhed.view.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.logic.FacebookAuthorization
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack

// Activity that handles login

class ActivityLogin : AppCompatActivity() {

    private lateinit var loginButton: Button
    private lateinit var facebookLoginButton: ImageButton
    private lateinit var forgotPassButton: TextView
    private lateinit var registerButton1: TextView
    private lateinit var registerButton2: TextView

    private lateinit var emailText: EditText
    private lateinit var passText: EditText

    private lateinit var textBlink: Animation

    private lateinit var facebookAuth: FacebookAuthorization
    private lateinit var firebaseAuth: FirebaseAuth

    private val localData = LocalData


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initializeView()
    }

    private fun initializeView() {
        // Initialize views
        loginButton = findViewById(R.id.activity_login_loginButton)
        forgotPassButton = findViewById(R.id.activity_login_forgotPasswordButton)
        facebookLoginButton = findViewById(R.id.activity_login_fbButton)
        registerButton1 = findViewById(R.id.activity_login_registerButton1)
        registerButton2 = findViewById(R.id.activity_login_registerButton2)
        emailText = findViewById(R.id.activity_login_email)
        passText = findViewById(R.id.activity_login_password)

        // Only for test login purposes
        emailText.setText("test@test.com")
        passText.setText("12345678")

        // Handles authorization via Firebase & Facebook
        firebaseAuth = FirebaseAuth.getInstance()
        facebookAuth = FacebookAuthorization(this, REGISTERACTIVITYDETAILS)

        // Initialize TextView animation
        textBlink = AnimationUtils.loadAnimation(this, R.anim.text_blink)

        //Handles button animations
        onTouchListenerAnimation(facebookLoginButton)
        onTouchListenerAnimation(registerButton1)
        onTouchListenerAnimation(registerButton2)
        onTouchListenerAnimation(forgotPassButton)
        onTouchListenerAnimation(loginButton)

        // Handles ActivityChange when clicking forgotten password
        forgotPassButton.setOnClickListener {
            val intent = Intent(this, FORGOTPASSWORDACTIVITY)
            startActivity(intent)
            finish()
        }

    }

    // gets the information back from the facebook activity that was opened when clicking on login with Facebook
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "requestCode: $resultCode resultCode: $resultCode")
        facebookAuth.callBackManager.onActivityResult(requestCode, resultCode, data)
    }

    // Handles touch animations
    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchListenerAnimation(button: Any) {
        val tempButton: Any
        tempButton = if (button is ImageButton) {
            button
        } else if (button is TextView) {
            button
        } else {
            button as Button
        }
        tempButton.setOnTouchListener { v, event ->
            if (event != null) {
                if (event.action == MotionEvent.ACTION_DOWN)
                    // Makes the buttons go dark
                    if (v != null) {
                        v.background.setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP)
                        v.invalidate()
                    }
                // Changes the color back to normal
                if (event.action == MotionEvent.ACTION_UP) {
                    v.background.clearColorFilter()
                    v.invalidate()
                    // Checks which button was pressed
                    if (tempButton == facebookLoginButton) {
                        // Facebook button was pressed
                        facebookAuth.signIn()
                    } else if (tempButton == registerButton1 || tempButton == registerButton2) {
                        // Animates the text with an blink effect
                        registerButton1.startAnimation(textBlink)
                        registerButton2.startAnimation(textBlink)
                        val intent = Intent(this, REGISTERACTIVITY)
                        startActivity(intent)
                    } else if (tempButton == forgotPassButton) {
                        // Animates the text with an blink effect
                        forgotPassButton.startAnimation(textBlink)
                        val intent = Intent(this, FORGOTPASSWORDACTIVITY)
                        startActivity(intent)
                    // if the user presses the normal login button
                    } else if (tempButton == loginButton) {
                        logInWithFirebase(emailText,passText)
                    }
                }
            }
            true
        }
    }

    // Handles checking firebase if the user exists or wrong password and so on
    private fun logInWithFirebase(emailText: EditText, passText: EditText) {
        val db = FirebaseDAO()
        // Some extra checks for the user to see
        if (emailText.text.toString().isEmpty() || !emailText.text.toString().contains("@")) {
            Toast.makeText(applicationContext,"Please enter a valid email",Toast.LENGTH_SHORT).show()
            return
        } else if (passText.text.toString().isEmpty()) {
            Toast.makeText(applicationContext,"Please enter a valid password",Toast.LENGTH_SHORT).show()
            return
        } else if (passText.text.toString().length <= 7) {
            Toast.makeText(applicationContext,"Please enter a password that's at least 8 characters",Toast.LENGTH_SHORT).show()
            return
        } else {
            // Connects to firebase to log in
            firebaseAuth.signInWithEmailAndPassword(emailText.text.toString(),passText.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        db.getUser(currentUser!!.uid, object : MyCallBack {
                            override fun onCallBack(`object`: Any) {
                                localData.userData = `object` as UserDTO
                                localData.id = currentUser!!.uid
                                val intent = Intent(applicationContext, HOMEACTIVITY)
                                startActivity(intent)
                            }
                        })
                    } else {
                        Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    companion object {
        private const val TAG = "login"
        private val HOMEACTIVITY = MainActivity::class.java
        private val REGISTERACTIVITY = ActivityRegister::class.java
        private val REGISTERACTIVITYDETAILS = ActivityRegisterDetails::class.java
        private val FORGOTPASSWORDACTIVITY = ActivityForgotPassword::class.java
    }
}
