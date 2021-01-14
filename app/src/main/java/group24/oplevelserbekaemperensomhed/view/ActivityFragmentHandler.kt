package group24.oplevelserbekaemperensomhed.view

import android.os.Bundle
import android.os.Parcelable
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import group24.oplevelserbekaemperensomhed.FragmentEventInfo
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.view.profile.FragmentProfile

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
        val bundle = Bundle()
        if (intent.extras!!["event"] != null) {
            fragment = FragmentEventInfo()
            bundle.putParcelable("event", intent.extras!!["event"] as Parcelable?)
            bundle.putString("other","other")
            fragment.arguments = bundle
            tag = getString(R.string.fragment_event)
        } else {
            fragment = FragmentProfile()
            bundle.putParcelable("profile", intent.extras!!["profile"] as Parcelable?)
            bundle.putString("other","other")
            fragment.arguments = bundle
            tag = getString(R.string.fragment_profile)
        }

        changeFragment(fragment, tag)
    }

    private fun changeFragment(fragment: Fragment?, tag: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.activity_fragment_handler_layout, fragment!!, tag)
        //if (addToBackStack) {
        //    transaction.addToBackStack(tag);
        //}
        transaction.commit()
    }
}