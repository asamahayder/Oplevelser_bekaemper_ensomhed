package group24.oplevelserbekaemperensomhed.view.event;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.chip.Chip;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.util.Pair;
import androidx.viewpager.widget.ViewPager;

import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import group24.oplevelserbekaemperensomhed.R;
import group24.oplevelserbekaemperensomhed.data.DateDTO;
import group24.oplevelserbekaemperensomhed.data.EventDTO;
import group24.oplevelserbekaemperensomhed.data.LocalData;
import group24.oplevelserbekaemperensomhed.data.UserDTO;
import group24.oplevelserbekaemperensomhed.logic.ViewPagerAdapter;
import group24.oplevelserbekaemperensomhed.logic.firebase.FirebaseDAO;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyCallBack;
import group24.oplevelserbekaemperensomhed.logic.firebase.MyUploadPicturesListener;

public class ActivityCreateEvent extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    //LinearLayout linearLayout;
    ConstraintLayout constraintLayout;

    EditText editTextTitle;
    EditText findAddressEditText;
    EditText editTextStart;
    EditText editTextEnd;
    EditText editTextDate;
    EditText editTextAmount;
    EditText editTextAbout;
    SwitchCompat switchAllDay;
    SwitchCompat switchPrice;
    SwitchCompat switchOnline;
    TimePickerDialog timePickerDialog;
    ImageView backButton;
    LinearLayout submitButton2;
    String address = "";

    //These are used to save the chosen start/end time and put them back in their fields in case a user activates switch multiple times
    String chosenStartTime = "";
    String chosenEndTime = "";

    //same as above but for price
    String amount ="";

    //Chips
    Chip chipEducation;
    Chip chipExercise;
    Chip chipEntertainment;
    Chip chipMusicNightlife;
    Chip chipCulture;
    Chip chipFoodDrinks;
    Chip chipGaming;
    ArrayList<Chip> listOfChips = new ArrayList<>();
    String chosenCategory = "";

    //To check if start date is before current date
    Long startDate;


    //Image stuff
    int getImageResultCode = 100;
    Uri imageUri;
    Chip getImageButton;
    ArrayList<String> pictures = new ArrayList<>();
    ArrayList<Uri> picturesAsUris = new ArrayList<>();
    ViewPager viewPager = null;

    //For uploading image
    FirebaseStorage firebaseStorage;
    StorageReference storageReference;
    FirebaseDAO firebaseDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        firebaseDAO = new FirebaseDAO();

        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        initializeView();
    }

    private void initializeView(){
        constraintLayout = findViewById(R.id.create_event_constraintlayout);

        findAddressEditText = findViewById(R.id.editTextAddress);
        switchAllDay = findViewById(R.id.switch_all_day);
        switchPrice = findViewById(R.id.switch_price);
        switchOnline = findViewById(R.id.switch_online);
        editTextTitle = findViewById(R.id.create_event_title);
        editTextStart = findViewById(R.id.editTextStart);
        editTextEnd = findViewById(R.id.editTextEnd);
        editTextDate = findViewById(R.id.editTextDate);
        editTextAmount = findViewById(R.id.editTextAmount);
        editTextAbout = findViewById(R.id.editAbout);
        submitButton2 = findViewById(R.id.create_event_submit2);
        backButton = findViewById(R.id.a_create_event_backButton);
        getImageButton = findViewById(R.id.create_event_choose_pictures_button);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Chips
        chipCulture = findViewById(R.id.chipCulture);
        chipEducation = findViewById(R.id.chipEducation);
        chipEntertainment = findViewById(R.id.chipEntertainment);
        chipExercise = findViewById(R.id.chipExercise);
        chipFoodDrinks = findViewById(R.id.chipFoodDrinks);
        chipGaming = findViewById(R.id.chipGaming);
        chipMusicNightlife = findViewById(R.id.chipMusicNightlife);
        listOfChips.add(chipCulture);
        listOfChips.add(chipEducation);
        listOfChips.add(chipEntertainment);
        listOfChips.add(chipExercise);
        listOfChips.add(chipFoodDrinks);
        listOfChips.add(chipGaming);
        listOfChips.add(chipMusicNightlife);

        //Setting chip clickListener
        chipCulture.setOnCheckedChangeListener(this);
        chipEducation.setOnCheckedChangeListener(this);
        chipEntertainment.setOnCheckedChangeListener(this);
        chipExercise.setOnCheckedChangeListener(this);
        chipFoodDrinks.setOnCheckedChangeListener(this);
        chipGaming.setOnCheckedChangeListener(this);
        chipMusicNightlife.setOnCheckedChangeListener(this);




        handleTimeAndDateFields();

        submitButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    handleOnSubmit();
                } catch (ParseException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),"Time Parser error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Handling address field and google places
        String key = getString(R.string.api_key); //Our Google API Key
        if (!Places.isInitialized()) { //Places is an API that enables us to autocomplete addresses
            Places.initialize(getApplicationContext(), key);
        }

        findAddressEditText.setInputType(InputType.TYPE_NULL);
        findAddressEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleAddressSearchWithAutoComplete();
            }
        });


        //Handling online as address
        switchOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(switchOnline.isChecked()){
                    address = findAddressEditText.getText().toString();
                    findAddressEditText.setEnabled(false);
                    findAddressEditText.setText("Online");
                    findAddressEditText.setHint("Online");
                }else{
                    findAddressEditText.setText(address);
                    findAddressEditText.setHint("Set an address");
                    findAddressEditText.setEnabled(true);
                }
            }
        });

        //Handling price field
        switchPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchPrice.isChecked()){
                    amount = editTextAmount.getText().toString();
                    editTextAmount.setEnabled(false);
                    editTextAmount.setText("Free");
                    editTextAmount.setHint("Free");
                }else{
                    editTextAmount.setText(amount);
                    editTextAmount.setHint("Amount in €");
                    editTextAmount.setEnabled(true);
                }
            }
        });


        getImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                gallery.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); //Alowing the selection of multiple files
                gallery.setType("image/*"); //specifying we only want image files
                startActivityForResult(gallery, getImageResultCode);
                //Activity for result, is an activity that returns something when finished.
                // Later we receive data in onActivityResult
            }
        });

    }

    //This method lets the user choose a date, and start/end time, in  a user friendly way, instead of typing it all themself.
    private void handleTimeAndDateFields(){
        editTextDate.setInputType(InputType.TYPE_NULL);
        editTextStart.setInputType(InputType.TYPE_NULL);
        editTextEnd.setInputType(InputType.TYPE_NULL);

        //Handles if 'All-day' is chosen
        switchAllDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchAllDay.isChecked()){
                    editTextStart.setText("All Day");
                    editTextEnd.setText("All Day");
                    editTextStart.setHint("All Day");
                    editTextEnd.setHint("All Day");
                    editTextStart.setEnabled(false);
                    editTextEnd.setEnabled(false);
                }else{
                    editTextStart.setEnabled(true);
                    editTextEnd.setEnabled(true);
                    editTextStart.setText(chosenStartTime);
                    editTextEnd.setText(chosenEndTime);
                    editTextStart.setHint("Start Time");
                    editTextEnd.setHint("End Time");
                }
            }
        });

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleDateRangePickerDialog(editTextDate);
            }
        });

        editTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editTextStart.getText().toString();
                handleTimePickerDialog(editTextStart);
                editTextStart.setText(time);
            }
        });

        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editTextEnd.getText().toString();
                handleTimePickerDialog(editTextEnd);
                editTextEnd.setText(time);
            }
        });
    }


    //This opens an interactive clock where you can choose a time
    //Inspired by tutorial example from this site: https://www.tutlane.com/tutorial/android/android-timepicker-with-examples
    private void handleTimePickerDialog(final EditText editText){
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                String timeAsString = sHour + ":" + sMinute;
                if (editText.getId() == R.id.editTextStart){
                    chosenStartTime = timeAsString;
                }else if (editText.getId() == R.id.editTextEnd){
                    chosenEndTime = timeAsString;
                }
                editText.setText(timeAsString);
            }
        }, hour, minutes,true);
        timePickerDialog.show();
    }


    //This opens an interactive date interface where you can choose a range of dates.
    //This code was inspired by an example from this site: https://www.geeksforgeeks.org/material-design-date-picker-in-android/
    private void handleDateRangePickerDialog(final EditText editText){
        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE RANGE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Pair<Long,Long>>() {
            @Override
            public void onPositiveButtonClick(Pair<Long, Long> selection) {
                startDate = selection.first;
                editText.setText(materialDatePicker.getHeaderText());
            }
        });
    }


    //Checking if all requirements are fulfilled and submitting to database
    private void handleOnSubmit() throws ParseException {
        //CHECKING REQUIREMENTS
        if (pictures.isEmpty()){
            Toast.makeText(getApplicationContext(),"Need at least 1 picture",Toast.LENGTH_SHORT).show();
            return;
        }else if (editTextTitle.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Need an event title",Toast.LENGTH_SHORT).show();
            return;
        }else if (chosenCategory.equals("")){
            Toast.makeText(getApplicationContext(),"Need a category",Toast.LENGTH_SHORT).show();
            return;
        }else if (editTextDate.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Need a date",Toast.LENGTH_SHORT).show();
            return;
        } else if (!switchOnline.isChecked() && findAddressEditText.getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(),"Need an Address",Toast.LENGTH_SHORT).show();
            return;
        } else if (!switchPrice.isChecked() && editTextAmount.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Need a price",Toast.LENGTH_SHORT).show();
            return;
        } else if (editTextAbout.getText().toString().equals("")){
            Toast.makeText(getApplicationContext(),"Need a description",Toast.LENGTH_SHORT).show();
            return;
        }

        if (!switchAllDay.isChecked()) { //checking if time is set correct, and only doing it if All-day is not checked
            String startTime = editTextStart.getText().toString();
            String endTime = editTextEnd.getText().toString();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("hh:mm");
            Date startTimeAsDate = dateFormatter.parse(startTime);
            Date endTimeAsDate = dateFormatter.parse(endTime);

            if (endTimeAsDate.before(startTimeAsDate) || startTimeAsDate.equals(endTimeAsDate)) {
                Toast.makeText(getApplicationContext(), "Start time should be before end time", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if(!editTextDate.equals("")) { //Checking that the date range is before current date
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            long now = cal.getTimeInMillis();

            long difference = now-startDate;
            if (difference > 0){ //startDate is before now
                Toast.makeText(this, "Date start time can not be in past", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        //Handling upload of pictures and getting their new urls
        //Showing progress dialog
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();


        //Here we are using callbacks to handle state change in objects from another thread. It enables us to keep the UI thread working, while
        //work in the firebase threads also is happening. It also minimizes a tight couple between the classes.
        firebaseDAO.uploadImages(picturesAsUris, new MyUploadPicturesListener() {
                    @Override
                    public void onSuccess(@NotNull Object object) {
                        ArrayList<String> pictureDownloadLinks = (ArrayList<String>)object;
                        progressDialog.dismiss();
                        createEvent(pictureDownloadLinks);
                    }

                    @Override
                    public void onProgress(@NotNull Object object) {
                        int counter = (int)object;
                        progressDialog.setMessage(counter + " images left");
                    }

                    @Override
                    public void onFailure(@NotNull Object object) {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityCreateEvent.this, "Could not upload images", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    //This creates the event inside the database
    private void createEvent(ArrayList<String> pictureDownloadLinks){
        //CREATING EVENT OBJECT
        LocalData data = LocalData.INSTANCE;
        UserDTO user = data.getUserData();

        DateDTO dateDTO = new DateDTO(editTextDate.getText().toString(), editTextStart.getText().toString(), editTextEnd.getText().toString());

        ArrayList<UserDTO> participants = new ArrayList<>();
        participants.add(user);

        EventDTO eventDTO = new EventDTO(user, participants, editTextAbout.getText().toString(), editTextTitle.getText().toString(),
                dateDTO, 13, chosenCategory, findAddressEditText.getText().toString(), editTextAmount.getText().toString(),
                pictureDownloadLinks);

        firebaseDAO.createEvent(eventDTO, new MyCallBack() {
            @Override
            public void onCallBack(@NotNull Object object) {
                String message = (String)object;
                onEventCreated(message);
            }
        });

    }

    //This is handling the result of creating event
    private void onEventCreated(String message){
        if(message.equals("success")){
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"Something went wrong with firebase",Toast.LENGTH_SHORT).show();
        }
    }

    //Called when we want to search for address
    public void handleAddressSearchWithAutoComplete() {
        //This is a modified version from https://medium.com/skillhive/android-google-places-autocomplete-feature-bb3064308f05

        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS);
        // Start the autocomplete intent.
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, fields).setCountry("DK") //Denmark
                .build(this);
        startActivityForResult(intent, 1);
    }

    //Handling category selection, and only allowing a single category to be selected.
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked){
            chosenCategory = buttonView.getText().toString();
            for (Chip chip : listOfChips) {
                if (chip.getId() != buttonView.getId() && chip.isChecked()) chip.setChecked(false);
            }
        }
    }

    //Used for result activities which are actvities that return something
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        //Modified version of https://medium.com/skillhive/android-google-places-autocomplete-feature-bb3064308f05
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) { //This is for the address result activity
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Toast.makeText(ActivityCreateEvent.this,"address:" + place.getAddress() + "Name:" + place.getName(), Toast.LENGTH_LONG).show();
                address = place.getAddress();
                String name = place.getName();
                System.out.println("Name: " + name + ", Address: " + address);
                findAddressEditText.setText(address);

            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                Status status = Autocomplete.getStatusFromIntent(data);
                Toast.makeText(ActivityCreateEvent.this, "Error: " + status.getStatusMessage(), Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {

            }
        }

        if (resultCode == RESULT_OK && requestCode == getImageResultCode) { //this is for the select images from gallery activity
            pictures.clear();
            picturesAsUris.clear();
            ClipData clipData = data.getClipData();
            if (clipData != null){//Checking if user selected multiple
                if (clipData.getItemCount() > 3){//Checking if exceeded max limit
                    Toast.makeText(ActivityCreateEvent.this, "Error: Max 3 pictures", Toast.LENGTH_LONG).show();
                    return;
                }else{
                    for (int i = 0; i <clipData.getItemCount(); i++) {
                        Uri imageUri = clipData.getItemAt(i).getUri();
                        picturesAsUris.add(imageUri);
                        pictures.add(imageUri.toString());
                    }
                }

            }else{
                imageUri = data.getData();
                pictures.add(imageUri.toString());
                picturesAsUris.add(imageUri);
            }


            //Showing pictures on viewpager, and resetting viewpager if already existing
            if (viewPager != null) {
                constraintLayout.removeView(viewPager);
            }
            createViewPager();
        }
    }

    //Handling the creation for the viewpager that enables pictures to slide
    private void createViewPager(){
        viewPager = new ViewPager(this);
        viewPager.setId(View.generateViewId());
        viewPager.setLayoutParams(new ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0));
        ((ConstraintLayout.LayoutParams)viewPager.getLayoutParams()).dimensionRatio ="1:1";

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), pictures, R.layout.fragment_profile_event_1_viewpager, null);
        viewPager.setAdapter(adapter);
        constraintLayout.addView(viewPager);

        //Moving set images button below viewpager
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(getImageButton.getId(), ConstraintSet.TOP, viewPager.getId(), ConstraintSet.BOTTOM, 20);
        constraintSet.applyTo(constraintLayout);
    }
}