package edu.wpi.cs4518goathome;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class UserProfileEdit extends AppCompatActivity {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private final static String KEY = "UsersProfileEdit";

    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;

    private EditText mUsersName;
    private ImageView mProfilePic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile_edit);

        mAuth = FirebaseAuth.getInstance();

        mStorageRef = FirebaseStorage.getInstance().getReference();

        mUsersName = findViewById(R.id.usersName);
        mProfilePic = findViewById(R.id.profilePicture);

        Button mSaveButton = findViewById(R.id.Save);

        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(mUsersName.getText().toString()).build();
                user.updateProfile(profileUpdates);
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
            startActivity(new Intent(this, LoginScreen.class));
        } else {
            updateUI(user);
        }
    }

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

            // update the location of the image in the user's info
            updateFirebaseProfilePicture(picRef.getPath());
        }
    }

    private void updateUI(FirebaseUser user) {
        mUsersName.setText(user.getDisplayName());

        // load the profile picture
        StorageReference picRef = mStorageRef.child(user.getPhotoUrl().toString());
        try {
            final File file = File.createTempFile("images", "png");
            picRef.getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                    mProfilePic.setImageBitmap(bitmap);
                }
            });
        } catch (IOException e) {}
    }

    /**
     * Update the user's Authentication Profile Picture
     * @param picture A firebase cloud storage path
     */
    private void updateFirebaseProfilePicture(String picture) {
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(picture)).build();
        user.updateProfile(profileUpdates);
    }
}
