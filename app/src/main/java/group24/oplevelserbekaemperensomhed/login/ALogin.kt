package group24.oplevelserbekaemperensomhed.login

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.AEventSwiper
import group24.oplevelserbekaemperensomhed.R


class ALogin : AppCompatActivity() {

    private lateinit var googleAuth: GoogleAuthorization
    private lateinit var facebookAuth: FacebookAuthorization
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.alogin)
        initializeView()
        Log.d(TAG, "ALogin activity started")
    }

    private fun initializeView() {
        //Handles authorization via firebase
        googleAuth = GoogleAuthorization(this, NEWACTIVITY)
        facebookAuth = FacebookAuthorization(this, NEWACTIVITY)
        firebaseAuth = FirebaseAuth.getInstance()

        //Handles button animations and sign in initialization
        onTouchListenerAnimation(findViewById(R.id.alogin_fbButton))
        onTouchListenerAnimation(findViewById(R.id.alogin_gButton))
    }

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
            facebookAuth.callBackManager.onActivityResult(requestCode, resultCode, data)
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
                        facebookAuth.signIn()
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

    companion object {
        private const val TAG = "login"
        private val NEWACTIVITY = AEventSwiper::class.java
    }
}
