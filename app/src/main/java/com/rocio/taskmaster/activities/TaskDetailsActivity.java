package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.rocio.taskmaster.MainActivity;
import com.rocio.taskmaster.R;

import java.io.File;

public class TaskDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TaskDetailsActivity";

    Intent callingIntent;
    Task currentTask;
    String s3ImageKey;
    ImageView taskImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        callingIntent = getIntent();

        taskImageView = findViewById(R.id.TaskDetailsActivityImageView);

        setupTaskImageView();
        setupTaskDetails();
    }

    void setupTaskImageView(){
        String taskId = "";
        if(callingIntent != null) {
            taskId = callingIntent.getStringExtra(MainActivity.TASK_ID_EXTRA_TAG);
        }

        if(!taskId.equals("")) {
            // query for task from dynamoDB
            Amplify.API.query(
                    ModelQuery.get(Task.class, taskId),
                    success -> {
                        Log.i(TAG, "successfully found task with id: " + success.getData().getId());
                        currentTask = success.getData();
                        populateImageView();
                    },
                    failure -> Log.i(TAG,"Failed to query task from DB: " + failure.getMessage())
            );
        }
    }

    void populateImageView() {
        // truncate folder name from tasks s3key
        if(currentTask.getTaskImageS3Key() != null) {
            int cut = currentTask.getTaskImageS3Key().lastIndexOf('/');
            if(cut != -1) {
                s3ImageKey = currentTask.getTaskImageS3Key().substring(cut + 1);
            }
        }

        if(s3ImageKey != null && !s3ImageKey.isEmpty()) {
            Amplify.Storage.downloadFile(
                    s3ImageKey,
                    new File(getApplication().getFilesDir(), s3ImageKey),
                    success -> taskImageView.setImageBitmap(BitmapFactory.decodeFile(success.getFile().getPath())),
                    failure -> Log.e(TAG, "Unable to get image from S3 for the task-S3 key: " + s3ImageKey + " error: " + failure.getMessage())
            );
        }
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


    }
}