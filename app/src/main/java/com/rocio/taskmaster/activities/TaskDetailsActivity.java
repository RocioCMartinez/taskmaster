package com.rocio.taskmaster.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.datastore.generated.model.Task;
import com.rocio.taskmaster.MainActivity;
import com.rocio.taskmaster.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TaskDetailsActivity extends AppCompatActivity {
    private static final String TAG = "TaskDetailsActivity";
    private final MediaPlayer mp = new MediaPlayer();

    Intent callingIntent;
    Task currentTask;
    String s3ImageKey;
    ImageView taskImageView;

    TextView taskNameTextView;

    TextView taskDescriptionTextView;

    TextView latitudeTextView;

    TextView longitudeTextView;

    Button playDescriptionButton;

    String longitude;

    String latitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        callingIntent = getIntent();

        taskImageView = findViewById(R.id.TaskDetailsActivityImageView);

        taskNameTextView = findViewById(R.id.TaskDetailsActivityTaskLabel);

        taskDescriptionTextView = findViewById(R.id.TaskDetailsActivityTaskDescription);

        playDescriptionButton = findViewById(R.id.TaskDetailsActivityDescriptionButton);

        latitudeTextView = findViewById(R.id.TaskDetailsActivityLatitudeTextView);
        longitudeTextView = findViewById(R.id.TaskDetailsActivityLongitudeTextView);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        latitude = preferences.getString("currentLatitude", "N/A");
        longitude = preferences.getString("currentLongitude", "N/A");

        setupTaskImageView();
        setupTaskDetails();
        setupPlayDetailsButton();
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

        latitudeTextView.setText("Latitude: " + latitude);
        longitudeTextView.setText("Longitude: " + longitude);

    }

    void setupPlayDetailsButton(){
        playDescriptionButton.setOnClickListener(view -> {
            String taskName = taskNameTextView.getText().toString();
            String taskDescription = taskDescriptionTextView.getText().toString();
            String combinedText = taskName + ". " + taskDescription;
            Amplify.Predictions.convertTextToSpeech(
                    combinedText,
                    result -> playAudio(result.getAudioData()),
                    error -> Log.e(TAG, "Audio conversion of task description text failed", error)
            );

            Amplify.Predictions.translateText(combinedText,
                    result -> Log.i("MyAmplifyApp", result.getTranslatedText()),
                    error -> Log.e("MyAmplifyApp", "Translation failed", error)
            );
        });
    }

    private void playAudio(InputStream data) {
        File mp3File = new File(getCacheDir(), "audio.mp3");

        try(OutputStream out = new FileOutputStream(mp3File)) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;

            while((bytesRead = data.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }

            Log.i(TAG, "audio file finished reading");

            mp.reset();
            mp.setOnPreparedListener(MediaPlayer::start);
            mp.setDataSource(new FileInputStream(mp3File).getFD());
            mp.prepareAsync();

            Log.i(TAG, "Audio played successfully");
        } catch (IOException ioe) {
            Log.e(TAG, "Error writing audio file", ioe);
        }
    }
}