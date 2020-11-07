package group24.oplevelserbekaemperensomhed.login

import android.content.Intent
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import group24.oplevelserbekaemperensomhed.AEventSwiper
import group24.oplevelserbekaemperensomhed.R

class GoogleAuthorization(val aLogin: ALogin) : AppCompatActivity() {

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

    public fun signIn() {
        Log.d(TAG, "GoogleAuthorization: signing in")
        val signInIntent = gsc.signInIntent
        aLogin.startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    public fun signOut() {
        Log.d(TAG, "GoogleAuthorization: signing out")
        firebaseAuth.signOut()
    }

    public fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(aLogin) { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "GoogleAuthorization: success")
                    val intent = Intent(aLogin, AEventSwiper::class.java)
                    aLogin.startActivity(intent)
                } else {
                    Log.d(TAG, "GoogleAuthorization: failure")
                }
            }
    }

    companion object {
        private const val TAG = "loginGoogle"
    }
}