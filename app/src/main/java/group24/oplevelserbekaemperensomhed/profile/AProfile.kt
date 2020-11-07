package group24.oplevelserbekaemperensomhed.profile

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.INVISIBLE
import android.view.ViewGroup.MarginLayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import kotlinx.android.synthetic.main.aprofile.*


class AProfile : AppCompatActivity() {

    private lateinit var mPager: ViewPager
    private lateinit var mTablayout: TabLayout

    private val profileTexts = ArrayList<TextView>()
    private val icons = ArrayList<ImageView>()

    private lateinit var userData: UserDTO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aprofile)
        initializeView()
        updateProfile()
        handlePfpSlider()
        handleActivityChanges()
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    private fun initializeView() {
        profileTexts.add(aprofile_nameAge)
        profileTexts.add(aprofile_address)
        profileTexts.add(aprofile_occupation)
        profileTexts.add(aprofile_education)
        profileTexts.add(aprofile_bio)
        icons.add(aprofile_addressIcon)
        icons.add(aprofile_occupationIcon)
        icons.add(aprofile_educationIcon)
    }

    private fun handleActivityChanges() {
        val editProfileButton: ImageView = aprofile_editProfileButton
        val backButton: ImageView = aprofile_backButton

        editProfileButton.setOnClickListener { _ ->
            val intent = Intent(this, AProfileEdit::class.java)

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        backButton.setOnClickListener { _ ->
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }
    }

    private fun handlePfpSlider() {
        mPager = findViewById(R.id.aprofile_viewpager)
        mTablayout = findViewById(R.id.aprofile_tablayout)
        mTablayout.setupWithViewPager(mPager, true)
        mTablayout.setTabTextColors(Color.RED, Color.WHITE);
        val pagerAdapter =
            ProfilePicSliderPagerAdapter(supportFragmentManager, userData.profilePictures)
        mPager.adapter = pagerAdapter

        if (mTablayout.tabCount == 1) {
            mTablayout.visibility = INVISIBLE
        }

        for (i in 0 until mTablayout.tabCount) {
            val tab = (mTablayout.getChildAt(0) as ViewGroup).getChildAt(i)
            val p = tab.layoutParams as MarginLayoutParams
            p.setMargins(0, 0, 10, 0)
            tab.requestLayout()
        }
    }

    private fun updateProfile() {
        userData = LocalData.userData!!
        handleProfileInfo()
        handleProfileIcons()
    }

    @SuppressLint("SetTextI18n")
    private fun handleProfileInfo() {
        if (userData.name != null) {
            profileTexts[0].text = userData.name
            if (userData.age != null) {
                val text = userData.name + ", " + userData.age
                profileTexts[0].text = text
            }

            if (userData.about != null) {
                profileTexts[4].text = userData.about
            } else {
                profileTexts[4].text = ""
            }
        }
    }

    private fun handleProfileIcons() {
        var aTxt = false
        var oTxt = false
        var eTxt = false

        if (userData.address != null) aTxt = true
        if (userData.occupation != null) oTxt = true
        if (userData.education != null) eTxt = true

        if (aTxt && oTxt && eTxt) {
            profileTexts[1].text = userData.address
            icons[0].setImageResource(R.drawable.ic_baseline_location_on_15)
            profileTexts[2].text = userData.occupation
            icons[1].setImageResource(R.drawable.ic_baseline_work_15)
            profileTexts[3].text = userData.education
            icons[2].setImageResource(R.drawable.ic_baseline_school_15)
        }
        if (!aTxt && oTxt && eTxt) {
            profileTexts[1].text = userData.occupation
            icons[0].setImageResource(R.drawable.ic_baseline_work_15)
            profileTexts[2].text = userData.education
            icons[1].setImageResource(R.drawable.ic_baseline_school_15)
            profileTexts[3].text = ""
            icons[2].visibility = View.GONE
        }
        if (!aTxt && !oTxt && eTxt) {
            profileTexts[1].text = userData.education
            icons[0].setImageResource(R.drawable.ic_baseline_school_15)
            profileTexts[2].text = ""
            icons[1].visibility = View.GONE
            profileTexts[3].text = ""
            icons[2].visibility = View.GONE
        }
        if (aTxt && !oTxt && eTxt) {
            profileTexts[1].text = userData.address
            icons[0].setImageResource(R.drawable.ic_baseline_location_on_15)
            profileTexts[2].text = userData.education
            icons[1].setImageResource(R.drawable.ic_baseline_school_15)
            profileTexts[3].text = ""
            icons[2].visibility = View.GONE
        }
        if (aTxt && oTxt && !eTxt) {
            profileTexts[1].text = userData.address
            icons[0].setImageResource(R.drawable.ic_baseline_location_on_15)
            profileTexts[2].text = userData.occupation
            icons[1].setImageResource(R.drawable.ic_baseline_work_15)
            profileTexts[3].text = ""
            icons[2].visibility = View.GONE
        }
        if (aTxt && !oTxt && !eTxt) {
            profileTexts[1].text = userData.address
            icons[0].setImageResource(R.drawable.ic_baseline_location_on_15)
            profileTexts[2].text = ""
            icons[1].visibility = View.GONE
            profileTexts[3].text = ""
            icons[2].visibility = View.GONE
        }
        if (!aTxt && !oTxt && !eTxt) {
            profileTexts[1].text = ""
            icons[0].visibility = View.GONE
            profileTexts[2].text = ""
            icons[1].visibility = View.GONE
            profileTexts[3].text = ""
            icons[2].visibility = View.GONE
        }
    }
}

