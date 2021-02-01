package com.example.noteapp.usecases.taskusecase;

import android.text.TextUtils;

import com.example.noteapp.entities.Task;
import com.example.noteapp.usecases.database.TaskDao;
import com.example.noteapp.usecases.reopository.TaskRepository;

import java.util.List;

public class TaskUseCase {


    TaskRepository taskRepository;

    public TaskUseCase() {
        taskRepository = new TaskRepository();
    }

    public void insertTask(Task task){
        taskRepository.insertTask(task);
    }

    public List<Task> getNotes(){
        return taskRepository.getNotes();
    }

    public boolean validationName(String name){
        if (TextUtils.isEmpty(name)){
            return false;
        }
        else {
            return true;
        }
    }
}
