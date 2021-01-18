package group24.oplevelserbekaemperensomhed.logic

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.*
import group24.oplevelserbekaemperensomhed.view.FragmentViewPager

// ViewPagerAdapter that is used for the profile and banner imageSliders

@Suppress("DEPRECATION")
class ViewPagerAdapter(
    val fm: FragmentManager,
    private val pictureURLs: ArrayList<String>,
    val layout: Int,
    private val listOfBannerThings: ArrayList<ArrayList<String>>?
) : FragmentPagerAdapter(fm) {

    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context

    override fun getCount() = pictureURLs.size

    override fun getItem(position: Int): Fragment {
        // Checks if the which type of viewpager this is. Depending on what it is, it'll either be a profile picture layout or banner layout
        return if (listOfBannerThings == null) {
            FragmentViewPager(pictureURLs[position], layout, null, position)
        } else {
            FragmentViewPager(pictureURLs[position], layout, listOfBannerThings, position)
        }
    }

}
