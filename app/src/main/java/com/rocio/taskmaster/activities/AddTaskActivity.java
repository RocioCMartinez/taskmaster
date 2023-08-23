package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


import com.rocio.taskmaster.MainActivity;
import com.rocio.taskmaster.R;
import com.rocio.taskmaster.models.Task;
import com.rocio.taskmaster.models.TaskStateEnum;

import java.util.Date;

public class AddTaskActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        Spinner taskStateSpinner = (Spinner) findViewById(R.id.AddTaskActivityTaskStateSpinner);

        setUpTaskStateSpinner(taskStateSpinner);
        setUpAddTaskActivityButton(taskStateSpinner);
    }


    void setUpTaskStateSpinner(Spinner taskStateSpinner) {
        taskStateSpinner.setAdapter(new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                TaskStateEnum.values()
        ));
    }

    void setUpAddTaskActivityButton(Spinner taskStateSpinner){
        // 4 steps to event listener set up
        //1: Get UI element by ID
        Button addTaskActivityButton = findViewById(R.id.AddTaskActivityButton);
        //2/3: Add an onClickListener
        addTaskActivityButton.setOnClickListener(view -> {
            //4: Define the callback method
//            ((TextView)findViewById(R.id.AddTaskActivityMessage)).setText("Submitted!");

            Task taskToSave = new Task(
                    ((EditText)findViewById(R.id.AddTaskActivityTaskTitle)).getText().toString(),
                    ((EditText) findViewById(R.id.AddTaskActivityDescription)).getText().toString(),
                    new Date(),
                    TaskStateEnum.fromString(taskStateSpinner.getSelectedItem().toString())
            );
//            TODO: Make a DynamoDB/GraphQL call
//            taskMasterDatabase.taskDao().insertATask(taskToSave);
            Toast.makeText(AddTaskActivity.this, "Task Added!", Toast.LENGTH_SHORT).show();
        });
    }
}