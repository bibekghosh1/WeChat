package com.bibek.chitchat.Adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.bibek.chitchat.Fragments.CallsFragment;
import com.bibek.chitchat.Fragments.ChatsFragment;

public class FragmentsAdapter extends FragmentStatePagerAdapter {
    public FragmentsAdapter(@NonNull FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1: return new CallsFragment();
            //setting the position accordingly of the fragments
            case 0:
            default: return new ChatsFragment();
        }
    }

    @Override
    public int getCount() {
        return 2;
    }  //set the number of fragments

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title=null;
        //Setting the title of fragment accordingly
        if (position==0){
            title="CHATS";
        }
        if(position==1){
            title="CALLS";
        }
        return title;
    }
}
