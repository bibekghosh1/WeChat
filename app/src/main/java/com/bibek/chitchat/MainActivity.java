package com.bibek.chitchat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bibek.chitchat.Adapter.FragmentsAdapter;
import com.bibek.chitchat.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{
            FirebaseAuth mAuth;
            ActivityMainBinding binding;

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).setElevation(0); //to remove the below shadow of tabLayout

        mAuth=FirebaseAuth.getInstance();
        binding.viewPager.setAdapter(new FragmentsAdapter(getSupportFragmentManager()));
        binding.tabLayout.setupWithViewPager(binding.viewPager);


    }


        //linking the menu.xml to the mainActivity
    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        MenuInflater inflater=getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }
        //here we can define which menu item do what work
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.settings:
                //Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
                Intent intent1=new Intent(MainActivity.this,SettingsActivity.class);
                startActivity(intent1);
                finish();
                break;
            case R.id.groupChat:
                //Toast.makeText(this,"group chat joined",Toast.LENGTH_SHORT).show();
                Intent intent=new Intent(MainActivity.this,GroupChatActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.logout:
                Intent intent2=new Intent(MainActivity.this,SignInActivity.class);
                startActivity(intent2);
                finish();
                mAuth.signOut();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}