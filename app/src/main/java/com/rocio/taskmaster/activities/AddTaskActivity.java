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
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.Model;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.rocio.taskmaster.R;


import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {
    private final String TAG = "AddTaskActivity";

    Button saveButton;
    EditText taskTitleEditText;
    EditText taskBodyEditText;
    Spinner taskStateSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        saveButton = findViewById(R.id.AddTaskActivityButton);
        taskStateSpinner = findViewById(R.id.AddTaskActivityTaskStateSpinner);
        taskTitleEditText = findViewById(R.id.AddTaskActivityTaskTitle);
        taskBodyEditText = findViewById(R.id.AddTaskActivityDescription);

        setUpTaskStateSpinner();
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

//            TODO: Make a DynamoDB/GraphQL call
            Task taskToSave = Task.builder()
                    .title(taskTitleEditText.getText().toString())
                    .body(taskBodyEditText.getText().toString())
                            .dateCreated(new Temporal.DateTime(new Date(), 0))
                                    .taskState((TaskStateEnum) taskStateSpinner.getSelectedItem())
                                            .build();

            Amplify.API.mutate(
                    ModelMutation.create(taskToSave),
                    successResponse -> Log.i(TAG, "AddTaskActivity.setUpAddTaskButton(): created task successfully"),
                    failureResponse -> Log.i(TAG, "AddTaskActivity.setUpAddTaskButton(): failure response " + failureResponse)
            );
            Toast.makeText(AddTaskActivity.this, "Task saved!", Toast.LENGTH_SHORT).show();
        });
    }
}