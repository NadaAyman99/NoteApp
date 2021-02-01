package com.example.noteapp.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.noteapp.R;
import com.example.noteapp.entities.Task;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class TasksAdapter extends RecyclerView.Adapter<TasksAdapter.TaskViewHolder>{

    List<Task> taskList;
    OnItemClickListener mItemClickListener;

    public TasksAdapter(List<Task> taskList) {
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public void setList(List<Task> resList) {
        this.taskList = resList;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClicklListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task_view, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = taskList.get(position);
        holder.taskName.setText(currentTask.getTask());
        holder.description.setText(currentTask.getDesc());
        if (currentTask.isFinished()){
            holder.done.setText("Done");
        }
        else holder.done.setText("");
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class TaskViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView taskName , description,done;
        MaterialCardView cardView;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);

            taskName = itemView.findViewById(R.id.task_name);
            description = itemView.findViewById(R.id.task_desc);
            done = itemView.findViewById(R.id.done_tv);
            cardView = itemView.findViewById(R.id.card_view);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mItemClickListener.onItemClick(v, getPosition());
        }

    }

}
