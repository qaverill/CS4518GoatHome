package edu.wpi.cs4518goathome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import edu.wpi.cs4518goathome.models.User;

public class UserProfileEdit extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private final static String KEY = "UsersProfileEdit";

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseDatabase mDatabase;

    private EditText mUsersName;
    private ImageView mProfilePic;
    private EditText mMajor;
    private EditText mPhone;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance();

        mUsersName = findViewById(R.id.usersName);
        mProfilePic = findViewById(R.id.profilePicture);
        mMajor = findViewById(R.id.usersMajor);
        mPhone = findViewById(R.id.usersPhone);
        Button mSaveButton = findViewById(R.id.Save);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                DatabaseReference dbRef = mDatabase.getReference("/users/" +  user.getUid());

                dbRef.setValue(
                        new User(mUsersName.getText().toString(),
                                mMajor.getText().toString(),
                                mPhone.getText().toString()));
                startActivity(new Intent(UserProfileEdit.this, Ride.class));
            }
        });

        ImageButton mEditProfilePicture = findViewById(R.id.editProfilePicture);

        mEditProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            // return to login activity if we are not logged in
            startActivity(new Intent(this, LoginScreen.class));
        } else {
            // set listeners for new user data
            updateUI(user);
        }
    }

    /**
     * Result from taking the photo
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // set the ImageView
            mProfilePic.setImageBitmap(imageBitmap);

            // Upload the image to firebase
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            StorageReference picRef = mStorageRef.child(mAuth.getCurrentUser().getUid() + "/profilePic.png");
            picRef.putBytes(byteArray);
        }
    }


    /**
     * Add value listeners for user's values changing
     * @param user A firebaseuser object
     */
    private void updateUI(FirebaseUser user) {
        DatabaseReference dbRef = mDatabase.getReference("/users").child(user.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) return;
                mUsersName.setText(user.name);
                mMajor.setText(user.major);
                mPhone.setText(user.phoneNumber);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        StorageReference picRef = mStorageRef.child(User.getProfilePic(user.getUid()));
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
}
