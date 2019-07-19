package com.example.missitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button createAccountButton;
    private FirebaseAuth auth;
    private ProgressDialog connectionProgress;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        TAG = "Login Activity:";

        auth = FirebaseAuth.getInstance();

        connectionProgress = new ProgressDialog(this);
        connectionProgress.setTitle("Logging In");
        connectionProgress.setMessage("Please wait while we log you in");
        connectionProgress.setCanceledOnTouchOutside(false);

        emailInput = findViewById(R.id.registerInputEmail);
        passwordInput = findViewById(R.id.registerInputPassword);
        createAccountButton = findViewById(R.id.registerCreateAccountButton);

        createAccountButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString();
                String password = passwordInput.getText().toString();

                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this, "Please fill out all the fields", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Logging in for " + email + " and " + password, Toast.LENGTH_SHORT).show();
                    connectionProgress.show();
                    loginFirebaseUser(email, password);
                }
            }
        });

    }

    private void loginFirebaseUser(String email, String password) {
        Log.d(TAG, "loginFirebaseUser: started");
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        connectionProgress.dismiss();
                        String TAG = "loginFirebaseUser:";
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
//                            FirebaseUser user = auth.getCurrentUser();
                            Intent loginToMainIntent = new Intent(LoginActivity.this, ConversationListActivity.class);
                            loginToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(loginToMainIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Firebase authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
