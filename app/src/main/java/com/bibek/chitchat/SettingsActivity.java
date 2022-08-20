package com.bibek.chitchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.bibek.chitchat.databinding.ActivitySettingsBinding;
import com.bibek.chitchat.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        storage=FirebaseStorage.getInstance();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        binding.backArrow.setOnClickListener(view -> {
            Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        });

        //fetch the image and then show in the ImageView
        database.getReference().child("Users").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users=snapshot.getValue(Users.class);
                //in this line we set the image into the imageView fetching from firebase
                Picasso.get().load(Objects.requireNonNull(users).getProfilePic()).placeholder(R.drawable.avatar).into(binding.profileImage);
                //this line are responsible for Username and Status setting
                binding.etUsername.setText(users.getUserName());
                binding.etStatus.setText(users.getStatus()); //fetching the data using Users Class
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //store the username, status by clicking save button
        binding.saveButton.setOnClickListener(view -> {
            if (!binding.etStatus.getText().toString().equals("") && !binding.etUsername.getText().toString().equals("")) {
                //fetching the value from users
                String username = binding.etUsername.getText().toString();
                String status = binding.etStatus.getText().toString();
                //creating a key pair Obj of HashMap type
                HashMap<String, Object> obj = new HashMap<>();
                obj.put("userName", username);
                obj.put("status", status);

                //store these into the firebase
                database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).updateChildren(obj);
                Toast.makeText(SettingsActivity.this, "Name and Status Updated.", Toast.LENGTH_SHORT).show();
            }
        });
        //whenEver user click on plus button which will open our gallery
        binding.plus.setOnClickListener(view -> {
            Intent intent=new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,25);
        });

        binding.txtPrivacyPolicy.setOnClickListener(view -> Toast.makeText(getApplicationContext(), "It's under Develop.", Toast.LENGTH_SHORT).show());
        binding.txtAboutUs.setOnClickListener(view -> Toast.makeText(SettingsActivity.this, "It's under Develop", Toast.LENGTH_SHORT).show());
        binding.txtInviteFriend.setOnClickListener(view -> Toast.makeText(SettingsActivity.this, "It's under Develop", Toast.LENGTH_SHORT).show());
        binding.txtNotification.setOnClickListener(view -> Toast.makeText(SettingsActivity.this, "It's under Develop", Toast.LENGTH_SHORT).show());
        binding.txtHelp.setOnClickListener(view -> Toast.makeText(SettingsActivity.this, "It's under Develop", Toast.LENGTH_SHORT).show());

    }
    //this is outside OnCreate method


    @Override  //this for pressing back button
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent=new Intent(SettingsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    //here we have store the data and fetch from database
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(Objects.requireNonNull(data).getData()!=null){
            Uri sFile=data.getData();
            binding.profileImage.setImageURI(sFile); //set the image in the image view
            //store the image in Storage section in firebase
            final StorageReference reference=storage.getReference().child("profile_pic").child(Objects.requireNonNull(FirebaseAuth.getInstance().getUid()));

            reference.putFile(sFile).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(SettingsActivity.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    database.getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
                            .child("profilePic").setValue(uri.toString()); //set the image url in the realtime database
                });
            });
        }
    }
}