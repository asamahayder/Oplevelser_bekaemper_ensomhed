package group24.oplevelserbekaemperensomhed.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

        var userData = LocalData.userData

        val backButton = aprofileedit_backButton
        val saveButton = aprofileedit_saveButton
        val bioText = aprofileedit_bioText
        val addressText = aprofileedit_addressText
        val jobText = aprofileedit_jobText
        val educationText = aprofileedit_educationText

        backButton.setOnClickListener { _ ->
            finish()
        }
        saveButton.setOnClickListener { _ ->
            if (userData == null) {
                userData = UserDTO(null,null,null,null,null,null,null,null,ArrayList())
            }
            if (!bioText.text.isEmpty()) {
                userData!!.about = bioText.text.toString()
            }
            if (!addressText.text.isEmpty()) {
                userData!!.address = addressText.text.toString()
            }
            if (!jobText.text.isEmpty()) {
                userData!!.occupation = jobText.text.toString()
            }
            if (!educationText.text.isEmpty()) {
                userData!!.education = educationText.text.toString()
            }
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}