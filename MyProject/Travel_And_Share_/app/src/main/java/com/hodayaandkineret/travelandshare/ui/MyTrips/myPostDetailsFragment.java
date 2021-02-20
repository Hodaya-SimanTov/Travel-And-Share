package com.hodayaandkineret.travelandshare.ui.MyTrips;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hodayaandkineret.travelandshare.Model.Model;
import com.hodayaandkineret.travelandshare.Model.Post;
import com.hodayaandkineret.travelandshare.R;
import com.hodayaandkineret.travelandshare.ui.home.PostDetailsFragmentArgs;
import com.squareup.picasso.Picasso;


public class myPostDetailsFragment extends Fragment {
    ImageView imageTrip,editImage;
    EditText InputLocation,InputName,InputDescription,InputCost;
    Post post;
    CheckBox cbFamily,cbBenefactors,cbAccessible;
    ProgressBar progressBar;
    Button btnSave,btnDelete;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
      View view=inflater.inflate(R.layout.fragment_my_post_details, container, false);
      post = PostDetailsFragmentArgs.fromBundle(getArguments()).getMyPost();
      imageTrip=view.findViewById(R.id.Fragment_my_post_details__image);
      editImage=view.findViewById(R.id.Fragment_my_post_details_editImage);
      InputLocation=view.findViewById(R.id.Fragment_my_post_details__location);
      InputName=view.findViewById(R.id.Fragment_my_post_details__nametrip);
      InputDescription=view.findViewById(R.id.Fragment_my_post_details_description);
      InputCost=view.findViewById(R.id.Fragment_my_post_details_cost);
      cbFamily=view.findViewById(R.id.Fragment_my_post_details_cb_Families);
      cbBenefactors=view.findViewById(R.id.Fragment_my_post_details_cb_benefactors);
      cbAccessible=view.findViewById(R.id.Fragment_my_post_details_accessible);
      progressBar=view.findViewById(R.id.Fragment_my_post_details_prossbarr);
      btnSave=view.findViewById(R.id.Fragment_my_post_details__save_btn);
      btnDelete=view.findViewById(R.id.Fragment_my_post_details__delete_btn);
      btnDelete.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {
              deletePost(v);
          }
      });
      InputName.setText(post.getName());
      InputLocation.setText(post.getLocation());
      InputDescription.setText(post.getOpenText());
      InputCost.setText(post.getCost());
      cbFamily.setChecked(post.isForFamilies());
      cbBenefactors.setChecked(post.isForBenefactors());
      cbAccessible.setChecked(post.isAccessible());
      progressBar.setVisibility(View.INVISIBLE);
      Picasso.get().load(post.getImageUrl()).into(imageTrip);

      return view;
    }

    private void deletePost(View view) {
        progressBar.setVisibility(View.VISIBLE);
        Model.instance.DeletePost(post,new Model.DeletePostListener() {
            @Override
            public void onComplete() {
                progressBar.setVisibility(View.INVISIBLE);
                Navigation.findNavController(view).navigate(R.id.action_myPostDetailsFragment_to_nav_myTrips);
            }

        });
    }
}