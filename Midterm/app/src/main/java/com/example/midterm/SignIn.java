package com.example.midterm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignIn extends AppCompatActivity {

    private EditText emailin;
    private EditText passwordin;
    private Button confirmin;
    private TextView goregister;
    private FirebaseAuth fAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private NetworkConnect networkConnect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        emailin = findViewById(R.id.emailin);
        passwordin = findViewById(R.id.passwordin);
        confirmin = findViewById(R.id.confirmin);
        goregister = findViewById(R.id.goregister);
        networkConnect = new NetworkConnect(this);


        fAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = fAuth.getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(SignIn.this,Todo.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        final LoadingProgress loadingProgress = new LoadingProgress(this);

        confirmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (networkConnect.isNetworkAvailable()){
                    String email = emailin.getText().toString().trim();
                    String password = passwordin.getText().toString().trim();

                    if (TextUtils.isEmpty(email)){
                        emailin.setError("Email không phù hợp");
                        return;
                    }else if (TextUtils.isEmpty(password)){
                        passwordin.setError("Password không phù hợp");
                        return;
                    }else{
                        loadingProgress.startLoading();
                        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    startActivity(new Intent(getApplicationContext(),Todo.class));
                                    finish();
                                }else
                                {
                                    Toast toast = Toast.makeText(getApplicationContext(),"Đăng nhập không thành công. Vui lòng thử lại", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                                loadingProgress.dismissDialog();
                            }
                        });
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(),"Không có kết nối mạng. Vui lòng thử lại sau", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        goregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),SignUp.class));
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        fAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        fAuth.removeAuthStateListener(authStateListener);
    }
}