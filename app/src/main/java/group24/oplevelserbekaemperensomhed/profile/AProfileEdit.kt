package group24.oplevelserbekaemperensomhed.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import kotlinx.android.synthetic.main.aprofileedit.*

class AProfileEdit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aprofileedit)
        initializeView()
    }

    private fun initializeView() {

        val userData = LocalData.userData

        val backButton = aprofileedit_backButton
        val saveButton = aprofileedit_saveButton
        val bioText = aprofileedit_bioText
        val addressText = aprofileedit_addressText
        val jobText = aprofileedit_jobText
        val educationText = aprofileedit_educationText

        handleSavedUserInfo(userData, bioText, addressText, jobText, educationText)

        handleActivityChanges(
            backButton,
            saveButton,
            userData,
            bioText,
            addressText,
            jobText,
            educationText
        )
    }

    private fun handleSavedUserInfo(
        userData: UserDTO?,
        bioText: EditText,
        addressText: EditText,
        jobText: EditText,
        educationText: EditText
    ) {
        if (userData != null) {
            if (userData.about != null) {
                bioText.setText(userData.about)
            }
            if (userData.address != null) {
                addressText.setText(userData.address)
            }
            if (userData.occupation != null) {
                jobText.setText(userData.occupation)
            }
            if (userData.education != null) {
                educationText.setText(userData.education)
            }
        }
    }

    private fun handleActivityChanges(
        backButton: ImageView,
        saveButton: ImageView,
        userData: UserDTO?,
        bioText: EditText,
        addressText: EditText,
        jobText: EditText,
        educationText: EditText
    ) {
        var userData1 = userData
        backButton.setOnClickListener { _ ->
            finish()
        }
        saveButton.setOnClickListener { _ ->
            if (userData1 == null) {
                userData1 =
                    UserDTO(null, null, null, null, null, null, null, null, ArrayList<String>())
            }
            if (!bioText.text.isEmpty()) {
                userData1!!.about = bioText.text.toString()
            }
            if (!addressText.text.isEmpty()) {
                userData1!!.address = addressText.text.toString()
            }
            if (!jobText.text.isEmpty()) {
                userData1!!.occupation = jobText.text.toString()
            }
            if (!educationText.text.isEmpty()) {
                userData1!!.education = educationText.text.toString()
            }
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}