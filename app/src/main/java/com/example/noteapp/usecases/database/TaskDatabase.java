package com.example.noteapp.usecases.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.noteapp.entities.Task;

@Database(entities = {Task.class}, version = 1,exportSchema = false)
public abstract class TaskDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
    private static volatile TaskDatabase INSTANCE;

    public static TaskDatabase getDatabase(Context context) {
        if (INSTANCE == null) {
            synchronized (TaskDatabase.class) {
                if (INSTANCE == null) {
                        INSTANCE = Room.databaseBuilder(context,
                        TaskDatabase.class, "task_database")
                        .build();
                }
            }
        }
        return INSTANCE;
    }
}
