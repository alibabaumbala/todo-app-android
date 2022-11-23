package com.example.midterm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.taskHolder> {
    private Context context;
    private List<TodoItems> taskList;

    public TodoAdapter(Context context, List<TodoItems> taskList) {
        this.context = context;
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public taskHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_todo_sublayout,parent,false);
        return new taskHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull taskHolder holder, int position) {
        TodoItems item = taskList.get(position);
        holder.title.setText(item.getTitle());
        holder.content.setText(item.getContent());

    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    class taskHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private TextView content;

        public taskHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titletask);
            content = itemView.findViewById(R.id.contenttask);
        }
    }
}
