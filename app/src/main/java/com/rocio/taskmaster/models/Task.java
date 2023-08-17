package com.rocio.taskmaster.models;
// Step 2-1: Create a data class
public class Task {
    String title;

    String body;

    State taskState;


    public Task(String title, String body, State taskState) {
        this.title = title;
        this.body = body;
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
}
