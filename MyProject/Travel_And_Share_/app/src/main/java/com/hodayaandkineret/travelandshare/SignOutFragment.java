package com.hodayaandkineret.travelandshare;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.firebase.auth.FirebaseAuth;


public class SignOutFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_sign_out, container, false);
        FirebaseAuth.getInstance().signOut();
        Runnable runnable=new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        };
        Handler handler=new Handler();
        handler.postDelayed(runnable,5000);
        return view;
    }
}