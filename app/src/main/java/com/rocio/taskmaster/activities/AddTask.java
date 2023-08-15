package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;


import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import com.rocio.taskmaster.R;

public class AddTask extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        setUpAddTaskActivityButton();
    }
    void setUpAddTaskActivityButton(){
        // 4 steps to event listener set up
        //1: Get UI element by ID
        Button addTaskActivityButton = findViewById(R.id.AddTaskActivityButton);
        //2/3: Add an onClickListener
        addTaskActivityButton.setOnClickListener(view -> {
            //4: Define the callback method
            ((TextView)findViewById(R.id.AddTaskActivityMessage)).setText("Submitted!");

        });
    }
}