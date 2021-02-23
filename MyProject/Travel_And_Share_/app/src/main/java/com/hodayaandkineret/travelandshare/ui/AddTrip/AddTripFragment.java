package com.hodayaandkineret.travelandshare.ui.AddTrip;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hodayaandkineret.travelandshare.Model.Model;
import com.hodayaandkineret.travelandshare.Model.ModelFirebase;
import com.hodayaandkineret.travelandshare.Model.Post;
import com.hodayaandkineret.travelandshare.R;

public class AddTripFragment extends Fragment {
    private static final int RESULT_CANCELED =0 ;
    private static final int RESULT_OK =-1 ;
    FirebaseUser myUser;
    ProgressBar progressBar;
    ImageView InputImageTrip;
    TextInputLayout InputName, InputLocation, InputCost;
    EditText InputDescription;
    //EditText InputName,InputLocation,InputCost,InputDescription;
    CheckBox cbFamily,cbBenefactors,cbAccessible;
    Button btn_addTrip;
    Uri imageUri;
    boolean flagAddImage=false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View view = inflater.inflate(R.layout.fragment_add_trip1, container, false);
        progressBar=view.findViewById(R.id.Fragment_AddTrip_prossbarr);
        InputImageTrip=view.findViewById(R.id.Fragment_AddTrip_imageTrip);
        InputName=view.findViewById(R.id.Fragment_AddTrip_tripName);
        InputLocation=view.findViewById(R.id.Fragment_AddTrip_tripLocation);
        InputCost=view.findViewById(R.id.Fragment_AddTrip_tripCost);
        InputDescription=view.findViewById(R.id.Fragment_AddTrip_description);
        cbFamily=view.findViewById(R.id.Fragment_AddTrip_cb_Families);
        cbBenefactors=view.findViewById(R.id.Fragment_my_post_details_cb_benefactors);
        cbAccessible=view.findViewById(R.id.Fragment_AddTrip_accessible);
        btn_addTrip=view.findViewById(R.id.Fradment_my_post_details__save_btn);
        progressBar.setVisibility(View.INVISIBLE);
        myUser= FirebaseAuth.getInstance().getCurrentUser();
        InputImageTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromPhone();
            }
        });
        btn_addTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddPost();
            }
        });

       return view;
    }

    private void AddPost() {
        String name,location,cost,description;
        name=InputName.getEditText().getText().toString();
        location=InputLocation.getEditText().getText().toString();
        cost=InputCost.getEditText().getText().toString();
        description=InputDescription.getText().toString();

        if(name.isEmpty()){
            showError(InputName,"Name is not valid");
         }
        else if(location.isEmpty()){
            showError(InputLocation,"Location is not valid");
        }
        else if(flagAddImage==false){
            Toast.makeText(getActivity(),"Please select an image",Toast.LENGTH_SHORT).show();
        }
        else{
            if(cost.isEmpty()){
                cost="";
            }
            if(description.isEmpty()){
                description="";
            }
            Post post=new Post();
            post.setName(name);
            post.setLocation(location);
            post.setCost(cost);
            post.setOpenText(description);
            post.setAccessible(cbAccessible.isChecked());
            post.setForFamilies(cbFamily.isChecked());
            post.setForBenefactors(cbBenefactors.isChecked());
            post.setOwnerUid(myUser.getUid());
            post.setDelete(false);
            savePostInFirebase(post);
        }
    }

    private void savePostInFirebase(Post post) {
        progressBar.setVisibility(View.VISIBLE);

        Bitmap bitmap = ((BitmapDrawable) InputImageTrip.getDrawable()).getBitmap();
        Model.instance.uploadImage(bitmap, myUser.getUid() + post.getName(), new ModelFirebase.UploadImageListener() {
            @Override
            public void onComplete(String url) {
                if (url == null) {
                    progressBar.setVisibility(View.INVISIBLE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setTitle("Save Image Filed");
                    builder.setMessage("The operation was cancelled, please try again...");
                    builder.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builder.show();
                } else {
                    post.setImageUrl(url);
                    Model.instance.addPost(post, new Model.AddPostListener() {
                        @Override
                        public void onComplete(boolean success) {
                            if (success) {
                                Toast.makeText(getView().getContext(), "Post is  Created", Toast.LENGTH_SHORT).show();
                                Navigation.findNavController(getView()).navigate(R.id.action_nav_addTrip_to_nav_home);
                            } else {
                                Toast.makeText(getView().getContext(), "Error! Post is not Created", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
    private void getImageFromPhone( ){
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
                        InputImageTrip.setImageBitmap(selectedImage);
                        flagAddImage=true;
                    }
                    break;
                case 1: //return from gallery
                    if( resultCode==RESULT_OK && data!=null){
                        imageUri=data.getData();
                        InputImageTrip.setImageURI(imageUri);
                        flagAddImage=true;
                    }
                    break;
            }
        }
    }
    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }
}