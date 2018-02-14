package edu.wpi.cs4518goathome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LoginScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        Button loginButton = (Button)findViewById(R.id.loginButton);


        /**
         * After logging in, if the user does not have all their information in the system, they should
         * be brought immediately to the UserProfileEdit page. If they are all set for entering information,
         * bring them to the map.
         */
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginScreen.this, UserProfileEdit.class));
            }
        });
    }

}
