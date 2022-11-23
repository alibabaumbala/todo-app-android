package com.example.midterm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ModifyTask extends AppCompatActivity {
    private EditText editTitle;
    private EditText editContent;
    private Button delete;
    private Button update;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_task);

        Intent intent = getIntent();
        editTitle = findViewById(R.id.edittitle);
        editContent = findViewById(R.id.editcontent);
        delete = findViewById(R.id.deletetask);
        update = findViewById(R.id.updatetask);

        editTitle.setText(intent.getStringExtra("Title"));
        editContent.setText(intent.getStringExtra("Content"));

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ModifyTask.this);
                builder.setTitle("Thông báo").setMessage("Bạn có chắc muốn xóa?");
                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("remove_id",intent.getStringExtra("Id"));
                        resultIntent.putExtra("remove_position",intent.getIntExtra("Position",0));
                        setResult(1, resultIntent);
                        finish();

                    }
                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.setCancelable(true);

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTitle.getText().toString().trim())){
                    editTitle.setError("Tiêu đề không hợp lệ");
                    return;
                }else if (TextUtils.isEmpty(editContent.getText().toString().trim())){
                    editContent.setError("Nội dung không hợp lệ");
                    return;
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(ModifyTask.this);
                    builder.setTitle("Thông báo").setMessage("Bạn có chắc muốn chỉnh sửa?");
                    builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra("update_title",editTitle.getText().toString());
                            resultIntent.putExtra("update_content",editContent.getText().toString());
                            resultIntent.putExtra("update_id",intent.getStringExtra("Id"));
                            resultIntent.putExtra("update_position",intent.getIntExtra("Position",0));
                            setResult(2, resultIntent);
                            finish();

                        }
                    });
                    builder.setNegativeButton("Hủy",null);
                    builder.setCancelable(true);

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }
}