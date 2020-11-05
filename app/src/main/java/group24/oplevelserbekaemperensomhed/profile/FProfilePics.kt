package group24.oplevelserbekaemperensomhed.profile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Picasso
import group24.oplevelserbekaemperensomhed.R

class FProfilePics(val s: String?) : Fragment() {

    private lateinit var pfpPicture: ImageView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fprofilepics, container, false)
        initializeView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeView(view)
    }

    private fun initializeView(view: View) {
        pfpPicture = view.findViewById(R.id.fprofile_pfp)
        if (s != null) {
            Picasso.get()
                .load(s)
                .fit()
                .centerCrop()
                .into(pfpPicture)
        }
    }

}