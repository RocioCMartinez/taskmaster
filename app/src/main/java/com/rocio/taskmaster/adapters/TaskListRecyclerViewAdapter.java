package com.rocio.taskmaster.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.amplifyframework.datastore.generated.model.Task;
import com.rocio.taskmaster.MainActivity;
import com.rocio.taskmaster.R;
import com.rocio.taskmaster.activities.TaskDetailsActivity;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

// Step 1-4: Make a custom RecyclerViewAdapter class which extends RecyclerView.Adapter
//public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter {
//Step 3-1: Refactor Adapter class to work with our custom ViewHolder
public class TaskListRecyclerViewAdapter extends RecyclerView.Adapter<TaskListRecyclerViewAdapter.TaskListViewHolder>{
//    Step 2-3 cont.: Create a product list variable and constructor w/in the adapter
    List<Task> tasks;

    //    Step 3-2 cont.: Create a Task List variable and update constructor
    Context callingActivity;

    public TaskListRecyclerViewAdapter(List<Task> tasks, Context callingActivity){
        this.tasks = tasks;
        this.callingActivity = callingActivity;
    }


    @NonNull
    @Override
    public TaskListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        Step 1-7: Inflate the fragment (add the fragment to the viewHolder)
        View taskFragment = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_task_list, parent, false);
//        Step 1-9: Attach fragment to the ViewHolder
        return new TaskListViewHolder(taskFragment);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskListViewHolder holder, int position) {
//Step 2-4: Bind data items to Fragments inside of ViewsHolders
        TextView taskFragmentTextView = (TextView) holder.itemView.findViewById(R.id.TaskFragmentTextView);
        String dateString = formatDateString(tasks.get(position));
        String taskFragmentText = (position+1) + ". " + tasks.get(position).getTitle()
                + "\n" + dateString
                + "\n" + tasks.get(position).getTaskState();
        
        taskFragmentTextView.setText(taskFragmentText);

//        Step 3-3: Create onClickListener, make intent inside, call the intent w/ an extra to go to new activity
        View taskViewholder = holder.itemView;
        taskViewholder.setOnClickListener(view -> {
            Intent goToTaskDetailsIntent = new Intent(callingActivity, TaskDetailsActivity.class);
            goToTaskDetailsIntent.putExtra(MainActivity.TASK_NAME_EXTRA_TAG, tasks.get(position).getTitle());
//            goToTaskDetailsIntent.putExtra(MainActivity.TASK_ID_EXTRA_TAG, tasks.get(position).getId()); // new line***
            goToTaskDetailsIntent.putExtra(MainActivity.TASK_DESCRIPTION_TAG, tasks.get(position).getBody());
            callingActivity.startActivity(goToTaskDetailsIntent);
        });
    }

    @Override
    public int getItemCount() {

//        Step1-10: Set # of items you want to see
//        return 10;
        //        Step2-5: Make the size of the list dynamic based on size of the task list
        return tasks.size();
    }

    private String formatDateString(Task task){
        DateFormat dateCreatedIso8601InputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        dateCreatedIso8601InputFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        DateFormat dateCreatedOutputFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
        dateCreatedOutputFormat.setTimeZone(TimeZone.getDefault());
        String dateCreatedString = "";

        try {
            Date dateCreatedJavaDate = dateCreatedIso8601InputFormat.parse(task.getDateCreated().format());
            if (dateCreatedJavaDate != null) {
                dateCreatedString = dateCreatedOutputFormat.format(dateCreatedJavaDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateCreatedString;
    }
//Step 1-8: Make ViewHolder class to hold fragments (nested w/in TaskListRecyclerViewAdapter)
    public static class TaskListViewHolder extends RecyclerView.ViewHolder{
        public TaskListViewHolder(@NonNull View itemView){
            super(itemView);
        }
    }
}
