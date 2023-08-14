package com.example.noticeboard;

import android.os.Bundle;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.PatternMatcher;
import android.support.*;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.util.NumberUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignUpActivity extends AppCompatActivity {

    private EditText mErpIdFromSignup;
    private EditText mEmailFromSignup;
    private EditText mPasswordFromSignup;
    private Button mSignupButton;
    private ProgressDialog mProgress;
    private FirebaseAuth mAuth;
    private DatabaseReference mDbRefBeta;
    private DatabaseReference mDatabaseRef;
    private String canSignUp;
    private String emailInFb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        canSignUp = "0";
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mProgress = new ProgressDialog(this);
        mErpIdFromSignup = findViewById(R.id.username1);
        mEmailFromSignup= findViewById(R.id.mail1);
        mSignupButton = findViewById(R.id.signupButton1);
        mSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String erpId = mErpIdFromSignup.getText().toString().trim();
                final String email = mEmailFromSignup.getText().toString().trim();
                final String password = mPasswordFromSignup.getText().toString().trim();
                mProgress.setMessage("Creating Account...");
                mProgress.show();
                mDatabaseRef.child("Can-Sign-Up").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.hasChild(erpId.toUpperCase())||snapshot.hasChild(erpId.toLowerCase())){
                            canSignUp="1";
                        }
                        startSignUp(erpId,email,password);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(SignUpActivity.this, "Cannot Connect!", Toast.LENGTH_SHORT).show();
                    }
                });
                Intent gotoLoginIntent = new Intent(SignUpActivity.this,LoginActivity.class);
                gotoLoginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(gotoLoginIntent);
            }


        });

    }
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    public static boolean validate(String emailStr){
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
        return matcher.find();
    }
    public void startSignUp(final String erpId,final String email, final String password){
        String erpSubString="";

        if ( TextUtils.isEmpty(erpId) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
            mProgress.dismiss();
            Toast.makeText(getApplicationContext(), "Please fill all the details before Signing Up", Toast.LENGTH_LONG).show();
        }

        else if (TextUtils.getTrimmedLength(erpId) <= 4){
            mProgress.dismiss();
            mErpIdFromSignup.setError(" Id Format is not valid! more than 4 characters");
        }


        else if (!validate(email)){
            mProgress.dismiss();
            mEmailFromSignup.setError("Not a valid email address!");
        }

        else if (TextUtils.getTrimmedLength(password) < 8 ){
            mProgress.dismiss();
            mPasswordFromSignup.setError("Password should not be less than 8 characters!");
        }


        else if (!canSignUp.equals("1")){
            mProgress.dismiss();
            mErpIdFromSignup.setError("This Erp Id is not registered to the email address you have provided!");
        }

        else{
            canSignUp="0";
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        String user_id = mAuth.getCurrentUser().getUid();
                        DatabaseReference current_user_db = mDatabaseRef.child("Users").child(user_id);
                        current_user_db.child("erp_id").setValue(erpId);
                        mDatabaseRef.child("Users").child(user_id).setValue(mAuth.getCurrentUser().getEmail());
                        mProgress.setMessage("Signed Up");
                        mProgress.dismiss();
                    }
                    else{
                        Toast.makeText(SignUpActivity.this, "Error: Couldn't sign up", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }

    }

}
