package edu.wpi.cs4518goathome;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by jsbremner on 2/23/18.
 */

public class ViewYourTrips extends AppCompatActivity {

    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;

    private ListView mTripList;
    private Button mAddATrip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_your_trips);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();

        mTripList = findViewById(R.id.listView);
        mAddATrip = findViewById(R.id.button2);
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
