package com.rocio.taskmaster;

import static com.rocio.taskmaster.activities.UserProfileActivity.TEAM_TAG;
import static com.rocio.taskmaster.activities.UserProfileActivity.USERNAME_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.auth.AuthUserAttributeKey;
import com.amplifyframework.auth.cognito.result.AWSCognitoAuthSignOutResult;
import com.amplifyframework.auth.options.AuthSignOutOptions;
import com.amplifyframework.auth.options.AuthSignUpOptions;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.rocio.taskmaster.activities.AddTaskActivity;
import com.rocio.taskmaster.activities.AllTasksActivity;
import com.rocio.taskmaster.activities.UserProfileActivity;
import com.rocio.taskmaster.adapters.TaskListRecyclerViewAdapter;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TAG = "MainActivity";
    public static final String TASK_NAME_EXTRA_TAG = "taskName";
    public static final String TASK_DESCRIPTION_TAG = "taskDescription";
    SharedPreferences preferences;


    List<Task> tasks = new ArrayList<>();




    TaskListRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




//        Cognito LogOut Logic
        AuthSignOutOptions signOutOptions = AuthSignOutOptions.builder()
                        .globalSignOut(true)
                                .build();

        Amplify.Auth.signOut(signOutOptions,
                signOutResults -> {
            if(signOutResults instanceof AWSCognitoAuthSignOutResult.CompleteSignOut) {
                Log.i(TAG, "Global signout successful");
            } else if (signOutResults instanceof AWSCognitoAuthSignOutResult.PartialSignOut) {
                Log.i(TAG, "Partial signout successful");
            } else if (signOutResults instanceof  AWSCognitoAuthSignOutResult.FailedSignOut) {
                Log.i(TAG, "Logout failed: " + signOutResults.toString());
            }
                }
        );

        preferences = PreferenceManager.getDefaultSharedPreferences(this);


        setUpUserProfileButton();
        updateTaskListFromDatabase();
        setupTaskRecyclerView();
        setUpAddTaskFormButton();
        setUpAllTasksButton();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setupUsernameTextView();
        updateTaskListFromDatabase();
    }



    void setUpUserProfileButton(){
        ((ImageView)findViewById(R.id.MainActivityUserProfileButton)).setOnClickListener(view -> {
            Intent goToUserProfileActivityIntent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(goToUserProfileActivityIntent);
        });
    }

    @SuppressLint("SetTextI18n")
    void setupUsernameTextView(){
        String userName = preferences.getString(USERNAME_TAG, "No username");
        ((TextView)findViewById(R.id.MainActivityUserName)).setText(userName + "'s Tasks");
    }



    void setupTaskRecyclerView(){

        RecyclerView taskListRecyclerView = (RecyclerView) findViewById(R.id.MainActivityTaskRecyclerView);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);

        taskListRecyclerView.setLayoutManager(layoutManager);


        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.task_fragment_spacing);
        taskListRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = spaceInPixels;
            }
        });


        adapter = new TaskListRecyclerViewAdapter(tasks, this);
        taskListRecyclerView.setAdapter(adapter);

    }

    void setUpAddTaskFormButton(){
        // 4 steps to event listener set up
        //1: Get UI element by ID
        Button addTaskButton = findViewById(R.id.MainActivityAddTaskButton);
        //2/3: Add an onClickListener
        addTaskButton.setOnClickListener(view -> {
            //4: Define the callback method
            System.out.println("Add task button was pressed!");

            //create + trigger Intent(w/startActivity)
            Intent goToAddTaskIntent = new Intent(MainActivity.this, AddTaskActivity.class);
            startActivity(goToAddTaskIntent);

        });
    }

    void setUpAllTasksButton(){
        // 4 steps to event listener set up
        //1: Get UI element by ID
        Button allTasksButton = findViewById(R.id.MainActivityAllTasksButton);
        //2/3: Add an onClickListener
        allTasksButton.setOnClickListener(view -> {
            //4: Define the callback method

            //create + trigger Intent(w/startActivity)
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasksActivity.class);
            startActivity(goToAllTasksIntent);

        });
    }



    void updateTaskListFromDatabase(){
        String teamName = preferences.getString(TEAM_TAG, null);
//        tasks.clear();
//        TODO: Make a dynamoDB call
        Amplify.API.query(
                ModelQuery.list(Task.class),
                success -> {
                    Log.i(TAG, "Read tasks successfully!");
                    tasks.clear();
                    for(Task databaseTask : success.getData()) {
                        // Only view tasks by team

                            if(databaseTask.getTeamP() != null && databaseTask.getTeamP().getTeamName().equals(teamName)) {
                                tasks.add(databaseTask);
                            }
                    }
                    runOnUiThread(() -> adapter.notifyDataSetChanged());
                },
                failure -> Log.i(TAG, "Failed to read tasks")
        );
    }

}