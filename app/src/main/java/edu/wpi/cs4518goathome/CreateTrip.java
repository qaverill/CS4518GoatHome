/**
 * Created by Quinn Averill
 */
package edu.wpi.cs4518goathome;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateTrip extends AppCompatActivity {

    private EditText chosenPrice;
    private EditText chosenTripInformation;

    //For picking the destination
    private EditText chosenDestination;

    //For picking the date
    private EditText chosenDate;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;

    private static final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1;

    private static final String TAG = "CreateTrip";

    //For submitting
    private Button createTrip;

    //For cancelling
    private Button cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_trip);

        //Don't auto open keyboard for edit text areas
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        //Destination Picker
        chosenDestination = findViewById(R.id.chosenDestination);
        chosenDestination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {

                    Intent intent =
                            new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_FULLSCREEN)
                                    .build(CreateTrip.this);
                    startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
                } catch (GooglePlayServicesRepairableException e) {
                    // TODO: Handle the error.
                } catch (GooglePlayServicesNotAvailableException e) {
                    // TODO: Handle the error.
                }
            }
        });

        //Date Picker
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }
        };
        chosenDate = findViewById(R.id.chosenDate);
        chosenDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(CreateTrip.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //Submitting the trip
        createTrip = findViewById(R.id.createTrip);
        createTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitTrip();
            }
        });

        //Cancelling the creation
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelCreation();
            }
        });

    }


    // A place has been received; use requestCode to track the request.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
                chosenDestination.setText(place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    /**
     * Updates the date label in UI or shows error
     */
    private void updateDateLabel(){
        String format = "MM/dd/yy";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);

        //Get today's date
        Calendar todayCalendar = Calendar.getInstance();
        todayCalendar.set(Calendar.HOUR_OF_DAY, 0);
        todayCalendar.set(Calendar.MINUTE, 0);
        todayCalendar.set(Calendar.SECOND, 0);
        todayCalendar.set(Calendar.MILLISECOND, 0);
        Date todayDate = todayCalendar.getTime();

        //If the entered date is before today, show error
        if (myCalendar.before(todayDate)){
            Toast.makeText(CreateTrip.this, "Date entered is in the past, date must be in the future!",
                    Toast.LENGTH_SHORT).show();
        }

        //Else, save the date
        else {
            chosenDate.setText(sdf.format(myCalendar.getTime()));
        }
    }

    /**
     * Called when the create a trip button is pressed. It sends all info to the database and launches
     * View your trips activity
     */
    private void submitTrip(){
        String destination = "destination"; //TODO: get the destination
        String date = chosenDate.getText().toString();
        String price = chosenPrice.getText().toString();
        String otherInformation = chosenTripInformation.getText().toString();

        //If there is no destination entered, show error
        if (destination.length() == 0){
            Toast.makeText(CreateTrip.this, "You must enter a destination for the trip",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //If there is no date entered, show error
        if (date.length() == 0){
            Toast.makeText(CreateTrip.this, "You must enter a date for the trip",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //If there is no price entered, make it equal 0
        if (price.length() == 0){
            price = "$0";
        }

        //TODO: Send to database

        //TODO: Launch View your Trips
        //startActivity(new Intent(this, ViewYourTrips.class));
    }

    /**
     * Called when the user presses the cancel button
     */
    private void cancelCreation(){
        startActivity(new Intent(this, MapsActivity.class));
    }
}
