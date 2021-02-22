package com.hodayaandkineret.travelandshare.ui.home;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hodayaandkineret.travelandshare.Model.Post;
import com.hodayaandkineret.travelandshare.R;
import com.squareup.picasso.Picasso;


public class PostDetailsFragment extends Fragment {
    Post post;
    ImageView like;
    TextView countLike;
    FirebaseUser muser;
    DatabaseReference likeRef;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_post_details, container, false);
        post=PostDetailsFragmentArgs.fromBundle(getArguments()).getMyPost();
        ImageView image=view.findViewById(R.id.Fragment_post_details_image);

        //TextInputLayout location = view.findViewById(R.id.Fragment_post_details_name);

        TextView name=view.findViewById(R.id.Fragment_post_details_name);
        TextView location=view.findViewById(R.id.post_details_location);
        TextView cost=view.findViewById(R.id.post_details_cost);
        TextView description=view.findViewById(R.id.Fragment_post_details_description);
        TextView accessible= view.findViewById(R.id.Fragment_post_details_accessible);
        TextView benefactors=view.findViewById(R.id.Fragment_post_details_benefactors);
        TextView Family=view.findViewById(R.id.Fragment_post_details_family);
        like=view.findViewById(R.id.Fragment_post_details_like);
        countLike=view.findViewById(R.id.Fragment_post_details_count_like);
        muser= FirebaseAuth.getInstance().getCurrentUser();
        likeRef= FirebaseDatabase.getInstance().getReference().child("Likes");
        name.setText(post.getName());
        Picasso.get().load(post.getImageUrl()).into(image);
        location.setText(post.getLocation());
        description.setText(post.getOpenText());
        cost.setText(post.getCost());
        if(post.isAccessible()){
            accessible.setTextColor(Color.CYAN);
        }
        else{
            accessible.setEnabled(false);
        }
        if(post.isForBenefactors()){
            benefactors.setTextColor(Color.CYAN);
        }
        else{
            benefactors.setEnabled(false);
        }
        if(post.isForFamilies()){
            Family.setTextColor(Color.CYAN);
        }
        else{
            Family.setEnabled(false);
        }
        countLike(post.getId(),muser.getUid(),likeRef);
        like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    likePost(v);
                }
            });
        return view;
    }
    private void likePost(View v) {
        likeRef.child(post.getId()).child(muser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    likeRef.child(post.getId()).child(muser.getUid()).removeValue();
                    like.setColorFilter(Color.GRAY);
                }
                else{
                    likeRef.child(post.getId()).child(muser.getUid()).setValue("like");
                    like.setColorFilter(-16343841);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(),""+error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void countLike(String id, String uid, DatabaseReference likeRef) {

        likeRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int totalLikes=(int)snapshot.getChildrenCount();
                    countLike.setText(totalLikes+"");
                }
                else{
                    countLike.setText("0");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        likeRef.child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(uid).exists()){
                    like.setColorFilter(-16343841);
                }
                else{
                    like.setColorFilter(Color.GRAY);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}


