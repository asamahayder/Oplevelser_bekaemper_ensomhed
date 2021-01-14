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
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter
import group24.oplevelserbekaemperensomhed.logic.firebase.DBUser
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack
import group24.oplevelserbekaemperensomhed.logic.firebase.MyUploadPicturesListener
import kotlinx.android.synthetic.main.activity_register_details.*
import java.util.*
import kotlin.collections.ArrayList


class ActivityRegisterDetails : AppCompatActivity() {

    private lateinit var choosePicturesLayout: ConstraintLayout
    private lateinit var editTextViews: Array<EditText>
    private lateinit var buttonViews: Array<Any>

    private lateinit var adapter: ViewPagerAdapter
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
        setContentView(R.layout.activity_register_details)
        checkWhatLoginType()
        initializeViews()
    }

    private fun checkWhatLoginType() {
        if (intent.extras != null) {
            facebookCredential = intent.extras!!["facebook"] as AuthCredential
        }
    }

    private fun initializeViews() {
        // Initializing all Buttons
        val backButton: ImageView = activity_register_details_backButton
        val submitButton1: ImageView = activity_register_details_submitButton1
        val submitButton2: LinearLayout = activity_register_details_submitButton2
        val addressButton: EditText = activity_register_details_addressText
        val choosePicturesButton: Chip = activity_register_details_choose_pictures_button
        buttonViews = arrayOf(
            backButton,
            submitButton1,
            submitButton2,
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

        // Initialize adapter
        adapter = ViewPagerAdapter(
            supportFragmentManager,
            profilePictures,
            R.layout.fragment_profile_event_1_viewpager,
            null
        )
    }

    private fun handleOnClickViews() {
        val backButton: ImageView = buttonViews[0] as ImageView
        backButton.setOnClickListener {
            goBackAndDeleteUserDetails()
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

    private fun goBackAndDeleteUserDetails() {
        auth.currentUser?.delete()
        val intent = Intent(applicationContext, LOGINACTIVITY)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    private fun submitDataToUserObject() {
        if (checkTextFieldsForMistakes()) return

        // Saves the data in the text fields to the user object
        if(saveUserDetailsToLocalData()) {
            //FIXME LOADING ANIMATION MAYBE?

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
                    createUser(pictureDownloadLinks)
                }

                override fun onProgress(`object`: Any) {
                    val counter = `object` as Int
                    progressDialog.setMessage("$counter images left")
                }

                override fun onFailure(`object`: Any) {
                    progressDialog.dismiss()
                    Toast.makeText(
                        this@ActivityRegisterDetails,
                        "Could not upload images",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

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

    private fun createUser(pictureDownloadLinks: ArrayList<String>) {
        if (facebookCredential == null) {
            auth.createUserWithEmailAndPassword(localData.id, localData.userPassword)
                .addOnCompleteListener { task ->
                    uploadUserDetailsToDatabase(task, pictureDownloadLinks)
                }
        } else {
            auth.signInWithCredential(facebookCredential!!)
                .addOnCompleteListener { task ->
                    Log.d(TAG, "FacebookAuthorization: firebase auth succeeded")
                    uploadUserDetailsToDatabase(task, pictureDownloadLinks)
                }
        }
    }

    private fun uploadUserDetailsToDatabase(
        task: Task<AuthResult>,
        pictureDownloadLinks: ArrayList<String>
    ) {
        if (task.isSuccessful) {
            Log.d(TAG, "create firebase firestore user success")
            val firebaseUser = auth.currentUser
            if (firebaseUser != null) {
                val user = localData.userData
                val dbUser = DBUser(
                    user.name!!,
                    user.age!!,
                    user.gender!!,
                    user.about!!,
                    user.address!!,
                    user.occupation!!,
                    user.education!!,
                    ArrayList<String>(),
                    pictureDownloadLinks
                )
                if (facebookCredential != null) {
                    localData.id = firebaseUser.uid
                }
                db.createUser(dbUser, localData.id, object : MyCallBack {
                    override fun onCallBack(`object`: Any) {
                        // Opens the mainactivity now that the user has been created
                        val intent = Intent(applicationContext, HOMEACTIVITY)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        startActivity(intent)
                        localData.userPassword = ""
                    }
                })
            }
        } else {
            Log.d(TAG, "create firebase firestore user failed")
            Toast.makeText(applicationContext, "Authentication Failed", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun saveUserDetailsToLocalData(): Boolean {
        val localData = LocalData
        var nameBuilder = StringBuilder()
        val age = getAge(
            editTextViews[4].text.toString().toInt(),
            editTextViews[3].text.toString().toInt(),
            editTextViews[2].text.toString().toInt()
        )
        if (age < 0) {
            Toast.makeText(applicationContext, "Please enter a valid age", Toast.LENGTH_SHORT).show()
            return false
        }
        nameBuilder = nameBuilder.append(editTextViews[0].text.toString())
        if (editTextViews[1].text.toString().isNotEmpty()) {
            nameBuilder = nameBuilder.append(editTextViews[0].text.toString()).append(" ").append(
                editTextViews[1].text.toString()
            )
        }
        val user = UserDTO(nameBuilder.toString(), age, address, editTextViews[5].text.toString(), editTextViews[6].text.toString(), editTextViews[7].text.toString(), gender, ArrayList<EventDTO>(), profilePictures)
        Log.d(TAG, "User object = $user")

        localData.userData = user
        Log.d(TAG, "userdata = $user")
        return true
    }

    private fun getAge(year: Int, month: Int, day: Int): Int {
        val dob = Calendar.getInstance()
        val today = Calendar.getInstance()
        dob[year, month] = day
        var age = today[Calendar.YEAR] - dob[Calendar.YEAR]
        if (today[Calendar.DAY_OF_YEAR] < dob[Calendar.DAY_OF_YEAR]) {
            age--
        }
        Log.d(TAG, "user age = $age")
        return age
    }

    private fun searchForAddressWithAutoComplete() {
        val fields = listOf(Place.Field.NAME, Place.Field.ADDRESS)
        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.FULLSCREEN, fields
        )
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
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            profilePictures.clear()
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
                constraintSet.connect(
                    choosePicturesButton.id,
                    ConstraintSet.TOP,
                    viewPager!!.id,
                    ConstraintSet.BOTTOM,
                    20
                )
                constraintSet.applyTo(choosePicturesLayout)
            } else {
                adapter = ViewPagerAdapter(
                    supportFragmentManager,
                    profilePictures,
                    R.layout.fragment_profile_event_1_viewpager,
                    null
                )
                viewPager!!.adapter = adapter
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val auth = FirebaseAuth.getInstance()
        auth.currentUser?.delete()
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
        private const val TAG = "ActivityRegisterDetails"
        private val HOMEACTIVITY = MainActivity::class.java
        private val LOGINACTIVITY = ActivityLogin::class.java
        private var checked = false
        private var gender = ""
    }
}