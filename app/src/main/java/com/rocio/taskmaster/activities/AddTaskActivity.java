package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.amplifyframework.datastore.generated.model.Team;
import com.rocio.taskmaster.R;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTaskActivity extends AppCompatActivity {
    private final String TAG = "AddTaskActivity";

    CompletableFuture<List<Team>> teamsFuture = null;

    Button saveButton;
    EditText taskTitleEditText;
    EditText taskBodyEditText;
    Spinner taskStateSpinner;

    Spinner taskTeamSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        teamsFuture = new CompletableFuture<>();

        saveButton = findViewById(R.id.AddTaskActivityButton);
        taskStateSpinner = findViewById(R.id.AddTaskActivityTaskStateSpinner);
        taskTitleEditText = findViewById(R.id.AddTaskActivityTaskTitle);
        taskBodyEditText = findViewById(R.id.AddTaskActivityDescription);
        taskTeamSpinner = findViewById(R.id.AddTaskTeamSpinner);

        setUpTaskStateSpinner();
        setUpTaskTeamSpinner();
        setUpAddTaskButton();
    }


    void setUpTaskStateSpinner() {
        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()
        ));
    }

    void setUpAddTaskButton(){

        saveButton.setOnClickListener(view -> {
            String selectedTeamString = taskTeamSpinner.getSelectedItem().toString();

            List<Team> teams = null;
            try {
                teams = teamsFuture.get();
            } catch (InterruptedException ie) {
                Log.e(TAG, "InterruptedException while getting teams");
                Thread.currentThread().interrupt();
            } catch (ExecutionException ee) {
                Log.e(TAG, "ExecutionException while getting teams");
            }
            assert teams != null;
            Team selectedTeam = teams.stream().filter(t -> t.getTeamName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);

//            TODO: Make a DynamoDB/GraphQL call
            Task taskToSave = Task.builder()
                    .title(taskTitleEditText.getText().toString())
                    .body(taskBodyEditText.getText().toString())
                            .dateCreated(new Temporal.DateTime(new Date(), 0))
                                    .taskState((TaskStateEnum) taskStateSpinner.getSelectedItem())
                                     .teamP(selectedTeam)
                                            .build();

            Amplify.API.mutate(
                    ModelMutation.create(taskToSave),
                    successResponse -> Log.i(TAG, "AddTaskActivity.setUpAddTaskButton(): created task successfully"),
                    failureResponse -> Log.i(TAG, "AddTaskActivity.setUpAddTaskButton(): failure response " + failureResponse)
            );
            Toast.makeText(AddTaskActivity.this, "Task saved!", Toast.LENGTH_SHORT).show();
        });
    }

    void setUpTaskTeamSpinner(){
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read teams successfully");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for(Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getTeamName());
                    }
                    teamsFuture.complete(teams);

                    runOnUiThread(() -> taskTeamSpinner.setAdapter(new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            teamNames
                    )));
                },
                failure -> {
                    teamsFuture.complete(null);
                    Log.i(TAG, "Failed to read teams");
                }
        );
    }
}