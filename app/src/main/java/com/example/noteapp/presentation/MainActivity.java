package com.example.noteapp.presentation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.noteapp.R;
import com.example.noteapp.usecases.database.TaskDao;
import com.example.noteapp.usecases.database.TaskDatabase;
import com.example.noteapp.entities.Task;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    ImageButton addNoteButton;
    List<Task> list = new ArrayList<>();
    RecyclerView recyclerView;
    TasksAdapter taskAdapter;

    //TaskViewModel taskViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);

        addNoteButton = findViewById(R.id.add_new_note);

        recyclerView = findViewById(R.id.recycler_view);
        getData();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        taskAdapter = new TasksAdapter(list);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        dividerItemDecoration.setDrawable(getResources().getDrawable(R.drawable.recyclerview_divid));
        recyclerView.addItemDecoration(dividerItemDecoration);

        recyclerView.setAdapter(taskAdapter);

        addNoteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNote();
            }
        });

        //onSwiped to delete a Note
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                TaskDatabase database = TaskDatabase.getDatabase(getApplicationContext());
                final TaskDao dao = database.taskDao();
                Single.fromCallable(() -> {
                    dao.delete(list.get(position));
                    return true;
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(value -> {getData();
                                    Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
                                }
                                , error -> Log.e("error", error.getMessage()));
            }
        }).attachToRecyclerView(recyclerView);

        //click on note to update the note
        taskAdapter.setOnItemClicklListener(new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlerDialog);
                view = LayoutInflater.from(MainActivity.this)
                        .inflate(R.layout.dialog_update, findViewById(R.id.dialog_layout_container));
                builder.setView(view);
                Task task;
                task = list.get(position);
                EditText noteTitle = view.findViewById(R.id.note_title);
                EditText noteDesc = view.findViewById(R.id.note_desc);
                EditText noteFinishBy = view.findViewById(R.id.note_finish_by);
                noteTitle.setText(task.getTask());
                noteDesc.setText(task.getDesc());
                noteFinishBy.setText(task.getFinishBy());

                final AlertDialog alertDialog = builder.create();
                view.findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TaskDatabase database = TaskDatabase.getDatabase(getApplicationContext());
                        final TaskDao dao = database.taskDao();
                        task.setTask(noteTitle.getText().toString());
                        task.setDesc(noteDesc.getText().toString());
                        task.setFinishBy(noteFinishBy.getText().toString());
                        Single.fromCallable(() -> {
                            dao.update(list.get(position));
                            return true;
                        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(value -> {getData();
                                            Toast.makeText(MainActivity.this, "Note Updated", Toast.LENGTH_SHORT).show();
                                        }
                                        , error -> Log.e("error", error.getMessage()));
                    }
                });
                view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                }
                alertDialog.show();
            }
        });

    }

    private void addNote() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.AlerDialog);
        View view = LayoutInflater.from(MainActivity.this)
                .inflate(R.layout.dialog, findViewById(R.id.dialog_layout_container));
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        view.findViewById(R.id.add_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkDone = view.findViewById(R.id.check_finish);
                EditText noteTitle = view.findViewById(R.id.note_title);
                EditText noteDesc = view.findViewById(R.id.note_desc);
                EditText noteFinishBy = view.findViewById(R.id.note_finish_by);
                String sName = noteTitle.getText().toString().trim();
                String sDesc = noteDesc.getText().toString().trim();
                String sFinishBy = noteFinishBy.getText().toString().trim();
                if (TextUtils.isEmpty(sName)) {
                    noteTitle.setError("Please Enter Note name");
                    return;
                }
                Task task = new Task();
                task.setTask(sName);
                task.setDesc(sDesc);
                task.setFinishBy(sFinishBy);
                checkDone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (checkDone.isChecked()) {
                            task.setFinished(true);
                        } else {
                            task.setFinished(false);
                        }
                    }
                });

                TaskDatabase database = TaskDatabase.getDatabase(getApplicationContext());
                TaskDao dao = database.taskDao();
                Single.fromCallable(() -> {
                    dao.insert(task);
                    return true;
                }).
                        subscribeOn(Schedulers.io()).
                        observeOn(AndroidSchedulers.mainThread()).
                        subscribe(value ->
                                {
                                    getData();
//                                    taskAdapter.setList(list);
                                    Toast.makeText(MainActivity.this, "Note added", Toast.LENGTH_LONG).show();
//                                    taskAdapter.notifyDataSetChanged();
                                }
                                , error -> Log.e("error", error.getMessage()));
            }
        });
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();

        //taskViewModel.insertNote(note);
    }

    private void getData() {
        TaskDatabase database = TaskDatabase.getDatabase(getApplicationContext());
        final TaskDao dao = database.taskDao();
        Single.fromCallable(() -> dao.getAll()).
                subscribeOn(Schedulers.io()).
                observeOn(AndroidSchedulers.mainThread())
                .subscribe(new DisposableSingleObserver<List<Task>>() {
                    @Override
                    public void onSuccess(List<Task> notes) {
                        list = notes;
                        taskAdapter.setList(list);
                        taskAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onError(Throwable e) {
                        Toast.makeText(MainActivity.this, "Empty data", Toast.LENGTH_LONG).show();

                    }
                });
    }

}





