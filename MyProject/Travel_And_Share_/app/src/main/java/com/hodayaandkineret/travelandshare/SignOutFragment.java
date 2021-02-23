package com.hodayaandkineret.travelandshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.google.firebase.auth.FirebaseAuth;


public class SignOutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sign_out, container, false);

        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                FirebaseAuth.getInstance().signOut();
                Navigation.findNavController(getView()).navigate(R.id.action_nav_signout_to_loginActivity);
            }
        };
        Handler handler=new Handler();
        handler.postDelayed(runnable,5000);
        return view;
    }
}