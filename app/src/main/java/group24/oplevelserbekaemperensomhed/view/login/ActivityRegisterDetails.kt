package group24.oplevelserbekaemperensomhed.view.login

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.viewpager.widget.ViewPager
import com.google.android.gms.tasks.Task
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.chip.Chip
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import group24.oplevelserbekaemperensomhed.MainActivity
import group24.oplevelserbekaemperensomhed.R
import group24.oplevelserbekaemperensomhed.data.EventDTO
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

// Handles Registering user details

class ActivityRegisterDetails : AppCompatActivity() {

    private lateinit var choosePicturesLayout: ConstraintLayout
    private lateinit var editTextViews: Array<EditText>
    private lateinit var buttonViews: Array<Any>

    private var viewPager: ViewPager? = null

    private val profilePictures = ArrayList<String>()
    private var address = ""

    private val auth = FirebaseAuth.getInstance()
    private val localData = LocalData
    private val db = FirebaseDAO()

    private val picturesAsURIs: ArrayList<Uri> = ArrayList()

    private var facebookCredential: AuthCredential? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        checkWhatLoginType()
    }

    // Handles going back to login page
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(applicationContext, LOGINACTIVITY)
        startActivity(intent)
        finish()
    }

    // To handle if the user got here from the Facebook signup or Email/Password signup
    private fun checkWhatLoginType() {
        // Checks if there exists an facebook credential
        if (intent.extras?.get("facebook") is AuthCredential) {
            facebookCredential = intent.extras!!["facebook"] as AuthCredential
            // Signs in
            auth.signInWithCredential(facebookCredential!!)
                .addOnCompleteListener {task ->
                    if (task.isSuccessful) {
                        db.getUser(auth.currentUser!!.uid, object : MyCallBack {
                            override fun onCallBack(`object`: Any) {
                                // If the user already exists in the database, open up the mainActivity
                                if (`object` is UserDTO) {
                                    localData.userData = `object`
                                    localData.id = auth.uid.toString()
                                    val intent = Intent(applicationContext, MainActivity::class.java)
                                    startActivity(intent)
                                // Else start registering user details
                                } else {
                                    auth.currentUser!!.delete()
                                    setContentView(R.layout.activity_register_details)
                                    initializeViews()
                                }
                            }
                        })
                    }

                }
            // Else the user got here from email/password signup
        } else {
            setContentView(R.layout.activity_register_details)
            initializeViews()
        }
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

    }

    // Handles all buttons on the layout
    private fun handleOnClickViews() {
        val backButton: ImageView = buttonViews[0] as ImageView
        backButton.setOnClickListener {
            auth.currentUser?.delete()
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

        // Saves the data in the text fields to the user object
        if(saveUserDetailsToLocalData()) {
            //Handling upload of pictures and getting their new urls
            //Showing progress dialog
            val progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Uploading...")
            progressDialog.show()

            // Uploads pictures to the database
            db.uploadImages(picturesAsURIs, object : MyUploadPicturesListener {
                override fun onSuccess(`object`: Any) {
                    val pictureDownloadLinks =
                        `object` as java.util.ArrayList<String>
                    progressDialog.dismiss()
                    createUser(pictureDownloadLinks)
                }

                // Shows progress loading
                override fun onProgress(`object`: Any) {
                    val counter = `object` as Int
                    progressDialog.setMessage("$counter images left")
                }

                // Handles failure message
                override fun onFailure(`object`: Any) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        applicationContext,
                        "Could not upload images",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    // Handles all the different types of errors the user may do when typing into the text fields
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

    // Authenticates the user
    private fun createUser(pictureDownloadLinks: ArrayList<String>) {
        if (facebookCredential == null) {
            // Facebook authentication
            auth.createUserWithEmailAndPassword(localData.userEmail, localData.userPassword)
                .addOnCompleteListener { task ->
                    // Uploads the user data to the database
                    uploadUserDetailsToDatabase(task, pictureDownloadLinks)
                }
        } else {
            // Facebook authentication
            auth.signInWithCredential(facebookCredential!!)
                .addOnCompleteListener { task ->
                    // Uploads the user data to the database
                    uploadUserDetailsToDatabase(task, pictureDownloadLinks)
                }
        }
    }

    // Uploads the user data to the database
    private fun uploadUserDetailsToDatabase(
        task: Task<AuthResult>,
        pictureDownloadLinks: ArrayList<String>
    ) {
        if (task.isSuccessful) {
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                // inserts the typed data into the data object for ease of uploading to firestore
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
                // Saves the id to the singleton for access later on in the app's usage
                localData.id = firebaseUser.uid
                db.createUser(dbUser, localData.id, object : MyCallBack {
                    override fun onCallBack(`object`: Any) {
                        // Opens the mainactivity now that the user has been created
                        val intent = Intent(this@ActivityRegisterDetails, HOMEACTIVITY)
                        startActivity(intent)
                        // Deletes the password and email because it's not needed anymore
                        localData.userPassword = ""
                        localData.userEmail = ""
                    }
                })
            }
        } else {
            Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Creates a local user object and saves it to the singleton object
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
        // appends first-/last name, if there is a last name
        nameBuilder = nameBuilder.append(editTextViews[0].text.toString())
        if (editTextViews[1].text.toString().isNotEmpty()) {
            nameBuilder = nameBuilder.append(" ").append(
                editTextViews[1].text.toString()
            )
        }
        // creates the user object
        val user = UserDTO(
            nameBuilder.toString(),
            listOf(editTextViews[4].text.toString().toInt(), editTextViews[3].text.toString().toInt(), editTextViews[2].text.toString().toInt()),
            address,
            editTextViews[5].text.toString(),
            editTextViews[6].text.toString(),
            editTextViews[7].text.toString(),
            gender,
            ArrayList<EventDTO>(),
            profilePictures
        )
        // saves the user object to the singleton
        localData.userData = user
        return true
    }

    // Auto completes address text whilst searching
    private fun searchForAddressWithAutoComplete() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields)
            .setCountry("DK") //Denmark
            .build(this)
        startActivityForResult(intent, 1)
    }

    // Opens the phone storage so that the user may be able to choose a picture to upload
    private fun openPhoneStorage() {
        val gallery =
            Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        gallery.type = "image/*"
        startActivityForResult(gallery, REQUEST_CODE)
    }

    // When leaving the address activity or phone storage the respective fields get updated
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            // Updates the address
            when (resultCode) {
                RESULT_OK -> {
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
        // Updates the profile picture list
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            profilePictures.clear()
            picturesAsURIs.clear()
            val clipData = data!!.clipData
            if (clipData != null) {
                if (clipData.itemCount > 8) {
                    Toast.makeText(this, "Error: Max 8 pictures", Toast.LENGTH_LONG).show()
                    return
                } else {
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
            // Updates the viewpager with profile pictures
            createViewPager()
        }
    }

    // creates the viewpager which holds all the images
    private fun createViewPager(){
        viewPager = ViewPager(this)
        viewPager!!.id = View.generateViewId()
        viewPager!!.layoutParams = ConstraintLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            0
        )
        (viewPager!!.layoutParams as ConstraintLayout.LayoutParams).dimensionRatio = "1:1"
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

    override fun onDestroy() {
        super.onDestroy()
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.delete()
    }

    // Handles the gender radiobuttons
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
        private const val TAG = "ActivityRegisterDetails"
        private val HOMEACTIVITY = MainActivity::class.java
        private val LOGINACTIVITY = ActivityLogin::class.java
        private var checked = false
        private var gender = ""
    }
}