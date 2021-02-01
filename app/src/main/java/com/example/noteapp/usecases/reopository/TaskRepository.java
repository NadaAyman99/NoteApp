package com.example.noteapp.usecases.reopository;

import com.example.noteapp.entities.Task;
import com.example.noteapp.presentation.core.TaskApplication;
import com.example.noteapp.usecases.database.TaskDao;
import com.example.noteapp.usecases.database.TaskDatabase;

import java.util.List;

public class TaskRepository {

    private TaskDatabase database;
    private TaskDao dao;

    public TaskRepository() {
        database = TaskDatabase.getDatabase(TaskApplication.getContext());
        dao = database.taskDao();
    }

    public void insertTask(Task task){
        dao.insert(task);
    }

    public List<Task> getNotes(){
        return dao.getAll();
    }
}
