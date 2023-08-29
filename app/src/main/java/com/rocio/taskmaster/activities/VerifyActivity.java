package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.core.Amplify;
import com.rocio.taskmaster.R;

public class VerifyActivity extends AppCompatActivity {
    public static final String TAG = "VerifyActivity";

    Button submitButton;

    EditText emailEditText;

    EditText verificationCodeEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify);

        emailEditText = findViewById(R.id.VerifyActivityEmailEditText);
        verificationCodeEditText = findViewById(R.id.VerifyActivityVerificationCodeEditText);
        submitButton = findViewById(R.id.VerifyActivitySumitButton);

        Intent intent = getIntent();
        if (intent != null) {
            String email = intent.getStringExtra("email");
            if (email != null) {
                emailEditText.setText(email);
            }
        }

        setupSubmitButton();
    }

    void setupSubmitButton() {
        //      Cognito Verification Logic
        submitButton.setOnClickListener(view -> Amplify.Auth.confirmSignUp(emailEditText.getText().toString(),
                verificationCodeEditText.getText().toString(),
                success -> {Log.i(TAG, "Verification succeeded: " + success);
                    Intent goToLoginIntent = new Intent(VerifyActivity.this, LoginActivity.class);
                    goToLoginIntent.putExtra("email", emailEditText.getText().toString());
                    startActivity(goToLoginIntent);
                },
                failure -> Log.i(TAG, "Verification failed: " + failure)));

    }
}