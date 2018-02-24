package edu.wpi.cs4518goathome;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreen extends AppCompatActivity {

    private static final String KEY = "LoginScreen";

    private FirebaseAuth mAuth;

    private EditText mLoginUsername;
    private EditText mLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        mAuth = FirebaseAuth.getInstance();

        Button loginButton = findViewById(R.id.loginButton);
        Button createButton = findViewById(R.id.createButton);
        final EditText mLoginUsername = findViewById(R.id.loginUsername);
        final EditText mLoginPassword = findViewById(R.id.loginPassword);

        /**
         * After logging in, if the user does not have all their information in the system, they should
         * be brought immediately to the UserProfileEdit page. If they are all set for entering information,
         * bring them to the map.
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mLoginUsername.getText().toString(),
                        mLoginPassword.getText().toString());
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createUser(mLoginUsername.getText().toString(),
                        mLoginPassword.getText().toString());
            }
        });

    }


    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            redirectUserToMap();
        }
    }

    private void signIn(String email, String password) {
        Log.d(KEY, "SignIn()" + email);
        if (!validateForm()) return;
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(KEY, "Login successful!");
                    redirectUserToMap();
                } else {
                    Log.d(KEY, "Login failed!", task.getException());
                    Toast.makeText(LoginScreen.this, "Authentication Failed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createUser(String email, String password) {
        Log.d(KEY, "createUser()" + email);
        if (!validateForm()) return;
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(KEY, "Login successful!");
                    redirectUserToProfile();
                } else {
                    Log.d(KEY, "Login failed!", task.getException());
                    Toast.makeText(LoginScreen.this, "Authentication Failed!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void redirectUserToProfile() {
        startActivity(new Intent(this, UserProfileEdit.class));
    }

    private void redirectUserToMap() {
        startActivity(new Intent(this, MapsActivity.class));
    }


    private boolean validateForm() {
        return true;
        // TODO: validate username and password before signing in
    }

}
