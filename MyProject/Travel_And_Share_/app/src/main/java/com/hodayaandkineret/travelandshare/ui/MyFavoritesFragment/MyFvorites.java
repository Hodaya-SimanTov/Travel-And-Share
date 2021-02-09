package com.hodayaandkineret.travelandshare.ui.MyFavoritesFragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hodayaandkineret.travelandshare.R;


public class MyFvorites extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_myfavoritestrips, container, false);
        return view;
    }
}