package com.rocio.taskmaster.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.rocio.taskmaster.doas.TaskDao;
import com.rocio.taskmaster.models.Task;

@TypeConverters({TaskMasterDbClassConverters.class})
@Database(entities = {Task.class}, version = 1)  //updating version will destroy the DB
public abstract class TaskMasterDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();

}
