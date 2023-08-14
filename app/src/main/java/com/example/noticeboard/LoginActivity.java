package com.example.noticeboard;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {
    private EditText usernamefromL;
    private EditText mPasswordFromLogin;
    private Button mSigninButton;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    private Button mGotoSignupButton;
    private ProgressDialog mProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernamefromL= findViewById(R.id.username);
        mPasswordFromLogin=findViewById(R.id.password);
        mSigninButton=findViewById(R.id.loginButton);
        mGotoSignupButton=findViewById(R.id.signupButton);
        mProgress = new ProgressDialog(this);
        mAuth= FirebaseAuth.getInstance();
        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    Intent loginIntent = new Intent(LoginActivity.this,MainActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(loginIntent);
                }
            }
        };

        mSigninButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProgress.setMessage("Signing in...");
                mProgress.show();
                startSignin();
            }
        });

    }
    @Override
    protected void onStart() {

        super.onStart();
        mAuth.addAuthStateListener(mAuthListner);
    }
    private void startSignin(){
        String userName = usernamefromL.getText().toString();
        String passWord = mPasswordFromLogin.getText().toString();
        if(TextUtils.isEmpty(userName)|| TextUtils.isEmpty(passWord)){
            mProgress.dismiss();
            Toast.makeText(this, "Email and Password Fields cannot be empty", Toast.LENGTH_LONG).show();
        }
        else if(TextUtils.getTrimmedLength(passWord)<8){
            mProgress.dismiss();
            mPasswordFromLogin.setError("Password should not be less than 8 characters");
        }
        else{
            mAuth.signInWithEmailAndPassword(userName,passWord).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()){
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Problem Signing in", Toast.LENGTH_LONG).show();
                    }
                    else{
                        mProgress.dismiss();
                    }
                }
            });
        }
    }
}
