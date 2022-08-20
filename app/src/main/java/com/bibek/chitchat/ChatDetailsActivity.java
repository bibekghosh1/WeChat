package com.bibek.chitchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import com.bibek.chitchat.Adapter.ChatAdapter;
import com.bibek.chitchat.databinding.ActivityChatDetailsBinding;
import com.bibek.chitchat.models.MessageModels;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class ChatDetailsActivity extends AppCompatActivity {
    ActivityChatDetailsBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityChatDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();  //hide the action bar from top

        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();

        final String senderId=auth.getUid();
        String receiverId=getIntent().getStringExtra("userId"); //fetching the data from anther activity using intent
        String username=getIntent().getStringExtra("userName");
        String profilePic=getIntent().getStringExtra("profilePic");

        binding.userName.setText(username);  //set the name in textview
        Picasso.get().load(profilePic).placeholder(R.drawable.avatar).into(binding.profileImage); //set the image with is fetched from another activity

        final ArrayList<MessageModels> messageModel=new ArrayList<>();
        final ChatAdapter chatAdapter=new ChatAdapter(messageModel,this,receiverId);

        binding.chatRecyclerView.setAdapter(chatAdapter);
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        binding.chatRecyclerView.setLayoutManager(layoutManager);

        final  String senderRoom=senderId+receiverId;
        final  String receiverRoom=receiverId+senderId;

        //this will show the msg on the View
        database.getReference().child("chats").child(senderRoom).addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messageModel.clear();
                for(DataSnapshot snapshot1:snapshot.getChildren()){
                    MessageModels model=snapshot1.getValue(MessageModels.class);
                    assert model != null;
                    model.setMessageId(snapshot1.getKey());
                    messageModel.add(model);
                }
                chatAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.send.setOnClickListener(view -> {
            String message=binding.enterMessage.getText().toString(); //fetching the msg from EditText field
            final MessageModels model=new MessageModels(senderId,message);  //storing the uid and message into the model
            model.setTimeStamp(new Date().getTime()); //fetching the time when user type the message
            binding.enterMessage.setText(""); //set msg box to null

            database.getReference().child("chats")
                    .child(senderRoom)
                    .push()
                    .setValue(model).addOnSuccessListener(unused ->
                            database.getReference().child("chats")
                            .child(receiverRoom)
                            .push()
                            .setValue(model).addOnSuccessListener(unused1 -> {

                            }));
        });

        binding.backArrow.setOnClickListener(view -> {
            Intent intent=new Intent(ChatDetailsActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
    // this the outside of onCreate

    @Override
    public void onBackPressed() {
       // super.onBackPressed();
        Intent intent=new Intent(ChatDetailsActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}