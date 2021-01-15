package group24.oplevelserbekaemperensomhed;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;

import java.util.Calendar;
import java.util.Date;

public class FragmentCreateEvent extends Fragment {

    EditText findAddressEditText;
    EditText editTextStart;
    EditText editTextEnd;
    EditText editTextDate;
    SwitchCompat switchAllDay;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;

    int startDay = 0;
    int startMonth = 0;
    int startYear = 0;

    String timeResult = ""; //Is used in the timePicker dialog
    String dateResult = ""; //Is used in the datePicker dialog


    public FragmentCreateEvent() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_create_event, container, false);
        initializeView(v);
        return v;
    }

    private void initializeView(View v){
        findAddressEditText = v.findViewById(R.id.editTextAddress);
        switchAllDay = v.findViewById(R.id.switch_all_day);
        editTextStart = v.findViewById(R.id.editTextStart);
        editTextEnd = v.findViewById(R.id.editTextEnd);
        editTextDate = v.findViewById(R.id.editTextDate);
        handleTimeAndDateFields();

    }

    private void handleTimeAndDateFields(){
        editTextDate.setInputType(InputType.TYPE_NULL);
        editTextStart.setInputType(InputType.TYPE_NULL);
        editTextEnd.setInputType(InputType.TYPE_NULL);

        switchAllDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextEnd.getText().clear();
                editTextStart.getText().clear();

                if (switchAllDay.isChecked()){
                    editTextStart.setHint(R.string.setStartDate);
                    editTextEnd.setHint(R.string.setEndDate);
                    editTextDate.setEnabled(false);
                }else{
                    editTextStart.setHint(R.string.setStartTime);
                    editTextEnd.setHint(R.string.setEndTime);
                    editTextDate.setEnabled(true);
                }
            }
        });

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handleDatePickerDialog(editTextDate);
                handleDateRangePickerDialog(editTextDate);
            }
        });

        editTextStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editTextStart.getText().toString();
                if (switchAllDay.isChecked()){
                    handleDatePickerDialog(editTextStart);
                }else{
                    handleTimePickerDialog(editTextStart);
                }
                editTextStart.setText(time);
            }
        });


        editTextEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String time = editTextEnd.getText().toString();
                if (switchAllDay.isChecked()){
                    handleDatePickerDialog(editTextEnd);
                }else{
                    handleTimePickerDialog(editTextEnd);
                }
                editTextEnd.setText(time);
            }
        });


    }

    private void handleTimePickerDialog(final EditText editText){
        //This code was inspired by an example from this site: https://www.tutlane.com/tutorial/android/android-timepicker-with-examples
        final Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                        editText.setText(sHour + ":" + sMinute);
                    }
                }, hour, minutes,true);
        timePickerDialog.show();
    }

    private void handleDatePickerDialog(final EditText editText){
        //This code was inspired by an example from this site: https://www.tutlane.com/tutorial/android/android-datepicker-with-examples
        final Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        // date picker dialog
        datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        editText.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        startDay = dayOfMonth;
                        startMonth = monthOfYear;
                        startYear = year;
                    }
                }, year, month, day);



        if (editText.getId() == R.id.editTextDate || editText.getId() == R.id.editTextStart){
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis()-1000); //making previous dates unavailable
        }
        if (editText.getId() == R.id.editTextEnd){
            Calendar minimumDateAsCalendar = Calendar.getInstance();
            minimumDateAsCalendar.set(startYear, startMonth, startDay);
            Date minimumDate = minimumDateAsCalendar.getTime();

            datePickerDialog.getDatePicker().setMinDate(minimumDate.getTime());
        }
        datePickerDialog.show();
    }

    private void handleDateRangePickerDialog(final EditText editText){
        //This code was inspired by an example from this site: https://www.geeksforgeeks.org/material-design-date-picker-in-android/
        // date picker dialog


        MaterialDatePicker.Builder<Pair<Long, Long>> materialDateBuilder = MaterialDatePicker.Builder.dateRangePicker();
        materialDateBuilder.setTitleText("SELECT A DATE RANGE");
        final MaterialDatePicker materialDatePicker = materialDateBuilder.build();
        materialDatePicker.show(getActivity().getSupportFragmentManager(), "MATERIAL_DATE_PICKER");

        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener() {
            @Override
            public void onPositiveButtonClick(Object selection) {
                editText.setText(materialDatePicker.getHeaderText());
            }
        });
    }
}