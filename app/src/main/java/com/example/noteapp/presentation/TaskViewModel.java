package com.example.noteapp.presentation;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.noteapp.entities.Task;
import com.example.noteapp.usecases.taskusecase.TaskUseCase;

import java.util.concurrent.Callable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class TaskViewModel extends ViewModel {

    public MutableLiveData<Boolean> isInserted;
    TaskUseCase taskUseCase;

    public TaskViewModel() {
        isInserted = new MutableLiveData<>();
        taskUseCase = new TaskUseCase();
    }
    public void insertNote(Task note){

        Single.fromCallable((Callable<Object>) () -> {
            taskUseCase.insertTask(note);
            return true;
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).
                subscribe(value -> {
                    Log.e("success", "success");
                    isInserted.postValue(true);
                }, error -> {
                    Log.e("error", error.getMessage());
                    isInserted.postValue(false);
                });

    }

}
