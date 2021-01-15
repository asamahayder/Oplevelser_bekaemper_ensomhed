package group24.oplevelserbekaemperensomhed.logic

import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.*
import group24.oplevelserbekaemperensomhed.view.FragmentViewPager


@Suppress("DEPRECATION")
class ViewPagerAdapter(
    val fm: FragmentManager,
    private val pictureURLs: ArrayList<String>,
    val layout: Int,
    val webURLlist: ArrayList<String>?
) : FragmentPagerAdapter(fm) {

    private lateinit var layoutInflater: LayoutInflater
    private lateinit var context: Context

    override fun getCount() = pictureURLs.size

    override fun getItem(position: Int): Fragment {
        return if (webURLlist == null) {
            FragmentViewPager(pictureURLs[position], layout, "")
        } else {
            FragmentViewPager(pictureURLs[position], layout, webURLlist[position])
        }
    }

}
