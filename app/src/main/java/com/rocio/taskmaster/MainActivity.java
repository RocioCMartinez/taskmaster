package com.rocio.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.rocio.taskmaster.activities.AddTask;
import com.rocio.taskmaster.activities.AllTasks;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpAddTaskFormButton();
        setUpAllTasksButton();
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
            Intent goToAddTaskIntent = new Intent(MainActivity.this, AddTask.class);
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
            Intent goToAllTasksIntent = new Intent(MainActivity.this, AllTasks.class);
            startActivity(goToAllTasksIntent);

        });
    }
}