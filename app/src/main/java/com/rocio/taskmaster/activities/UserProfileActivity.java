package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.rocio.taskmaster.R;

public class UserProfileActivity extends AppCompatActivity {
    public static final String USERNAME_TAG = "userName";// At top to be accessed elsewhere
    SharedPreferences preferences; //Place near top of class

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setupUserNameEditText();
        setupSaveButton();
    }

    void setupUserNameEditText(){
       String userName = preferences.getString(USERNAME_TAG, null);
        ((EditText)findViewById(R.id.UserProfileActivityUserNameInput)).setText(userName);
    }

    void setupSaveButton() {
        ((Button)findViewById(R.id.UserProfileActivitySaveBtn)).setOnClickListener(view -> {
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            EditText userNameEditText = (EditText) findViewById(R.id.UserProfileActivityUserNameInput);
            String userNameString = userNameEditText.getText().toString();

            preferencesEditor.putString(USERNAME_TAG, userNameString);
            preferencesEditor.apply();// Nothing will save without this line
                //Can use either option to confirm save
//            Snackbar.make(findViewById(R.id.userProfileActivityView), "Username saved!", Snackbar.LENGTH_SHORT).show();
            Toast.makeText(UserProfileActivity.this, "Username saved!", Toast.LENGTH_SHORT).show();
        });
    }
}