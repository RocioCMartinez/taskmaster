package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.rocio.taskmaster.MainActivity;
import com.rocio.taskmaster.R;

public class TaskDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        setupTaskDetailsTaskLabel();
    }

    void setupTaskDetailsTaskLabel() {
        Intent callingIntent = getIntent();
        String taskNameString = null;
        if (callingIntent != null){
            taskNameString = callingIntent.getStringExtra(MainActivity.TASK_NAME_EXTRA_TAG);
        }

        TextView taskNameTextView = (TextView) findViewById(R.id.TaskDetailsActivityTaskLabel);
        if (taskNameString != null){
            taskNameTextView.setText(taskNameString);
        }
    }
}