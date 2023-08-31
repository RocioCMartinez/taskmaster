package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUser;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Team;
import com.rocio.taskmaster.MainActivity;
import com.rocio.taskmaster.R;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class UserProfileActivity extends AppCompatActivity {
    public static final String TAG = "UserProfileActivity";
    public static final String USERNAME_TAG = "userName";

    public static final String TEAM_TAG = "teamSelected";
    SharedPreferences preferences;

    AuthUser authUser; // Lecture36 Follow up: Creating an auth user to track whether or a not a user is signed in. Used for rendering buttons
    CompletableFuture<List<Team>> teamsFuture = null;

    Spinner taskTeamSpinner;

    Button signUpButton;

    Button loginButton;

    Button logoutButton;

    TextView loggedEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        taskTeamSpinner = findViewById(R.id.UserProfileActivityTeamSpinner);
        teamsFuture = new CompletableFuture<>();

        preferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(this);

        signUpButton = findViewById(R.id.UserProfileActivitySignUpButton);
        loginButton = findViewById(R.id.UserProfileActivityLoginButton);
        logoutButton = findViewById(R.id.UserProfileActivityLogoutButton);
        loggedEmail = findViewById(R.id.UserProfileActivityEmailTextView);

        setupUserNameEditText();
        setUpTeamSpinner();
        setupSaveButton();
        setUpSignUpButton();
        setupLoginButton();
        setupLogoutButton();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /* Lecture36 Follow up: Determine if user is logged in.
         * If the user is logged in, only show the Logout button.
         * If a user is not logged in, only show the Sign Up/Login buttons
         * This can also be used to grab the user's attributes if you choose to use this
         * instead of the fetchUserAttributes in the MainActivity
         * NOTE: remember to call the render buttons function whether it fails or succeeds!
         */
        Amplify.Auth.getCurrentUser(
                success -> {
                    Log.i(TAG, "User authenticated with username: " + success.getUsername());
                    authUser = success;
                    runOnUiThread(() -> {
                        renderButtons();
                        renderLoggedEmail();
                    });
                },
                failure -> {
                    Log.i(TAG, "There is no current authenticated user");
                    authUser = null;
                    runOnUiThread(() -> {
                        renderButtons();
                        renderLoggedEmail();
                    });
                }
        );
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

    void setUpSignUpButton() {
        signUpButton.setOnClickListener(view -> {
            Intent goToSignUpActivityIntent = new Intent(UserProfileActivity.this, SignUpActivity.class);
            startActivity(goToSignUpActivityIntent);
        });
    }

    void setupLoginButton(){
        loginButton.setOnClickListener(view -> {
            Intent goToLoginActivityIntent = new Intent(UserProfileActivity.this, LoginActivity.class);
            startActivity(goToLoginActivityIntent);
        });
    }

    void setupLogoutButton(){
        logoutButton.setOnClickListener(view -> {
            //        Cognito LogOut Logic
            AuthSignOutOptions signOutOptions = AuthSignOutOptions.builder()
                    .globalSignOut(true)
                    .build();

            Amplify.Auth.signOut(signOutOptions,
                    signOutResults -> {
                        if(signOutResults instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                            Log.i(TAG, "Global sign out successful");
                            // Lecture36 Followup: Send user back to MainActivity on Logout
                            Intent goToMainActivity = new Intent(UserProfileActivity.this, MainActivity.class);
                            startActivity(goToMainActivity);
                        } else if (signOutResults instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                            Log.i(TAG, "Partial sign out successful");
                        } else if (signOutResults instanceof  AWSCognitoAuthSignOutResult.FailedSignOut) {
                            Log.i(TAG, "Logout failed: " + signOutResults);
                        }
                    }
            );
        });
    }

    void renderButtons() {
        if(authUser == null) {
            logoutButton.setVisibility(View.INVISIBLE);
            loginButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
        } else {
            logoutButton.setVisibility(View.VISIBLE);
            loginButton.setVisibility(View.INVISIBLE);
            signUpButton.setVisibility(View.INVISIBLE);
        }
    }

    private void renderLoggedEmail() {
        if (authUser != null) {
            // User is authenticated, display the username
            loggedEmail.setText("Logged in as: " + authUser.getUsername());
            loggedEmail.setVisibility(View.VISIBLE);
        } else {
            // User is not authenticated, hide the TextView
            loggedEmail.setVisibility(View.GONE);
        }
    }
}
