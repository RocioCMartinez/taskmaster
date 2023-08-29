package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.rocio.taskmaster.R;

public class SignUpActivity extends AppCompatActivity {
    public static final String TAG = "SignUpActivity";

    Button submitButton;
    EditText emailEditText;
    EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailEditText = findViewById(R.id.SignUpActivityEmailEditText);
        passwordEditText = findViewById(R.id.SignUpActivityPasswordEditText);
        submitButton = findViewById(R.id.SignUpActivitySubmitButton);

//              Cognito Sign Up Logic
        Amplify.Auth.signUp(emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                AuthSignUpOptions.builder()
                        .userAttribute(AuthUserAttributeKey.email(), emailEditText.getText().toString())
                        .build(),
                successResponse -> Log.i(TAG, "Sign Up Successful: " + successResponse),
                failureResponse -> Log.i(TAG, "Sign Up Failed. Username: " + emailEditText.getText().toString() + " Error Message: " + failureResponse));

        setupSubmitButton();
    }

    void setupSubmitButton(){
        submitButton.setOnClickListener(v -> {

        });
    }
}