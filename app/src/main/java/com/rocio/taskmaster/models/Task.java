package com.rocio.taskmaster.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;
@Entity
// Step 2-1: Create a data class
public class Task {
    @PrimaryKey(autoGenerate = true)
            public Long id;
    String title;

    String body;

    java.util.Date dateCreated;

    TaskStateEnum taskState;

    @Ignore
    public Task() {
    }

    public Task(String title, String body, java.util.Date dateCreated, TaskStateEnum taskState) {
        this.title = title;
        this.body = body;
        this.dateCreated = dateCreated;
        this.taskState = taskState;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public TaskStateEnum getTaskState() {
        return taskState;
    }

    public void setTaskState(TaskStateEnum taskState) {
        this.taskState = taskState;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
