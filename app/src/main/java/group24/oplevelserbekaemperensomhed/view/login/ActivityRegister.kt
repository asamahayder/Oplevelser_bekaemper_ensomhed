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
        val submitButton1: ImageView = activity_register_submitButton1
        val submitButton2: LinearLayout = activity_register_submitButton2

        // initialize loading animation
        lottie = activity_register_loading

        // Handle onClickListener
        onButtonClick(backButton, submitButton1, submitButton2)
    }

    private fun onButtonClick(
        backButton: ImageView,
        submitButton1: ImageView,
        submitButton2: LinearLayout
    ) {
        backButton.setOnClickListener {
            finish()
        }
        submitButton1.setOnClickListener {
            handleSubmitUserDetails()
        }
        submitButton2.setOnClickListener {
            handleSubmitUserDetails()
        }
    }

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
        callLoadingAnimation(true)
        checkEmailExistsOrNot(editTextViews[0].text.toString())
    }

    private fun callLoadingAnimation(playAnimation: Boolean) {
        if (playAnimation) {
            lottie.visibility = View.VISIBLE
        } else {
            lottie.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "ActivityRegister"
        private val REGISTERDETAILSACTIVITY = ActivityRegisterDetails::class.java
    }

    private fun checkEmailExistsOrNot(email: String) {
        val firebaseauth = FirebaseAuth.getInstance()
        firebaseauth.fetchSignInMethodsForEmail(email).addOnCompleteListener(
            OnCompleteListener<SignInMethodQueryResult> { task ->
                Log.d(TAG, "" + task.result.signInMethods!!.size)
                if (task.result.signInMethods!!.size == 0) {
                    callLoadingAnimation(false)
                    Toast.makeText(applicationContext,"EMAIL DOES NOT EXIST!",Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, REGISTERDETAILSACTIVITY)
                    startActivity(intent)
                    finish()
                    lottie.visibility = View.GONE
                    localData.id = editTextViews[0].text.toString()
                    localData.userPassword = editTextViews[1].text.toString()
                } else {
                    callLoadingAnimation(false)
                    Toast.makeText(applicationContext,"EMAIL EXISTS!",Toast.LENGTH_SHORT).show()
                }
            }).addOnFailureListener(OnFailureListener { e -> e.printStackTrace() })
    }
}