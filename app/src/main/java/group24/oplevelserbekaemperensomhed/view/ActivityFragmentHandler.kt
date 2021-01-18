package group24.oplevelserbekaemperensomhed.view

import android.os.Bundle
import android.os.Parcelable
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import group24.oplevelserbekaemperensomhed.view.event.FragmentEventInfo
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.view.profile.FragmentProfile

// handles re-use of fragment profile and event when going deeper into the app layers

class ActivityFragmentHandler : AppCompatActivity() {

    private lateinit var layout: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_handler)
        initializeView()
    }

    private fun initializeView() {
        layout = findViewById(R.id.activity_fragment_handler_layout)
        val fragment: Fragment?
        val tag: String?

        // Checks which kind of activity we're dealing with. event or profile
        val bundle = Bundle()
        if (intent.extras!!["event"] != null) {
            // Opens a event fragment from this activity and passes the information on
            fragment =
                FragmentEventInfo()
            bundle.putParcelable("event", intent.extras!!["event"] as Parcelable?)
            bundle.putString("other","other")
            fragment.arguments = bundle
            tag = getString(R.string.fragment_event)
        } else {
            // Opens a profile fragment from this activity and passes the information on
            fragment = FragmentProfile()
            bundle.putParcelable("profile", intent.extras!!["profile"] as Parcelable?)
            bundle.putString("other","other")
            fragment.arguments = bundle
            tag = getString(R.string.fragment_profile)
        }
        // Handles change fragment
        changeFragment(fragment, tag)
    }

    // inflates the new fragments
    private fun changeFragment(fragment: Fragment?, tag: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_fragment_handler_layout, fragment!!, tag)
        transaction.commit()
    }
}