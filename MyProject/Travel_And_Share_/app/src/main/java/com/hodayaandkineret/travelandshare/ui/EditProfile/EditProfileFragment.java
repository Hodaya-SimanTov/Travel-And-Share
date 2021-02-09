package com.hodayaandkineret.travelandshare.ui.EditProfile;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hodayaandkineret.travelandshare.LoginActivity;
import com.hodayaandkineret.travelandshare.MainActivity;
import com.hodayaandkineret.travelandshare.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


public class EditProfileFragment extends Fragment {
    FirebaseAuth m2Auth;
    FirebaseUser m2User;
    DatabaseReference m2UserRef;
    CircleImageView editProfileImage;
    EditText editProfileName;
    EditText editProfileLastName;
    EditText editProfileBirthDate;
    EditText editProfilePassword;
    String imageV, nameV, lastNameV, birthDateV, passwordV;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        m2Auth=FirebaseAuth.getInstance();
        m2User=m2Auth.getCurrentUser();
        m2UserRef= FirebaseDatabase.getInstance().getReference().child("User");
        editProfileImage = view.findViewById(R.id.fragment_edit_profile_imageview);
        editProfileName = view.findViewById(R.id.fragment_edit_profile_name);
        editProfileLastName = view.findViewById(R.id.fragment_edit_profile_lastname);
        editProfileBirthDate = view.findViewById(R.id.fragment_edit_profile_birthDate);
        editProfilePassword = view.findViewById(R.id.fragment_edit_profile_password);
        return view;
    }

    public void onStart() {
        super.onStart();
        m2UserRef.child(m2User.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imageV = snapshot.child("profilImage").getValue().toString();
                    nameV = snapshot.child("username").getValue().toString();
                    Picasso.get().load(imageV).into(editProfileImage);
                    editProfileName.setText(nameV);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
              //  Toast.makeText(, "Sorry, something went wrong", Toast.LENGTH_SHORT).show();

            }
        });

    }
}