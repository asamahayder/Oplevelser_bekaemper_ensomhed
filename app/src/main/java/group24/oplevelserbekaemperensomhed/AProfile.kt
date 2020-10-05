package group24.oplevelserbekaemperensomhed

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.aprofile.*

class AProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aprofile)

        initializeView()
    }

    private fun initializeView() {

        val editProfileButton = aprofile_editProfileButton


        editProfileButton.setOnClickListener { v ->
            Intent(this, AProfileEdit::class.java)
        }
    }
}

