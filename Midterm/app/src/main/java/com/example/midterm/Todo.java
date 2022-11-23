package com.example.midterm;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Todo extends AppCompatActivity {

    private RecyclerView rv;
    private List<TodoItems> taskList;
    private FloatingActionButton floatingActionButton;
    private FirebaseAuth fAuth;
    private FirebaseUser fUser;
    private String userID;
    private DatabaseReference reference;
    private Activity activity;
    private TodoAdapter adapter;
    private NetworkConnect networkConnect;
    private MyRoomDatabase db;
    private TodoDAO dao;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        rv = findViewById(R.id.rv);
        floatingActionButton = findViewById(R.id.addtask);
        fAuth = FirebaseAuth.getInstance();
        fUser = fAuth.getCurrentUser();
        userID = fUser.getUid();
        reference = FirebaseDatabase.getInstance("https://midterm-54d06-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference().child("tasks").child(userID);
        activity = this;
        networkConnect = new NetworkConnect(this);
        toolbar = findViewById(R.id.bar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        taskList = new ArrayList<>();
        db = MyRoomDatabase.getInstance(this);
        dao = db.todoDAO();


        if (networkConnect.isNetworkAvailable()){
            loadData();
        }else{
            taskList = dao.getAll(userID);
        }

        adapter = new TodoAdapter(this,taskList);

        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        rv.setAdapter(adapter);

        rv.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), rv ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        if (networkConnect.isNetworkAvailable()){
                            Intent intent = new Intent(Todo.this,ModifyTask.class);
                            intent.putExtra("Title",taskList.get(position).getTitle());
                            intent.putExtra("Content",taskList.get(position).getContent());
                            intent.putExtra("Id",taskList.get(position).getId());
                            intent.putExtra("Position",position);
                            startActivityForResult(intent, 0);
                        }else{
                            Toast toast = Toast.makeText(getApplicationContext(),"Vui lòng kết nối mạng",Toast.LENGTH_SHORT);
                            toast.show();
                        }


                    }

                    @Override public void onLongItemClick(View view, int position) {

                    }
                })
        );

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (networkConnect.isNetworkAvailable())
                {
                    addTask();
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Vui lòng kết nối mạng",Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });


    }
    public void addTask(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = LayoutInflater.from(this);

        View view = inflater.inflate(R.layout.activity_add_task,null);
        builder.setView(view);

        final AlertDialog dialog = builder.create();
        dialog.setCancelable(false);

        final EditText title = view.findViewById(R.id.newname);
        final EditText content = view.findViewById(R.id.newcontent);
        Button cancel = view.findViewById(R.id.newcancel);
        Button add = view.findViewById(R.id.newconfirm);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String getTitle = title.getText().toString().trim();
                String getContent = content.getText().toString().trim();
                String id = reference.push().getKey();
                String date = DateFormat.getDateInstance().format(new Date());
                final LoadingProgress loadingProgress = new LoadingProgress(activity);

                if (TextUtils.isEmpty(getTitle)){
                    title.setError("Tiêu đề không phù hợp");
                    return;
                }else if (TextUtils.isEmpty(getContent)){
                    content.setError("Nội dung không phù hợp");
                    return;
                }else{
                     loadingProgress.startLoading();
                     TodoItems task = new TodoItems(getTitle,getContent,id,date,userID);

                     reference.child(id).setValue(task).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast toast = Toast.makeText(Todo.this,"Thêm thành công",Toast.LENGTH_SHORT);
                                toast.show();

                            }else{
                                String error = task.getException().toString();
                                Toast toast = Toast.makeText(Todo.this,error,Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            loadingProgress.dismissDialog();
                        }
                    });
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }
    public void loadData(){
        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TodoItems item = snapshot.getValue(TodoItems.class);
                taskList.add(item);
                adapter.notifyDataSetChanged();

                if(dao.get(item.getId()) == false){
                    dao.add(item);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                TodoItems item = snapshot.getValue(TodoItems.class);
                dao.update(item.getTitle(),item.getContent(),item.getId());

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                TodoItems item = snapshot.getValue(TodoItems.class);
                dao.delete(item.getId());
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 1){
            reference.child(data.getStringExtra("remove_id")).removeValue();
            taskList.remove(data.getIntExtra("remove_position",0));
            Toast toast = Toast.makeText(getApplicationContext(),"Xóa thành công",Toast.LENGTH_SHORT);
            toast.show();
            adapter.notifyItemRemoved(data.getIntExtra("remove_position",0));
        }
        if (resultCode == 2){
            TodoItems item = taskList.get(data.getIntExtra("update_position",0));
            item.setTitle(data.getStringExtra("update_title"));
            item.setContent(data.getStringExtra("update_content"));
            reference.child(data.getStringExtra("update_id")).setValue(item);
            Toast toast = Toast.makeText(getApplicationContext(),"Chỉnh sửa thành công",Toast.LENGTH_SHORT);
            toast.show();
            adapter.notifyItemChanged(data.getIntExtra("update_position",0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.todo_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.logout:
                AlertDialog.Builder builder = new AlertDialog.Builder(Todo.this);
                if (networkConnect.isNetworkAvailable()){
                    builder.setTitle("Thông báo").setMessage("Bạn có chắc muốn đăng xuất?");
                }else{
                    builder.setTitle("Thông báo").setMessage("Bạn đang không kết nối mạng. Nếu đăng xuất lúc này bạn cần phải kết nối mạng để đăng nhập lại");
                }
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fAuth.signOut();
                        Intent intent = new Intent(Todo.this,SignIn.class);
                        startActivity(intent);
                        finish();
                    }
                });
                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.setCancelable(true);
                AlertDialog alertDialog = builder.create();
                alertDialog.show();

        }
        return super.onOptionsItemSelected(item);
    }
}