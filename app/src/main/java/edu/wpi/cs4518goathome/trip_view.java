package edu.wpi.cs4518goathome;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.net.Uri;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;

import edu.wpi.cs4518goathome.models.Trip;
import edu.wpi.cs4518goathome.models.User;


public class trip_view extends AppCompatActivity {

    private ImageView mProfilePic;
    private TextView mDriverName;
    private TextView mDriverMajor;
    private TextView mDriverPhone;
    private ImageButton mCallDriver;
    private ImageButton mTextDriver;
    private TextView mTripDate;
    private TextView mTripDestination;
    private TextView mTripDescription;
    private TextView mTripPrice;

    private FirebaseDatabase mDatabase;
    private FirebaseStorage mStorage;

    public static final String EXTRA_TRIP_ID = "TripId";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_view);
        mProfilePic = findViewById(R.id.profilePicture);
        mDriverName = findViewById(R.id.driverName);
        mDriverMajor = findViewById(R.id.driverMajor);
        mDriverPhone = findViewById(R.id.driverPhoneNumber);
        mCallDriver = findViewById(R.id.callDriver);
        mTextDriver = findViewById(R.id.textDriver);
        mTripDate = findViewById(R.id.tripDate);
        mTripDestination = findViewById(R.id.tripDestination);
        mTripDescription = findViewById(R.id.tripDescription);
        mTripPrice = findViewById(R.id.tripPrice);

        mCallDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callDriver(mDriverPhone.getText().toString());
            }
        });

        mTextDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textDriver(mDriverPhone.getText().toString());
            }
        });

        String tripId = getIntent().getStringExtra(EXTRA_TRIP_ID);
        if (tripId != null) {
        } else {
            //TODO: do something if extra isn't provided
        }

        mDatabase = FirebaseDatabase.getInstance();
        mStorage = FirebaseStorage.getInstance();
        final StorageReference mStorageRef = mStorage.getReference();

        final ValueEventListener driverListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User driver = dataSnapshot.getValue(User.class);
                mDriverName.setText(driver.name);
                mDriverMajor.setText(driver.major);
                mDriverPhone.setText(driver.phoneNumber);
                StorageReference picRef = mStorageRef.child(User.getProfilePic(dataSnapshot.getKey()));
                try {
                    final File file = File.createTempFile("images", "png");
                    picRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                            mProfilePic.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        DatabaseReference tripRef = mDatabase.getReference("/trips").child(tripId);
        ValueEventListener tripListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Trip selectedTrip = dataSnapshot.getValue(Trip.class);
                mTripDate.setText(selectedTrip.date);
                mTripDestination.setText(selectedTrip.name);
                mTripDescription.setText(selectedTrip.desc);
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();
                mTripPrice.setText(currencyFormatter.format(selectedTrip.cost));
                DatabaseReference driverRef = mDatabase.getReference("/users").child(selectedTrip.driver);
                driverRef.addListenerForSingleValueEvent(driverListener);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        tripRef.addListenerForSingleValueEvent(tripListener);

    }



    public void callDriver (String phoneNumber) {
        //TODO: Call driver
        //Intent callIntent = new Intent(Intent.ACTION_CALL);
        //callIntent.setData(Uri.parse("tel:123456789"));

        //This is just for testing
        Toast.makeText(trip_view.this, "Calling driver " + phoneNumber,
                Toast.LENGTH_SHORT).show();

    }

    public void textDriver (String phoneNumber) {
        //TODO: Text driver
        //Intent callIntent = new Intent(Intent.ACTION_CALL);
        //callIntent.setData(Uri.parse("tel:123456789"));

        //This is just for testing
        Toast.makeText(trip_view.this, "Texting driver " + phoneNumber,
                Toast.LENGTH_SHORT).show();
    }
}
