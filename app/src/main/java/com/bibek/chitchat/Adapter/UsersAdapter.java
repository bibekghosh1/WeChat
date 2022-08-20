package com.bibek.chitchat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bibek.chitchat.ChatDetailsActivity;
import com.bibek.chitchat.R;
import com.bibek.chitchat.models.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

            ArrayList<Users> list;
            Context context;

    public UsersAdapter(ArrayList<Users> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.sample_show_user,parent,false); //linking the show user layout design
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
                Users users=list.get(position);
        Picasso.get().load(users.getProfilePic()).placeholder(R.drawable.avatar).into(holder.image);
        holder.userName.setText(users.getUserName());
        //to set the last message under username on mainActivity
        FirebaseDatabase.getInstance()
                    .getReference()
                         .child("chats")
                                .child(FirebaseAuth.getInstance().getUid()+users.getUserId())
                                     .orderByChild("timeStamp")
                                             .limitToLast(1)
                                                     .addListenerForSingleValueEvent(new ValueEventListener() {
                                                         @Override
                                                         public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                             if(snapshot.hasChildren()){
                                                                 for (DataSnapshot snapshot1:snapshot.getChildren()){
                                                                     holder.lastMessage.setText(Objects.requireNonNull(snapshot1.child("message").getValue()).toString());
                                                                 }
                                                             }
                                                         }

                                                         @Override
                                                         public void onCancelled(@NonNull DatabaseError error) {

                                                         }
                                                     });

        holder.itemView.setOnClickListener(view -> {
            Intent intent=new Intent(context, ChatDetailsActivity.class);
            intent.putExtra("userId",users.getUserId()); //sending data through intent
            intent.putExtra("userName",users.getUserName());
            intent.putExtra("profilePic",users.getProfilePic());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView userName,lastMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        image=itemView.findViewById(R.id.profileImage);
        userName=itemView.findViewById(R.id.userName);
        lastMessage=itemView.findViewById(R.id.lastMessage);

        }
    }
}
