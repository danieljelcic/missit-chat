package com.example.missitchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class WelcomeActivity extends AppCompatActivity {

    private Button registerButton;
    private Button logInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        registerButton = findViewById(R.id.welcomeRegisterButton);

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent welcomeToRegisterIntent = new Intent(WelcomeActivity.this, RegisterActivity.class);
                startActivity(welcomeToRegisterIntent);
            }
        });



        logInButton = findViewById(R.id.welcomeLogInButton);

        logInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent welcomeToLoginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                startActivity(welcomeToLoginIntent);
            }
        });
    }
}
