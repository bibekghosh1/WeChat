package com.bibek.chitchat.Adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bibek.chitchat.R;
import com.bibek.chitchat.models.MessageModels;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ChatAdapter extends RecyclerView.Adapter {
        ArrayList<MessageModels> messageModel;
        Context context;
        int SENDER_VIEW_TYPE=1;
        int RECEIVER_VIEW_TYPE=2;
        String receiverId;

    public ChatAdapter(ArrayList<MessageModels> messageModel, Context context, String receiverId) {
        this.messageModel = messageModel;
        this.context = context;
        this.receiverId = receiverId;
    }

    public ChatAdapter(ArrayList<MessageModels> messageModel, Context context) {
        this.messageModel = messageModel;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==SENDER_VIEW_TYPE){
            View view= LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }else{
            View view=LayoutInflater.from(context).inflate(R.layout.sample_reciver,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if(messageModel.get(position).getuId().equals(FirebaseAuth.getInstance().getUid())){
            return SENDER_VIEW_TYPE;
        }else {
            return RECEIVER_VIEW_TYPE;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            MessageModels messageModels= messageModel.get(position);

            //longPress the msg alert will popup for deletion
        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("are you sure ?")
                    .setPositiveButton("Yes", (dialogInterface, i) -> {
                        //Yes will set the Null value on that ->Hide,delete
                        FirebaseDatabase database=FirebaseDatabase.getInstance();
                        String senderRoom=FirebaseAuth.getInstance().getUid()+receiverId;
                        database.getReference()
                                .child("chats")
                                .child(senderRoom)
                                .child(messageModels.getMessageId())
                                .setValue(null);
                    }).setNegativeButton("No", (dialogInterface, i) -> {
                        dialogInterface.dismiss();  //No button will dismiss the alert box
                    }).show(); //this will show the alert bos after long press

            return false;
        });
                    //this is responsible for set the time and msg on View
            if(holder.getClass()==SenderViewHolder.class){
                ((SenderViewHolder)holder).senderMsg.setText(messageModels.getMessage());

                Date date=new Date(messageModels.getTimeStamp());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a");
                String strDate=simpleDateFormat.format(date);
                ((SenderViewHolder)holder).senderTime.setText(strDate);
            }else {
                ((ReceiverViewHolder)holder).receiverMsg.setText(messageModels.getMessage());
                Date date=new Date(messageModels.getTimeStamp());
                @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat=new SimpleDateFormat("h:mm a");
                String strDate=simpleDateFormat.format(date);
                ((ReceiverViewHolder)holder).receiverTime.setText(strDate);
            }
    }

    @Override
    public int getItemCount() {
        return messageModel.size();
    }
        //to set the messages on the view
    public  class ReceiverViewHolder extends RecyclerView.ViewHolder{
            TextView receiverMsg,receiverTime;
        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);
            receiverMsg=itemView.findViewById(R.id.receiverText);
            receiverTime=itemView.findViewById(R.id.receiverTime);

        }
    }
    public class SenderViewHolder extends RecyclerView.ViewHolder{
        TextView senderMsg,senderTime;
        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMsg=itemView.findViewById(R.id.senderText);
            senderTime=itemView.findViewById(R.id.senderTime);
        }
    }
}
