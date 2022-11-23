package com.example.midterm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    private EditText emailrg;
    private EditText passwordrg;
    private EditText repasswordrg;
    private Button confirmrg;
    private FirebaseAuth fAuth;
    private NetworkConnect networkConnect;
    private FloatingActionButton floatingActionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        emailrg = findViewById(R.id.emailrg);
        passwordrg = findViewById(R.id.passwordrg);
        repasswordrg = findViewById(R.id.repasswordrg);
        confirmrg = findViewById(R.id.confirmrg);
        floatingActionButton = findViewById(R.id.backtologin);
        networkConnect = new NetworkConnect(this);

        fAuth = FirebaseAuth.getInstance();
        final LoadingProgress loadingProgress = new LoadingProgress(this);

        confirmrg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(networkConnect.isNetworkAvailable()){
                    String email = emailrg.getText().toString().trim();
                    String password = passwordrg.getText().toString().trim();
                    String repassword = repasswordrg.getText().toString().trim();

                    if (TextUtils.isEmpty(email)){
                        emailrg.setError("Email không phù hợp");
                        return;
                    }else if (TextUtils.isEmpty(password)){
                        passwordrg.setError("Mật khẩu không phù hợp");
                        return;
                    }else if (password.length() < 6){
                        passwordrg.setError("Mật khẩu phải có ít nhất 6 ký tự");
                        return;
                    }
                    else if (TextUtils.isEmpty(repassword)){
                        repasswordrg.setError("Nhập lại mật khẩu không phù hợp");
                        return;
                    }else if (!password.equals(repassword)){
                        repasswordrg.setError("Mật khẩu không khớp");
                        return;
                    }else{
                        loadingProgress.startLoading();
                        fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    Intent intent = new Intent (getApplicationContext(),Todo.class);
                                    startActivity(intent);
                                    finish();
                                }else{
                                    Toast toast = Toast.makeText(getApplicationContext(),"Đăng ký không thành công. Vui lòng thử lại", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                loadingProgress.dismissDialog();

                            }
                        });
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Không có kết nối mạng. Vui lòng thử lại sau",Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignIn.class));
                finish();
            }
        });
    }

}