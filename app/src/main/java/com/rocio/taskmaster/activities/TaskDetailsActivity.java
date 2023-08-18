package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.rocio.taskmaster.MainActivity;
import com.rocio.taskmaster.R;
import com.rocio.taskmaster.database.TaskMasterDatabase;
import com.rocio.taskmaster.models.Task;

public class TaskDetailsActivity extends AppCompatActivity {

    TaskMasterDatabase taskMasterDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        setupTaskDetails();
    }

    void setupTaskDetails() {
        Intent callingIntent = getIntent();
        String taskNameString = null;
        String taskDescriptionString = null;
        if (callingIntent != null){
            taskNameString = callingIntent.getStringExtra(MainActivity.TASK_NAME_EXTRA_TAG);
            taskDescriptionString = callingIntent.getStringExtra(MainActivity.TASK_DESCRIPTION_TAG);
        }

        TextView taskNameTextView = (TextView) findViewById(R.id.TaskDetailsActivityTaskLabel);
        if (taskNameString != null){
            taskNameTextView.setText(taskNameString);
        } else {
            taskNameTextView.setText(R.string.blank_task_name);
        }

        TextView taskDescriptionTextView = (TextView) findViewById(R.id.TaskDetailsActivityTaskDescription);
        if (taskDescriptionString != null){
            taskDescriptionTextView.setText(taskDescriptionString);
        } else {
            taskDescriptionTextView.setText(R.string.no_task_description);
        }


//        Intent callingIntent = getIntent();
//        Long taskId = null;
//        if (callingIntent != null){
//            taskId = callingIntent.getLongExtra(MainActivity.TASK_ID_EXTRA_TAG, taskId);
//        }
//
//        TextView taskNameTextView = findViewById(R.id.TaskDetailsActivityTaskLabel);
//        TextView taskDescriptionTextView = findViewById(R.id.TaskDetailsActivityTaskDescription);
//
//        if (taskId != null) {
//            // Assuming you have a method to retrieve a Task by ID from your database
//            Task task = taskMasterDatabase.taskDao().findByAnId(taskId);
//
//            if (task != null) {
//                taskNameTextView.setText(task.getTitle()); // Set the task name
//
//                // Display the task description in the taskDescriptionTextView
//                taskDescriptionTextView.setText(task.getBody());
//            }
//        }

    }
}