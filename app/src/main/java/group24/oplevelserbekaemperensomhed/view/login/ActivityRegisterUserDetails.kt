package group24.oplevelserbekaemperensomhed.view.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager.widget.ViewPager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.chip.Chip
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.EventDTO
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter
import kotlinx.android.synthetic.main.activity_register_user_details.*
import java.util.*
import kotlin.collections.ArrayList


class ActivityRegisterUserDetails : AppCompatActivity() {

    private lateinit var choosePicturesLayout: ConstraintLayout
    private lateinit var editTextViews: Array<EditText>
    private lateinit var buttonViews: Array<Any>

    private lateinit var adapter: ViewPagerAdapter
    private var viewPager: ViewPager? = null

    private val profilePictures = ArrayList<String>()
    private var address = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_user_details)
        initializeViews()
    }

    private fun initializeViews() {
        // Initializing all Buttons
        val backButton: ImageView = activity_register_backButton
        val submitButton1: ImageView = activity_register_submitButton1
        val submitButton2: LinearLayout = activity_register_submitButton2
        val addressButton: EditText = activity_register_addressText
        val choosePicturesButton: Chip = activity_register_choose_pictures_button
        buttonViews = arrayOf(
            backButton,
            submitButton1,
            submitButton2,
            addressButton,
            choosePicturesButton
        )

        // Choose pictures layout
        choosePicturesLayout = activity_register_choose_pictures_layout

        // Initializing all TextViews
        val firstName: EditText = activity_register_firstName
        val lastName: EditText = activity_register_lastName
        val dayText: EditText = activity_register_dayTest
        val monthText: EditText = activity_register_monthText
        val yearText: EditText = activity_register_yearText
        val occupationText: EditText = activity_register_occupationText
        val educationText: EditText = activity_register_educationText
        val aboutText: EditText = activity_register_aboutText
        editTextViews = arrayOf(
            firstName,
            lastName,
            dayText,
            monthText,
            yearText,
            occupationText,
            educationText,
            aboutText
        )

        // Sets onClickListeners for every view that uses one
        handleOnClickViews()

        // Initialize Places
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.api_key))
        }

        // Initialize adapter
        adapter = ViewPagerAdapter(supportFragmentManager,profilePictures,R.layout.fragment_profile_event_1_viewpager,null)
    }

    private fun handleOnClickViews() {
        val backButton: ImageView = buttonViews[0] as ImageView
        backButton.setOnClickListener {
            finish()
        }
        val submitButton1: ImageView = buttonViews[1] as ImageView
        submitButton1.setOnClickListener {
            submitDataToUserObject()
        }
        val submitButton2: LinearLayout = buttonViews[2] as LinearLayout
        submitButton2.setOnClickListener {
            submitDataToUserObject()
        }
        val addressButton: EditText = buttonViews[3] as EditText
        addressButton.setOnClickListener {
            searchForAddressWithAutoComplete()
        }
        val choosePicturesButton: Chip = buttonViews[4] as Chip
        choosePicturesButton.setOnClickListener {
            openPhoneStorage()
        }
    }

    private fun submitDataToUserObject() {
        for (textView in editTextViews) {
            val text = textView.text.toString()
            val hint = textView.hint.toString()
            if (hint.contains("*") && text == "") {
                Toast.makeText(applicationContext, "Please fill out the sections marked with '*'", Toast.LENGTH_SHORT).show()
                return
            }
            else if (hint.contains("First Name*") && text.length == 1) {
                Toast.makeText(applicationContext, "Please choose a real name", Toast.LENGTH_SHORT).show()
                return
            }
            if (hint.contains("Day") && text.toInt() > 31) {
                Toast.makeText(applicationContext, "Please choose a real day date", Toast.LENGTH_SHORT).show()
                return
            }
            else if (hint.contains("Month") && text.toInt() > 12) {
                Toast.makeText(applicationContext, "Please choose a real month date", Toast.LENGTH_SHORT).show()
                return
            }
            else if (hint.contains("Year") && text.length < 4) {
                Toast.makeText(applicationContext, "Please choose a real year date", Toast.LENGTH_SHORT).show()
                return
            }
            else if (hint.contains("Month")) {
                if (text.toInt() % 2 != 1) {
                    if (text.toInt() >= 30) {
                        Toast.makeText(applicationContext, "Please choose a real day date", Toast.LENGTH_SHORT).show()
                        return
                    }
                }
            }
        }
        if (profilePictures.isEmpty()) {
            Toast.makeText(
                applicationContext,
                "Please choose at least one profile picture",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        if (!checked) {
            Toast.makeText(
                applicationContext,
                "Please choose a gender",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // Saves the data in the text fields to the user object
        saveUserDetailsToLocalData()

        //FIXME ADD FIREBASE REGISTRATION HERE

        // Opens the mainactivity now that the user has been created
        val intent = Intent(applicationContext, HOMEACTIVITY)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun saveUserDetailsToLocalData() {
        val localData = LocalData
        val name = StringBuilder()
        val age = getAge(
            editTextViews[2].text.toString().toInt(),
            editTextViews[2].text.toString().toInt(),
            editTextViews[3].text.toString().toInt()
        )
        val user = UserDTO(
            name.toString(),
            age,
            address,
            editTextViews[5].text.toString(),
            editTextViews[6].text.toString(),
            editTextViews[7].text.toString(),
            gender,
            ArrayList<EventDTO>(),
            profilePictures
        )
        localData.userData = user
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }

        return age
    }

    private fun searchForAddressWithAutoComplete() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields)
            .setCountry("DK") //Denmark
            .build(this)
        startActivityForResult(intent, 1)
    }

    private fun openPhoneStorage() {
        val gallery =
            Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        gallery.type = "image/*"
        startActivityForResult(gallery, REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    val addressEditText: EditText = buttonViews[3] as EditText
                    Toast.makeText(this, "address:" + place.address + "Name:" + place.name, Toast.LENGTH_LONG).show()
                    address = place.address!!
                    addressEditText.setText(address)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(
                        data!!
                    )
                    Toast.makeText(this, "Error: " + status.statusMessage, Toast.LENGTH_LONG
                    ).show()
                }
                RESULT_CANCELED -> { }
            }
        }
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            profilePictures.clear()
            val clipData = data!!.clipData
            if (clipData != null) {
                if (clipData.itemCount > 3) {
                    Toast.makeText(this, "Error: Max 3 pictures", Toast.LENGTH_LONG).show()
                    return
                } else {
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        profilePictures.add(imageUri.toString())
                    }
                }
            } else {
                val imageUri: Uri? = data.data
                profilePictures.add(imageUri.toString())
            }
            if (viewPager == null) {
                viewPager = ViewPager(this)
                viewPager!!.id = View.generateViewId()
                viewPager!!.layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    0
                )
                (viewPager!!.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
                    "1:1"
                viewPager!!.adapter = adapter
                choosePicturesLayout.addView(viewPager)

                //Moving set images button below viewpager
                val constraintSet = ConstraintSet()
                val choosePicturesButton: Chip = buttonViews[4] as Chip
                constraintSet.clone(choosePicturesLayout)
                constraintSet.connect(choosePicturesButton.id, ConstraintSet.TOP, viewPager!!.id, ConstraintSet.BOTTOM, 20)
                constraintSet.applyTo(choosePicturesLayout)
            } else {
                adapter = ViewPagerAdapter(supportFragmentManager, profilePictures, R.layout.fragment_search_home_1_viewpager, null)
                viewPager!!.adapter = adapter
            }
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            checked = view.isChecked
            when (view.id) {
                R.id.activity_register_male ->
                    if (checked) {
                        gender = "Male"
                    }
                R.id.activity_register_female ->
                    if (checked) {
                        gender = "Female"
                    }
                R.id.activity_register_other ->
                    if (checked) {
                        gender = "Other"
                    }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 100
        private val HOMEACTIVITY = MainActivity::class.java
        private var checked = false
        private var gender = ""
    }
}