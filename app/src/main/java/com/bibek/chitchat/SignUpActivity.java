package com.bibek.chitchat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bibek.chitchat.databinding.ActivitySignUpBinding;
import com.bibek.chitchat.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity{
            private FirebaseAuth mAuth;
            FirebaseDatabase database;
            ProgressDialog progressDialog;
            ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
         //linking the xml file with java using binding method
        binding=ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();  //hide the action bar

        //initialization the auth & database
        mAuth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
            //creating a dialog box for signup
    progressDialog=new ProgressDialog(SignUpActivity.this);
    progressDialog.setTitle("Creating Account");
    progressDialog.setMessage("we are creating your account.");

    binding.btnSignUp.setOnClickListener(view -> {
        if(!binding.txtUserName.getText().toString().isEmpty() && !binding.txtEmail.getText().toString().isEmpty() && !binding.txtPassword.getText().toString().isEmpty()){
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(binding.txtEmail.getText().toString(),binding.txtPassword.getText().toString()) //creating user
                    .addOnCompleteListener(task -> {
                   progressDialog.dismiss();
                   if(task.isSuccessful()){
                       Toast.makeText(SignUpActivity.this, "Sign Up successful", Toast.LENGTH_SHORT).show();
                       //fetch these 3 pics of data and store in user obj
                       Users user=new Users(binding.txtUserName.getText().toString(),binding.txtEmail.getText().toString(),binding.txtPassword.getText().toString());
                       //Extract the user uid from auth
                       String id= Objects.requireNonNull(task.getResult().getUser()).getUid();
                       //creating a Users table and store the user value there using id
                       database.getReference().child("Users").child(id).setValue(user);

                       Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
                       startActivity(intent);
                       finish();

                   }else {
                       Toast.makeText(SignUpActivity.this, Objects.requireNonNull(task.getException()).toString(), Toast.LENGTH_SHORT).show();
                   }
                    });
        }else{
            Toast.makeText(SignUpActivity.this, "Enter Credential", Toast.LENGTH_SHORT).show();
        }
    });
        binding.txtSignIn.setOnClickListener(view -> {
            Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
            startActivity(intent);
            finish();
        });

    }
    //this is outside OnCreate


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent=new Intent(SignUpActivity.this,SignInActivity.class);
        startActivity(intent);
        finish();
    }
}