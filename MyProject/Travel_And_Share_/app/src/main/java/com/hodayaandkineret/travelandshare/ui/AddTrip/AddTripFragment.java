package com.hodayaandkineret.travelandshare.ui.AddTrip;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.hodayaandkineret.travelandshare.R;

public class AddTripFragment extends Fragment {


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        //final TextView textView = view.findViewById(R.id.text_gallery);

        return view;
    }
}