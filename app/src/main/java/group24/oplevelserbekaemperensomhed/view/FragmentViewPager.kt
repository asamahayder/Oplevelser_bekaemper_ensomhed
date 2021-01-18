package group24.oplevelserbekaemperensomhed.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import group24.oplevelserbekaemperensomhed.R

// Handles updating the layout for the banner- and profile picture

class FragmentViewPager(
    val pictureURL: String?,
    val layout: Int,
    private val listOfBannerThings: ArrayList<ArrayList<String>>?,
    private val position: Int
) : Fragment() {

    private lateinit var pictureImageView: ImageView
    private lateinit var bannerText: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(layout, container, false)
        initializeView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initializeView(view)
    }

    private fun initializeView(view: View) {
        // Checks if the layout to be created was a banner or profile one
        pictureImageView = if (layout == R.layout.fragment_search_home_1_viewpager) {
            view.findViewById(R.id.fsearch_viewpager_bannerimage)
        } else{
            view.findViewById(R.id.fprofile_pfp)
        }

        // Updates the images for profile
        if (pictureURL != null) {
            Picasso.get()
                .load(pictureURL)
                .fit()
                .centerCrop()
                .into(pictureImageView)
        }

        // Updates the images for the banners
        if (listOfBannerThings != null) {
            bannerText = view.findViewById(R.id.fsearch_viewpager_bannertext)
            bannerText.text = listOfBannerThings[1][position]
            pictureImageView.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(listOfBannerThings[0][position]))
                startActivity(intent)
            }
        }
    }
}