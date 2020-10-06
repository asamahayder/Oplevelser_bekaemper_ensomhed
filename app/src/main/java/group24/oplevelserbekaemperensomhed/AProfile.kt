package group24.oplevelserbekaemperensomhed

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import kotlinx.android.synthetic.main.aprofile.*

class AProfile : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.aprofile)
        setupDummyData()
        initializeView()
    }

    override fun onResume() {
        super.onResume()
        updateProfile()
    }

    private fun updateProfile() {
    }

    private fun setupDummyData() {
        val eventsByUser = ArrayList<EventDTO>()
        val listOfPfps = ArrayList<String>()
        listOfPfps.add("https://alchetron.com/cdn/albrecht-thaer-3a110342-8ee9-462c-ade0-41fcadf6d35-resize-750.jpg")
        LocalData.userData = UserDTO(
            "Pernille",
            22,
            "Torshavn",
            "Stripper",
            "Harvard",
            "Hej med dig, kan du også godt lde pandekager?!\n" +
                    "Hvis du kan, pls kom og spis pandekager med mig på mit event k?\n" +
                    "Insert text\n\nTest test test\n" +
                    "olololololololo" +
                    "asdasdasda\nsdad ost ost lololo\n asdadasdasd",
            "Woman",
            eventsByUser,
            listOfPfps)
    }

    private fun initializeView() {

        val editProfileButton: ImageView = aprofile_editProfileButton
        val backButton: ImageView = aprofile_backButton
        val pfp: ImageView = aprofile_pfp

        val profileTexts = ArrayList<TextView>()
        val icons = ArrayList<ImageView>()
        val lines = ArrayList<View>()

        val userData = LocalData.userData

        profileTexts.add(aprofile_nameAge)
        profileTexts.add(aprofile_address)
        profileTexts.add(aprofile_occupation)
        profileTexts.add(aprofile_education)
        profileTexts.add(aprofile_bio)
        icons.add(aprofile_addressIcon)
        icons.add(aprofile_occupationIcon)
        icons.add(aprofile_educationIcon)

        for (i in 1..8) {
            val id = "aprofile_line$i"
            val resID = resources.getIdentifier(id, "id", packageName)
            val view = findViewById<View>(resID)
            lines.add(view)
        }

        editProfileButton.setOnClickListener { v ->
            val intent = Intent(this, AProfileEdit::class.java)

            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
        backButton.setOnClickListener { v ->
            finish()
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
        }

        initializeProfileInfo(userData, profileTexts, icons, lines, pfp)
    }

    @SuppressLint("SetTextI18n")
    private fun initializeProfileInfo(
        userData: UserDTO?,
        textViews: ArrayList<TextView>,
        icons: ArrayList<ImageView>,
        lines: ArrayList<View>,
        pfp: ImageView
    ) {

        if (userData != null) {

            if (userData.name != null) {
                textViews[0].text = userData.name
                if (userData.age != null) {
                    val text = userData.name + ", " + userData.age
                    textViews.get(0).text = text
                }
            } else {
                textViews[0].text = "Name, Age"
            }

            handleIconTextViews(userData, textViews, icons)

            if (userData.about != null) {
                textViews.get(4).text = userData.about
            } else {
                textViews.get(4).text = ""
            }

            if (userData.profilePictures != null) {
                var amountOfPfps = userData.profilePictures!!.size

                for (i in amountOfPfps..7) {
                    lines.get(i).visibility = View.INVISIBLE
                }
                Picasso.get()
                    .load(userData.profilePictures!!.get(0))
                    .resize(200,200)
                    .centerCrop()
                    .into(pfp)
            }
        }
    }

    private fun handleIconTextViews(userData: UserDTO, textViews: ArrayList<TextView>, icons: ArrayList<ImageView>) {
        var aTxt = false
        var oTxt = false
        var eTxt = false

        if (userData.address != null) aTxt = true
        if (userData.occupation != null) oTxt = true
        if (userData.education != null) eTxt = true

        if (aTxt && oTxt && eTxt) {
            textViews.get(1).text = userData.address
            icons.get(0).setImageResource(R.drawable.ic_baseline_location_on_15)
            textViews.get(2).text = userData.occupation
            icons.get(1).setImageResource(R.drawable.ic_baseline_work_15)
            textViews.get(3).text = userData.education
            icons.get(2).setImageResource(R.drawable.ic_baseline_school_15)
        }
        if (!aTxt && oTxt && eTxt) {
            textViews.get(1).text = userData.occupation
            icons.get(0).setImageResource(R.drawable.ic_baseline_work_15)
            textViews.get(2).text = userData.education
            icons.get(1).setImageResource(R.drawable.ic_baseline_school_15)
            textViews.get(3).text = ""
            icons.get(2).visibility = View.GONE
        }
        if (!aTxt && !oTxt && eTxt) {
            textViews.get(1).text = userData.education
            icons.get(0).setImageResource(R.drawable.ic_baseline_school_15)
            textViews.get(2).text = ""
            icons.get(1).visibility = View.GONE
            textViews.get(3).text = ""
            icons.get(2).visibility = View.GONE
        }
        if (aTxt && !oTxt && eTxt) {
            textViews.get(1).text = userData.address
            icons.get(0).setImageResource(R.drawable.ic_baseline_location_on_15)
            textViews.get(2).text = userData.education
            icons.get(1).setImageResource(R.drawable.ic_baseline_school_15)
            textViews.get(3).text = ""
            icons.get(2).visibility = View.GONE
        }
        if (aTxt && oTxt && !eTxt) {
            textViews.get(1).text = userData.address
            icons.get(0).setImageResource(R.drawable.ic_baseline_location_on_15)
            textViews.get(2).text = userData.occupation
            icons.get(1).setImageResource(R.drawable.ic_baseline_work_15)
            textViews.get(3).text = ""
            icons.get(2).visibility = View.GONE
        }
        if (aTxt && !oTxt && !eTxt) {
            textViews.get(1).text = userData.address
            icons.get(0).setImageResource(R.drawable.ic_baseline_location_on_15)
            textViews.get(2).text = ""
            icons.get(1).visibility = View.GONE
            textViews.get(3).text = ""
            icons.get(2).visibility = View.GONE
        }
        if (!aTxt && !oTxt && !eTxt) {
            textViews.get(1).text = ""
            icons.get(0).visibility = View.GONE
            textViews.get(2).text = ""
            icons.get(1).visibility = View.GONE
            textViews.get(3).text = ""
            icons.get(2).visibility = View.GONE
        }
    }
}

