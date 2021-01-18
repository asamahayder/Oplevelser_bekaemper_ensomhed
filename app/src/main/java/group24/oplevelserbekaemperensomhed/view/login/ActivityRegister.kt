package group24.oplevelserbekaemperensomhed.view.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.airbnb.lottie.LottieAnimationView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.SignInMethodQueryResult
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.LocalData
import kotlinx.android.synthetic.main.activity_register.*

// Handles Registering with Email & Password

class ActivityRegister : AppCompatActivity() {

    private lateinit var editTextViews: Array<EditText>
    private lateinit var lottie: LottieAnimationView

    private val localData = LocalData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initializeView()
    }

    private fun initializeView() {
        // Initialize EditTextViews
        val email: EditText = activity_register_email
        val password: EditText = activity_register_password1
        val passwordCheck: EditText = activity_register_password2
        editTextViews = arrayOf(email, password, passwordCheck)

        // Initialize buttons
        val backButton: ImageView = activity_register_backButton
        val submitButton2: LinearLayout = activity_register_submitButton2

        // initialize loading animation
        lottie = activity_register_loading

        // Handle onClickListener
        onButtonClick(backButton, submitButton2)
    }

    // Handles button clicks
    private fun onButtonClick(
        backButton: ImageView,
        submitButton2: LinearLayout
    ) {
        backButton.setOnClickListener {
            finish()
        }
        submitButton2.setOnClickListener {
            handleSubmitUserDetails()
        }
    }

    // Some messages for the user if something goes wrong
    private fun handleSubmitUserDetails() {
        if (editTextViews[1].text.toString() != editTextViews[2].text.toString()) {
            Toast.makeText(
                applicationContext,
                "Passwords do not match, please try again",
                Toast.LENGTH_SHORT
            ).show()
            return
        } else if (editTextViews[1].text.toString().length <= 7) {
            Toast.makeText(
                applicationContext,
                "Please choose a longer password",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        // Calls the loading animation and checks if the email exists or not
        callLoadingAnimation(true)
        checkEmailExistsOrNot(editTextViews[0].text.toString())
    }

    // Updates Lottie animation
    private fun callLoadingAnimation(playAnimation: Boolean) {
        if (playAnimation) {
            lottie.visibility = View.VISIBLE
        } else {
            lottie.visibility = View.GONE
        }
    }

    // Connects to Firebase and see's if there exists an email or not with the given information
    private fun checkEmailExistsOrNot(email: String) {
        val firebaseauth = FirebaseAuth.getInstance()
        firebaseauth.fetchSignInMethodsForEmail(email).addOnCompleteListener(
            OnCompleteListener<SignInMethodQueryResult> { task ->
                Log.d(TAG, "" + task.result.signInMethods!!.size)
                // If logging in was a success
                if (task.result.signInMethods!!.size == 0) {
                    // Cancels the loading animation and opens up registerUserDetails activity
                    callLoadingAnimation(false)
                    val intent = Intent(this, REGISTERDETAILSACTIVITY)
                    startActivity(intent)
                    finish()
                    // To save CPU usage, set the lottie animation to be GONE after we leave the activity
                    lottie.visibility = View.GONE
                    localData.userEmail = editTextViews[0].text.toString()
                    localData.userPassword = editTextViews[1].text.toString()
                } else {
                    // Updates the animation and notifies the user that something went wrong
                    callLoadingAnimation(false)
                    Toast.makeText(applicationContext,"The email entered is already in use",Toast.LENGTH_SHORT).show()
                }
            }).addOnFailureListener(OnFailureListener { e -> e.printStackTrace() })
    }

    companion object {
        private const val TAG = "ActivityRegister"
        private val REGISTERDETAILSACTIVITY = ActivityRegisterDetails::class.java
    }
}