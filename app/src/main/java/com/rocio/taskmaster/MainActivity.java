package com.rocio.taskmaster;

import static com.rocio.taskmaster.activities.UserProfileActivity.USERNAME_TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.rocio.taskmaster.activities.AddTaskActivity;
import com.rocio.taskmaster.activities.AllTasksActivity;
import com.rocio.taskmaster.activities.TaskDetailsActivity;
import com.rocio.taskmaster.activities.UserProfileActivity;
import com.rocio.taskmaster.adapters.TaskListRecyclerViewAdapter;
import com.rocio.taskmaster.models.State;
import com.rocio.taskmaster.models.Task;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String TASK_NAME_EXTRA_TAG = "taskName";
    SharedPreferences preferences;

//    Step 2-2: Make some data items
    List<Task> tasks = new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setUpUserProfileButton();
//        setUpTaskOneButton();
//        setupTaskTwoButton();
//        setupTaskThreeButton();
//   Instances must be created before RecyclerView
        createTaskInstances();
        setupTaskRecyclerView();

        setUpAddTaskFormButton();
        setUpAllTasksButton();

    }

    @Override
    protected void onResume() {
        super.onResume();

        setupUsernameTextView();
    }

    void setUpUserProfileButton(){
        ((ImageView)findViewById(R.id.MainActivityUserProfileButton)).setOnClickListener(view -> {
            Intent goToUserProfileActivityIntent = new Intent(MainActivity.this, UserProfileActivity.class);
            startActivity(goToUserProfileActivityIntent);
        });
    }

    void setupUsernameTextView(){
        String userName = preferences.getString(USERNAME_TAG, "No username");
        ((TextView)findViewById(R.id.MainActivityUserName)).setText(userName + "'s Tasks");
    }

//    void setUpTaskOneButton() {
//        ((Button)findViewById(R.id.MainActivityTaskOneBtn)).setOnClickListener(view -> {
//            String taskName = ((Button)findViewById(R.id.MainActivityTaskOneBtn)).getText().toString();
//            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetailsActivity.class);
//            goToTaskDetails.putExtra(TASK_NAME_EXTRA_TAG, taskName);
//            startActivity(goToTaskDetails);
//        });
//    }

//    void setupTaskTwoButton() {
//        ((Button)findViewById(R.id.MainActivityTaskTwoBtn)).setOnClickListener(view -> {
//            String taskName = ((Button)findViewById(R.id.MainActivityTaskTwoBtn)).getText().toString();
//            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetailsActivity.class);
//            goToTaskDetails.putExtra(TASK_NAME_EXTRA_TAG, taskName);
//            startActivity(goToTaskDetails);
//        });
//    }

//    void setupTaskThreeButton() {
//        ((Button)findViewById(R.id.MainActivityTaskThreeBtn)).setOnClickListener(view -> {
//            String taskName = ((Button)findViewById(R.id.MainActivityTaskThreeBtn)).getText().toString();
//            Intent goToTaskDetails = new Intent(MainActivity.this, TaskDetailsActivity.class);
//            goToTaskDetails.putExtra(TASK_NAME_EXTRA_TAG, taskName);
//            startActivity(goToTaskDetails);
//        });
//    }

    void setupTaskRecyclerView(){
//        Step 1-2: Grab the recyclerview
        RecyclerView taskListRecyclerView = (RecyclerView) findViewById(R.id.MainActivityTaskRecyclerView);

//        Step 1-3: set the layout manager for RecyclerView to a LinearLayoutManager
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
//        for horizontal line
//        ((LinearLayoutManager)layoutManager).setOrientation(LinearLayoutManager.HORIZONTAL);
        taskListRecyclerView.setLayoutManager(layoutManager);

//        Extra: RecyclerView Styling- Add item decoration w/ spacing
        int spaceInPixels = getResources().getDimensionPixelSize(R.dimen.task_fragment_spacing);
        taskListRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.bottom = spaceInPixels;
//                Do following code if issue displaying last fragment
//                if(parent.getChildAdapterPosition(view) == tasks.size()-1){
//                    outRect.bottom = 0;
//                }
            }
        });

//        Step1-5: Create and attach RecyclerView.Adapter to RecyclerView
//        TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter();
//        Step 2-3: Hand data items from Main Activity to our RecyclerViewAdapter
//        TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter(tasks);
//        Step 3-2: Hand in the Activity context to the adapter
        TaskListRecyclerViewAdapter adapter = new TaskListRecyclerViewAdapter(tasks, this);
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

    void createTaskInstances(){
//        Step 2-2 cont.: Fill list w/ data
        tasks.add(new Task("Meal Prep", "Cook and store containers of meals for the week", State.NEW));
        tasks.add(new Task("Laundry", "Wash, dry, fold, and store all the clothes in the hamper.", State.ASSIGNED));
        tasks.add(new Task("Replace bedding/towels", "Weekly replace bedding and towels, ensuring old ones are washed and put away.", State.COMPLETE));

    }
}