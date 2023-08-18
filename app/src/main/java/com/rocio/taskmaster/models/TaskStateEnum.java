package com.rocio.taskmaster.models;

public enum TaskStateEnum {
    NEW("New"),
    ASSIGNED("Assigned"),

    IN_PROGRESS("In Progress"),
    COMPLETE("Complete");

    public String text;

    TaskStateEnum(String text){
        this.text = text;
    }

    public static TaskStateEnum fromString(String text) {
        for (TaskStateEnum tse : TaskStateEnum.values()) {
            if (tse.text.equalsIgnoreCase(text)) {
                return tse;
            }
        }
        return null;
    }
}
