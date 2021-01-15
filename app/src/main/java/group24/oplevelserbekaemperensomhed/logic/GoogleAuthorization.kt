package group24.oplevelserbekaemperensomhed.logic

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.view.login.ActivityLogin

class GoogleAuthorization(private val aLogin: ActivityLogin, private val newActivity: Class<MainActivity>) : AppCompatActivity() {

    val RC_SIGN_IN = 404

    private lateinit var gsc: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    init {
        configure()
    }

    private fun configure() {
        Log.d(TAG, "GoogleAuthorization: configuring")
        firebaseAuth = FirebaseAuth.getInstance()
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(aLogin.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(aLogin, gso)
    }

    fun signIn() {
        Log.d(TAG, "GoogleAuthorization: signing in")
        val signInIntent = gsc.signInIntent
        aLogin.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    fun signOut() {
        Log.d(TAG, "GoogleAuthorization: signing out")
        firebaseAuth.signOut()
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(aLogin) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "GoogleAuthorization: firebase auth succeeded")
                    val intent = Intent(aLogin, newActivity)
                    aLogin.startActivity(intent)
                    aLogin.finish()
                } else {
                    Log.d(TAG, "GoogleAuthorization: firebase auth failed")
                }
            }
    }

    companion object {
        private const val TAG = "loginGoogle"
    }
}