package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.rocio.taskmaster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = "UserProfileActivity";
    public static final String USERNAME_TAG = "userName";

    public static final String TEAM_TAG = "teamSelected";
    SharedPreferences preferences;
    CompletableFuture<List<Team>> teamsFuture = null;

    Spinner taskTeamSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        taskTeamSpinner = findViewById(R.id.UserProfileActivityTeamSpinner);
        teamsFuture = new CompletableFuture<>();

        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        setupUserNameEditText();
        setUpTeamSpinner();
        setupSaveButton();
    }

    void setupUserNameEditText(){
       String userName = preferences.getString(USERNAME_TAG, null);
        ((EditText)findViewById(R.id.UserProfileActivityUserNameInput)).setText(userName);
    }

    void setUpTeamSpinner() {
        String teamSelected = preferences.getString(TEAM_TAG, null);
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read team successfully");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for (Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getTeamName());
                    }
                    teamsFuture.complete(teams);

                    runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                                this,
                                android.R.layout.simple_spinner_item,
                                teamNames
                        );
                        taskTeamSpinner.setAdapter(adapter);

                        // Find the index of the selected team name
                        int selectedTeamIndex = teamNames.indexOf(teamSelected);
                        if (selectedTeamIndex != -1) {
                            taskTeamSpinner.setSelection(selectedTeamIndex);
                        } else {
                            // If teamSelected is not in the list, set a default selection (e.g., first item)
                            taskTeamSpinner.setSelection(0);
                        }
                    });
                },
                failure -> {
                    teamsFuture.complete(null);
                    Log.i(TAG, "Failed to read team");
                }
        );
    }


    void setupSaveButton() {
        ((Button)findViewById(R.id.UserProfileActivitySaveBtn)).setOnClickListener(view -> {
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            EditText userNameEditText = (EditText) findViewById(R.id.UserProfileActivityUserNameInput);
            String userNameString = userNameEditText.getText().toString();

            Spinner teamSelectorSpinner = (Spinner)findViewById(R.id.UserProfileActivityTeamSpinner);
            String teamSelected = teamSelectorSpinner.getSelectedItem().toString();

            preferencesEditor.putString(USERNAME_TAG, userNameString);
            preferencesEditor.putString(TEAM_TAG, teamSelected);
            preferencesEditor.apply();// Nothing will save without this line

            Toast.makeText(UserProfileActivity.this, "Username/Team saved!", Toast.LENGTH_SHORT).show();
        });
    }



}