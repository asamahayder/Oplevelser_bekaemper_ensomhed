package group24.oplevelserbekaemperensomhed.view.profile

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager.widget.ViewPager
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.LocalData
import group24.oplevelserbekaemperensomhed.data.UserDTO
import group24.oplevelserbekaemperensomhed.logic.Logic
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter
import group24.oplevelserbekaemperensomhed.logic.firebase.DBUser
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack
import group24.oplevelserbekaemperensomhed.logic.firebase.MyUploadPicturesListener
import kotlinx.android.synthetic.main.activity_register_details.*
import kotlin.collections.ArrayList

// Handles editing the profile

class ActivityEditProfile : AppCompatActivity() {


    private lateinit var choosePicturesLayout: ConstraintLayout
    private lateinit var editTextViews: Array<EditText>
    private lateinit var buttonViews: Array<Any>

    private lateinit var adapter: ViewPagerAdapter
    private var viewPager: ViewPager? = null

    private lateinit var profilePictures: ArrayList<String>
    private var address = ""

    private val auth = FirebaseAuth.getInstance()
    private val localData = LocalData
    private val db = FirebaseDAO()

    private val picturesAsURIs: ArrayList<Uri> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_details)
        initializeViews()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    private fun initializeViews() {
        // Initializing all Buttons
        val backButton: ImageView = activity_register_details_backButton
        val submitButton: LinearLayout = activity_register_details_submitButton
        val addressButton: EditText = activity_register_details_addressText
        val choosePicturesButton: Chip = activity_register_details_choose_pictures_button
        buttonViews = arrayOf(
            backButton,
            submitButton,
            addressButton,
            choosePicturesButton
        )
        // Gender radio buttons
        val radioButton1: RadioButton = activity_register_male
        val radioButton2: RadioButton = activity_register_female
        val radioButton3: RadioButton = activity_register_other


        // Choose pictures layout
        choosePicturesLayout = activity_register_details_choose_pictures_layout

        // Initializing all TextViews
        val firstName: EditText = activity_register_details_firstName
        val lastName: EditText = activity_register_details_lastName
        val dayText: EditText = activity_register_details_dayTest
        val monthText: EditText = activity_register_details_monthText
        val yearText: EditText = activity_register_details_yearText
        val occupationText: EditText = activity_register_details_occupationText
        val educationText: EditText = activity_register_details_educationText
        val aboutText: EditText = activity_register_details_aboutText
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

        // Sets the text onto the edit text fields and updates variables
        address = localData.userData.address.toString()
        gender = localData.userData.gender.toString()
        val tempList = localData.userData.profilePictures.toMutableList()
        profilePictures = tempList as ArrayList<String>
        when (gender) {
            "male" -> {
                radioButton1.isChecked = true
            }
            "female" -> {
                radioButton2.isChecked = true
            }
            else -> {
                radioButton3.isChecked = true
            }
        }
        val firstNameText: String
        val lastNameText: String
        if (localData.userData.name?.contains(" ")!!) {
            val splitString = localData.userData.name!!.split(" ")
            firstNameText = splitString[0]
            lastNameText = splitString[1]
            editTextViews[0].setText(firstNameText)
            editTextViews[1].setText(lastNameText)
        } else {
            editTextViews[0].setText(localData.userData.name)
            editTextViews[1].setText("")
        }
        editTextViews[2].setText(localData.userData.age[2].toString())
        editTextViews[3].setText(localData.userData.age[1].toString())
        editTextViews[4].setText(localData.userData.age[0].toString())
        editTextViews[5].setText(localData.userData.occupation)
        editTextViews[6].setText(localData.userData.education)
        editTextViews[7].setText(localData.userData.about)
        addressButton.setText(localData.userData.address)

        // Initialize adapter
        createViewPager()
    }

    private fun handleOnClickViews() {
        val backButton: ImageView = buttonViews[0] as ImageView
        backButton.setOnClickListener {
            onBackPressed()
        }
        val submitButton: LinearLayout = buttonViews[1] as LinearLayout
        submitButton.setOnClickListener {
            submitDataToUserObject()
        }
        val addressButton: EditText = buttonViews[2] as EditText
        addressButton.setOnClickListener {
            searchForAddressWithAutoComplete()
        }
        val choosePicturesButton: Chip = buttonViews[3] as Chip
        choosePicturesButton.setOnClickListener {
            openPhoneStorage()
        }
    }

    private fun submitDataToUserObject() {
        if (checkTextFieldsForMistakes()) return

        // Check if new pictures added
        var added = false
        if (localData.userData.profilePictures[0] != profilePictures[0]) {
            added = true
        }

        // Saves the data in the text fields to the user object
        if(saveUserDetailsToLocalData()) {
            if (added) {
                //Handling upload of pictures and getting their new urls
                //Showing progress dialog
                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("Uploading...")
                progressDialog.show()

                db.uploadImages(picturesAsURIs, object : MyUploadPicturesListener {
                    override fun onSuccess(`object`: Any) {
                        val pictureDownloadLinks =
                            `object` as java.util.ArrayList<String>
                        progressDialog.dismiss()
                        uploadUserDetailsToDatabase(pictureDownloadLinks)
                        localData.userData.profilePictures = pictureDownloadLinks
                    }

                    override fun onProgress(`object`: Any) {
                        val counter = `object` as Int
                        progressDialog.setMessage("$counter images left")
                    }

                    override fun onFailure(`object`: Any) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            applicationContext,
                            "Could not upload images",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            } else {
                uploadUserDetailsToDatabase(localData.userData.profilePictures)
            }

        }
    }

    // Handles user errors
    private fun checkTextFieldsForMistakes(): Boolean {
        for (textView in editTextViews) {
            val text = textView.text.toString()
            val hint = textView.hint.toString()
            if (hint.contains("*") && text == "") {
                Toast.makeText(
                    applicationContext,
                    "Please fill out the sections marked with '*'",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            } else if (hint.contains("First Name*") && text.length == 1) {
                Toast.makeText(applicationContext, "Please choose a real name", Toast.LENGTH_SHORT)
                    .show()
                return true
            }
            if (hint.contains("Day") && text.toInt() > 31) {
                Toast.makeText(
                    applicationContext,
                    "Please choose a real day date",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            } else if (hint.contains("Month") && text.toInt() > 12) {
                Toast.makeText(
                    applicationContext,
                    "Please choose a real month date",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            } else if (hint.contains("Year") && text.length < 4) {
                Toast.makeText(
                    applicationContext,
                    "Please choose a real year date",
                    Toast.LENGTH_SHORT
                ).show()
                return true
            } else if (hint.contains("Month")) {
                //FIXME NOT REALLY WORKING AS INTENDED
                if (text.toInt() % 2 != 1) {
                    if (text.toInt() >= 29) {
                        Toast.makeText(
                            applicationContext,
                            "Please choose a real day date",
                            Toast.LENGTH_SHORT
                        ).show()
                        return true
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
            return true
        }
        if (!checked) {
            Toast.makeText(
                applicationContext,
                "Please choose a gender",
                Toast.LENGTH_SHORT
            ).show()
            return true
        }
        return false
    }

    // Uploads details to the firestore database
    private fun uploadUserDetailsToDatabase(pictureDownloadLinks: ArrayList<String>
    ) {
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            val user = localData.userData
            val dbUser = DBUser(
                user.name!!,
                listOf(user.age[0],user.age[1],user.age[2]),
                user.gender!!,
                user.about!!,
                user.address!!,
                user.occupation!!,
                user.education!!,
                ArrayList<String>(),
                pictureDownloadLinks
            )
            // Updates the singleton
            localData.id = firebaseUser.uid
            db.createUser(dbUser, localData.id, object : MyCallBack {
                override fun onCallBack(`object`: Any) {
                    finish()
                }
            })
        }
    }

    // Saves the user object to the local singleton
    private fun saveUserDetailsToLocalData(): Boolean {
        val logic = Logic()
        val localData = LocalData
        var nameBuilder = StringBuilder()
        val age = logic.getAge(
            editTextViews[4].text.toString().toInt(),
            editTextViews[3].text.toString().toInt(),
            editTextViews[2].text.toString().toInt()
        )
        if (age < 0) {
            Toast.makeText(applicationContext, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return false
        }
        // Appends name depening on if there's a last name or not
        nameBuilder = nameBuilder.append(editTextViews[0].text.toString())
        if (editTextViews[1].text.toString().isNotEmpty()) {
            nameBuilder = nameBuilder.append(" ").append(
                editTextViews[1].text.toString()
            )
        }
        // Adds all the information to the user object
        val user = UserDTO(
            nameBuilder.toString(),
            listOf(editTextViews[4].text.toString().toInt(), editTextViews[3].text.toString().toInt(), editTextViews[2].text.toString().toInt()),
            address,
            editTextViews[5].text.toString(),
            editTextViews[6].text.toString(),
            editTextViews[7].text.toString(),
            gender,
            localData.userData.eventsCreatedByUser,
            profilePictures
        )
        // saves the user object to the singleton
        localData.userData = user
        return true
    }

    // Handles gender radiobuttons
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

    // Searches for address and auto completes for whatever is typed
    private fun searchForAddressWithAutoComplete() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        )
            .setCountry("DK") //Denmark
            .build(this)
        startActivityForResult(intent, 1)
    }

    // Handles opening storage on the phone
    private fun openPhoneStorage() {
        val gallery =
            Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        gallery.type = "image/*"
        startActivityForResult(gallery, REQUEST_CODE)
    }

    // Handles getting results back from address & storage
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // If iit's the address activity that was opened
        if (requestCode == 1) {
            when (resultCode) {
                RESULT_OK -> {
                    // save address
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    val addressEditText: EditText = buttonViews[2] as EditText
                    address = place.address!!
                    addressEditText.setText(address)
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(
                        data!!
                    )
                    Toast.makeText(
                        this, "Error: " + status.statusMessage, Toast.LENGTH_LONG
                    ).show()
                }
                RESULT_CANCELED -> {
                }
            }
        }
        // If it was the storage that was opened
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            profilePictures.clear()
            picturesAsURIs.clear()
            val clipData = data!!.clipData
            if (clipData != null) {
                if (clipData.itemCount > 8) {
                    Toast.makeText(this, "Error: Max 8 pictures", Toast.LENGTH_LONG).show()
                    return
                } else {
                    // Adds the profile pictures to the profile picture list
                    for (i in 0 until clipData.itemCount) {
                        val imageUri = clipData.getItemAt(i).uri
                        profilePictures.add(imageUri.toString())
                        picturesAsURIs.add(imageUri)
                    }
                }
            } else {
                val imageUri: Uri? = data.data
                profilePictures.add(imageUri.toString())
                if (imageUri != null) {
                    picturesAsURIs.add(imageUri)
                }
            }
            if (viewPager != null) {
                choosePicturesLayout.removeView(viewPager)
            }
            // creates the viewpager for holding the profile pictures
            createViewPager()
        }
    }

    private fun createViewPager(){
        viewPager = ViewPager(this)
        viewPager!!.id = View.generateViewId()
        viewPager!!.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        )
        (viewPager!!.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio =
            "1:1"
        viewPager!!.adapter = ViewPagerAdapter(supportFragmentManager, profilePictures, R.layout.fragment_profile_event_1_viewpager, null);
        choosePicturesLayout.addView(viewPager)

        //Moving set images button below viewpager
        val constraintSet = ConstraintSet()
        val choosePicturesButton: Chip = buttonViews[3] as Chip
        constraintSet.clone(choosePicturesLayout)
        constraintSet.connect(
            choosePicturesButton.id,
            ConstraintSet.TOP,
            viewPager!!.id,
            ConstraintSet.BOTTOM,
            20
        )
        constraintSet.applyTo(choosePicturesLayout)
    }

    companion object {
        private const val REQUEST_CODE = 100
        private const val TAG = "ActivityEditProfile"
        private var gender = ""
        private var checked = true
    }
}