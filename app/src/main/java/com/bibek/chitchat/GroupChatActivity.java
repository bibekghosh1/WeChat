package com.bibek.chitchat;

import static com.bibek.chitchat.R.string.all_members;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bibek.chitchat.Adapter.ChatAdapter;
import com.bibek.chitchat.databinding.ActivityGroupChatBinding;
import com.bibek.chitchat.models.MessageModels;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class GroupChatActivity extends AppCompatActivity{

    ActivityGroupChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide(); //this will hide topBar
        final FirebaseDatabase database=FirebaseDatabase.getInstance();
        final ArrayList<MessageModels> messageModel=new ArrayList<>(); //creating a messageModels type ArrayList Obj

        final String senderId= FirebaseAuth.getInstance().getUid(); //fetching the current user's ID

        binding.userName.setText(all_members);  //set the username filed

        final ChatAdapter adapter=new ChatAdapter(messageModel,this);
        binding.chatRecyclerView.setAdapter(adapter);

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        //this will show the msg on view
        database.getReference().child("Group Chat").addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModel.clear(); //array list is clear
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    MessageModels model=dataSnapshot.getValue(MessageModels.class);
                    messageModel.add(model);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener(view -> {
            final  String message=binding.enterMessage.getText().toString(); //fetching the msg from user and storing into message variable
            final MessageModels model=new MessageModels(senderId,message);
            model.setTimeStamp(new Date().getTime()); //getting the time

            binding.enterMessage.setText(""); //set the field null
            //storing the msg into the firebase
            database.getReference().child("Group Chat").push().setValue(model).addOnSuccessListener(unused -> {
                //Toast.makeText(GroupChatActivity.this, "Sent", Toast.LENGTH_SHORT).show();
            });
        });

        binding.backArrow.setOnClickListener(view -> {
            Intent intent=new Intent(GroupChatActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
    //this is OutSide of onCreate method


    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent intent=new Intent(GroupChatActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}