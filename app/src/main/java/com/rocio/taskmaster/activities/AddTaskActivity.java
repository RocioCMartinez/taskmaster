package com.rocio.taskmaster.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;


import com.amplifyframework.api.graphql.model.ModelMutation;
import com.amplifyframework.api.graphql.model.ModelQuery;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.core.model.temporal.Temporal;
import com.amplifyframework.datastore.generated.model.Task;
import com.amplifyframework.datastore.generated.model.TaskStateEnum;
import com.amplifyframework.datastore.generated.model.Team;
import com.rocio.taskmaster.R;


import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class AddTaskActivity extends AppCompatActivity {
    private final String TAG = "AddTaskActivity";
    private String s3ImageKey = ""; // holds the image S3 key if one currently exists in this activity, or the empty String if there is no image picked in this activity currently


    ActivityResultLauncher<Intent> activityResultLauncher;

    CompletableFuture<List<Team>> teamsFuture = null;

    Button saveButton;
    EditText taskTitleEditText;
    EditText taskBodyEditText;
    Spinner taskStateSpinner;

    ImageView taskImageView;

    Spinner taskTeamSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        activityResultLauncher = getImagePickingActivityResultLauncher();
        teamsFuture = new CompletableFuture<>();

        taskImageView = findViewById(R.id.AddTaskActivityTaskImageView);
        saveButton = findViewById(R.id.AddTaskActivityButton);
        taskStateSpinner = findViewById(R.id.AddTaskActivityTaskStateSpinner);
        taskTitleEditText = findViewById(R.id.AddTaskActivityTaskTitle);
        taskBodyEditText = findViewById(R.id.AddTaskActivityDescription);
        taskTeamSpinner = findViewById(R.id.AddTaskTeamSpinner);

        setupTaskImageView();
        setUpTaskStateSpinner();
        setUpTaskTeamSpinner();
        setUpAddTaskButton();
    }

    void setupTaskImageView(){
        taskImageView.setOnClickListener(view -> {
            launchImageSelectionIntent();
        });
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
            List<Team> teams = null;
            try {
                teams = teamsFuture.get();
            } catch (InterruptedException ie) {
                Log.e(TAG, "InterruptedException while getting teams");
                Thread.currentThread().interrupt();
            } catch (ExecutionException ee) {
                Log.e(TAG, "ExecutionException while getting teams");
            }
            Object selectedTeamObject = taskTeamSpinner.getSelectedItem();
            if (selectedTeamObject != null) {
                String selectedTeamString = selectedTeamObject.toString();

                assert teams != null;
            Team selectedTeam = teams.stream().filter(t -> t.getTeamName().equals(selectedTeamString)).findAny().orElseThrow(RuntimeException::new);

//            Part4: Update task to include an S3 image key
            Task taskToSave = Task.builder()
                    .title(taskTitleEditText.getText().toString())
                    .body(taskBodyEditText.getText().toString())
                    .dateCreated(new Temporal.DateTime(new Date(), 0))
                    .taskState((TaskStateEnum) taskStateSpinner.getSelectedItem())
                    .teamP(selectedTeam)
                    .taskImageS3Key(s3ImageKey)
                    .build();

            Amplify.API.mutate(
                    ModelMutation.create(taskToSave),
                    successResponse -> Log.i(TAG, "AddTaskActivity.setUpAddTaskButton(): created task successfully"),
                    failureResponse -> Log.i(TAG, "AddTaskActivity.setUpAddTaskButton(): failure response " + failureResponse)
            );
            } else {
                Log.e(TAG, "No team selected in the spinner");
                // Handle the case where no team is selected
            }
            Toast.makeText(AddTaskActivity.this, "Task saved!", Toast.LENGTH_SHORT).show();
        });
    }

    void setUpTaskTeamSpinner(){
        Amplify.API.query(
                ModelQuery.list(Team.class),
                success -> {
                    Log.i(TAG, "Read teams successfully");
                    ArrayList<String> teamNames = new ArrayList<>();
                    ArrayList<Team> teams = new ArrayList<>();
                    for(Team team : success.getData()) {
                        teams.add(team);
                        teamNames.add(team.getTeamName());
                    }
                    teamsFuture.complete(teams);

                    runOnUiThread(() -> taskTeamSpinner.setAdapter(new ArrayAdapter<>(
                            this,
                            android.R.layout.simple_spinner_item,
                            teamNames
                    )));
                },
                failure -> {
                    teamsFuture.complete(null);
                    Log.i(TAG, "Failed to read teams");
                }
        );
    }

    void launchImageSelectionIntent() {
        // Part 1: Launch Android's activity to pick an image
        Intent imageFilePickingIntent = new Intent(Intent.ACTION_GET_CONTENT); // one of several file picking activities built into Android
        imageFilePickingIntent.setType("*/*"); // only allow one kind or category of file; if you don't have this, you get a very cryptic error about "No activity found to handle Intent"
        imageFilePickingIntent.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/jpeg", "image/png"}); // only pick JPEG and PNG images

        // Launch Android's built-in file picking activity using our newly created ActivityResultLauncher below
        activityResultLauncher.launch(imageFilePickingIntent);
    }

    ActivityResultLauncher<Intent> getImagePickingActivityResultLauncher() {
        ActivityResultLauncher<Intent> imagePickingActivityResultLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
                        new ActivityResultCallback<ActivityResult>(){
                            // Part 2: Android calls your callback with the picked image
                            @Override
                            public void onActivityResult(ActivityResult result) {
                                Uri pickedImageFileUri = result.getData().getData();
                                try {
                                    InputStream pickedImageInputStream = getContentResolver().openInputStream(pickedImageFileUri);
                                    String pickedImageFilename = getFileNameFromUri(pickedImageFileUri); // nice way of extracting filename from a Uri
                                    Log.i(TAG, "Successful input stream from file on phone! Filename is: " + pickedImageFilename);
                                    uploadInputStreamToS3(pickedImageInputStream, pickedImageFilename, pickedImageFileUri);
                                } catch (FileNotFoundException fnfe) {
                                    Log.e(TAG, "Failed to get file from file picker: " + fnfe.getMessage(), fnfe);
                                }
                            }
                        }
                );

        return imagePickingActivityResultLauncher;
    }

    void uploadInputStreamToS3(InputStream pickedImageInputStream, String pickedImageFilename, Uri pickedImageFileUri) {
        // Part 3: Use InputStream to upload file to S3

        Amplify.Storage.uploadInputStream(
                pickedImageFilename, // S3Key
                pickedImageInputStream,
                success -> {
                    Log.i(TAG, "Succeeded in uploading file to S3! Key: " + success.getKey());
                    s3ImageKey = success.getKey(); // non-empty s3ImageKey indicates an image was picked in this activity
                    // TODO: update our saveProduct to include the S3 key

                    // grab a new input stream since we used the original for uploading to S3
                    InputStream pickedImageInputStreamCopy = null; // need to make a copy because InputStreams cannot be reused!
                    try {
                        pickedImageInputStreamCopy = getContentResolver().openInputStream(pickedImageFileUri);
                    } catch (FileNotFoundException fnfe) {
                        Log.e(TAG, "Could not get file stream from URI! " + fnfe.getMessage(), fnfe);
                    }

                    // set the imageView with the selected image
                    taskImageView.setImageBitmap(BitmapFactory.decodeStream(pickedImageInputStreamCopy));
                },
                failure -> {
                    Log.e(TAG, "Failure in uploading file to S3 with filename: " + pickedImageFilename + " with error: " + failure.getMessage());
                }
        );
    }

    // Taken from https://stackoverflow.com/a/25005243/16889809
    @SuppressLint("Range")
    public String getFileNameFromUri(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

}