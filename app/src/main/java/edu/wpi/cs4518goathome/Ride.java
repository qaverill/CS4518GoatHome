package edu.wpi.cs4518goathome;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;


/**
 * Created by AlazarGenene on 2/14/18.
 */

public class Ride extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ride);

        Button requestButton = (Button) findViewById(R.id.request_ride);
        Button giveRide = (Button) findViewById(R.id.request_ride);


    }
}