package com.hodayaandkineret.travelandshare.ui.MyTrips;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
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

import com.google.android.material.textfield.TextInputLayout;
import com.hodayaandkineret.travelandshare.Model.Model;
import com.hodayaandkineret.travelandshare.Model.ModelFirebase;
import com.hodayaandkineret.travelandshare.Model.Post;
import com.hodayaandkineret.travelandshare.R;
import com.hodayaandkineret.travelandshare.ui.home.PostDetailsFragmentArgs;
import com.squareup.picasso.Picasso;


public class myPostDetailsFragment extends Fragment {
    private static final int RESULT_CANCELED =0 ;
    private static final int RESULT_OK =-1 ;
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
      InputLocation=view.findViewById(R.id.my_post_details_location);
      InputName=view.findViewById(R.id.my_post_details_tripName);
      InputDescription=view.findViewById(R.id.Fragment_post_details_description_et);
      InputCost=view.findViewById(R.id.my_post_details_cost);
      cbFamily=view.findViewById(R.id.Fragment_my_post_details_cb_Families);
      cbBenefactors=view.findViewById(R.id.Fragment_my_post_details_cb_benefactors);
      cbAccessible=view.findViewById(R.id.Fragment_my_post_details_accessible);
      progressBar=view.findViewById(R.id.Fragment_my_post_details_prossbarr);
      btnSave=view.findViewById(R.id.Fragment_my_post_details__save_btn);
      btnDelete=view.findViewById(R.id.Fragment_my_post_details__delete_btn);
      editImage=view.findViewById(R.id.Fragment_my_post_details_editImage);
      btnDelete.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { deletePost(v); }});
      btnSave.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { UpdatePostInFirebase(); }});
      editImage.setOnClickListener(new View.OnClickListener() {@Override public void onClick(View v) { getImageFromPhone(); }});
      progressBar.setVisibility(View.INVISIBLE);
      InputName.setText(post.getName());
      InputLocation.setText(post.getLocation());
      InputDescription.setText(post.getOpenText());
      InputCost.setText(post.getCost());
      cbFamily.setChecked(post.isForFamilies());
      cbBenefactors.setChecked(post.isForBenefactors());
      cbAccessible.setChecked(post.isAccessible());
      Picasso.get().load(post.getImageUrl()).into(imageTrip);

      return view;
    }

    private void UpdatePostInFirebase() {
        if(InputName.getText().toString().isEmpty()){
            showError(InputName,"Name is not valid");
        }
        else if(InputLocation.getText().toString().isEmpty()){
            showError(InputLocation,"Location is not valid");
        }
        else{
            if(InputCost.getText().toString().isEmpty()){
              post.setCost("");
            }
            else{
                post.setCost(InputCost.getText().toString());
            }
            if(InputDescription.getText().toString().isEmpty()){
                post.setOpenText("");
            }
            else{
                post.setOpenText(InputDescription.getText().toString());
            }
            post.setName(InputName.getText().toString());
            post.setLocation(InputLocation.getText().toString());
            post.setAccessible(cbAccessible.isChecked());
            post.setForFamilies(cbFamily.isChecked());
            post.setForBenefactors(cbBenefactors.isChecked());
            updatePostInFirebase(post);
        }

    }

    private void updatePostInFirebase(Post post) {
        progressBar.setVisibility(View.VISIBLE);

        Bitmap bitmap = ((BitmapDrawable) imageTrip.getDrawable()).getBitmap();
        Model.instance.uploadImage(bitmap,post.getOwnerUid()+post.getName(), new ModelFirebase.UploadImageListener() {
            @Override
            public void onComplete(String url) {
                if (url == null){
                    progressBar.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Save Image Filed");
                    builder.setMessage("the operation was cancelled, please try again...");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                }else{
                    post.setImageUrl(url);
                    Model.instance.UpdatePost(post, new Model.updatePostListener() {
                        @Override
                        public void onComplete(boolean success) {
                            if(success) {
                                Toast.makeText(getView().getContext(), "Post is  Created", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(R.id.action_myPostDetailsFragment_to_nav_myTrips);
                            }
                            else{
                                Toast.makeText(getView().getContext(), "Error! Post is not Created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void showError( EditText field, String text) {
        field.setError(text);
        field.requestFocus();
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
    private void getImageFromPhone(){
        editImage();
    }
    //dialog for take an image for phone
    private void editImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose your profile picture");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent takePicture = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else if (options[item].equals("Choose from Gallery")) {
//                   Intent pickPhoto = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    Intent pickPhoto=new Intent(Intent.ACTION_GET_CONTENT)   ;
                    pickPhoto.setType("image/*");
                    startActivityForResult(pickPhoto, 1);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0: //return from camera
                    if (resultCode == RESULT_OK && data != null) {
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        imageTrip.setImageBitmap(selectedImage);
                    }
                    break;
                case 1: //return from gallery
                    if( resultCode==RESULT_OK && data!=null){
                        imageTrip.setImageURI(data.getData());
                    }
                    break;
            }
        }
    }
}