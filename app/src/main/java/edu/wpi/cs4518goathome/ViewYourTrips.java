package edu.wpi.cs4518goathome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import edu.wpi.cs4518goathome.models.Trip;

/**
 * Created by jsbremner on 2/23/18.
 */

public class ViewYourTrips extends AppCompatActivity {

    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private ListView mTripList;
    private Button mAddATrip;

    private ArrayList<Trip> trips;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_your_trips);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();

        mTripList = findViewById(R.id.listView);
        mAddATrip = findViewById(R.id.button2);

        mAddATrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ViewYourTrips.this, CreateTrip.class));
            }
        });

        trips = new ArrayList<>();

        final ArrayAdapter<Trip> tripAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, trips);
        mTripList.setAdapter(tripAdapter);
        mTripList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Trip selectedTrip = trips.get(i);
                Log.i("ViewYourTrips", "selected trip: " + selectedTrip.getUid() + selectedTrip.name + selectedTrip.latitude);
            }
        });

        // Get all the trips where this user is the driver
        Query ref = mDatabase.getReference("/trips").orderByChild("driver").equalTo(mAuth.getUid());
        ValueEventListener viewYourTrips = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.getChildren()) {
                    Trip trip = d.getValue(Trip.class);
                    trip.setUid(d.getKey());
                    trips.add(trip);
                    tripAdapter.notifyDataSetChanged();
                    Log.i("ViewYourTrips", trip.name);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        // only call the data fetch once
        ref.addListenerForSingleValueEvent(viewYourTrips);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // return to login activity if we are not logged in
            startActivity(new Intent(this, LoginScreen.class));
        } else {
        }
    }
}
