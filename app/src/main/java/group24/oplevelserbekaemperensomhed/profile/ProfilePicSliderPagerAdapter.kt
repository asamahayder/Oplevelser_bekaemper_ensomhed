package group24.oplevelserbekaemperensomhed.profile

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


@Suppress("DEPRECATION")
class ProfilePicSliderPagerAdapter(
    fm: FragmentManager,
    val pfpURLs: ArrayList<String>) : FragmentStatePagerAdapter(fm) {

    override fun getCount(): Int {
        return pfpURLs.size
    }

    override fun getItem(position: Int): Fragment {
        val fragment: Fragment = FProfilePics(pfpURLs[position])
        return fragment
    }

}
