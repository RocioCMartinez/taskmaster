package com.rocio.taskmaster;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import com.rocio.taskmaster.activities.AddTaskActivity;
import com.rocio.taskmaster.activities.AllTasksActivity;
import com.rocio.taskmaster.activities.UserProfileActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUserProfileButton();
        setUpAddTaskFormButton();
        setUpAllTasksButton();
    }

    void setUpUserProfileButton(){
        ((ImageView)findViewById(R.id.MainActivityUserProfileButton)).setOnClickListener(view -> {
            Intent goToUserProfileActivityIntent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(goToUserProfileActivityIntent);
        });
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
}