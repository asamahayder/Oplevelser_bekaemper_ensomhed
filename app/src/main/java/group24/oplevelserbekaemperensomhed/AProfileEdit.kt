package group24.oplevelserbekaemperensomhed

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.aprofileedit.*

class AProfileEdit : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aprofileedit)

        initializeView()
    }

    private fun initializeView() {

        val backButton = aprofileedit_backButton

        backButton.setOnClickListener { v ->
            finish()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }
}