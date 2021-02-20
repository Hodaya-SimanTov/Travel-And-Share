package com.hodayaandkineret.travelandshare.ui.EditProfile;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hodayaandkineret.travelandshare.LoginActivity;
import com.hodayaandkineret.travelandshare.MainActivity;
import com.hodayaandkineret.travelandshare.Model.ModelFirebase;
import com.hodayaandkineret.travelandshare.R;
import com.hodayaandkineret.travelandshare.RegisterActivity;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_CANCELED;
import static com.hodayaandkineret.travelandshare.RegisterActivity.uploadImage;


public class EditProfileFragment extends Fragment {
    FirebaseAuth m2Auth;
    FirebaseUser m2User;
    DatabaseReference m2UserRef;
    CircleImageView editProfileImage;
    TextInputLayout editProfileName, editProfileLastName;
    EditText t1,t2;
    String imageV, nameV, lastNameV;
    Button updateProfileBtn;
    Uri imageUri1;
    Boolean flagCheckImage1 = false;
    DatabaseReference mRef;
    ProgressDialog myLoadingDialog;
    private static final int RESULT_CANCELED =0 ;
    private static final int RESULT_OK =-1 ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        m2Auth = FirebaseAuth.getInstance();
        m2User = m2Auth.getCurrentUser();
        m2UserRef = FirebaseDatabase.getInstance().getReference().child("User");
        editProfileImage = view.findViewById(R.id.fragment_edit_profile_imageview);
        editProfileName = view.findViewById(R.id.fragment_edit_profile_name);
        editProfileLastName = view.findViewById(R.id.fragment_edit_profile_lastname);
        t1 = view.findViewById(R.id.etid_profile_name);
        t2 = view.findViewById(R.id.edit_profile_lastname);
        //editProfileBirthDate = view.findViewById(R.id.fragment_edit_profile_birthDate);
        updateProfileBtn = view.findViewById(R.id.fragment_edit_profile_saveBtn);
        myLoadingDialog=new ProgressDialog(getContext());
        getInformationForFirebase();
        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromPhone();
            }
        });
        updateProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateUserProfile();

            }
        });
        return view;
    }

    private void getImageFromPhone(){
        editImage();
    }
    //dialog for take an image for phone
    private void editImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void UpdateUserProfile() {
        myLoadingDialog.setTitle("Update Profile");
        myLoadingDialog.setMessage("Please Wait, Updating your profile!");
        myLoadingDialog.setCanceledOnTouchOutside(false);
        myLoadingDialog.show();
        String newPass, newName, newLastName;
        newName = editProfileName.getEditText().getText().toString();
        newLastName = editProfileLastName.getEditText().getText().toString();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mRef=FirebaseDatabase.getInstance().getReference().child("User");

        if (newName.isEmpty() || newName.length() < 3) {
            showError(editProfileName, "First Name is not valid");
        }
        else{
            Bitmap bitmap = ((BitmapDrawable)  editProfileImage.getDrawable()).getBitmap();
            uploadImage(bitmap, user.getUid(), new ModelFirebase.UploadImageListener() {
                @Override
                public void onComplete(String url) {

                    mRef.child(user.getUid()).updateChildren(toMap(newName,newLastName,url) ).addOnSuccessListener(new OnSuccessListener() {
                        @Override
                        public void onSuccess(Object o) {
                            myLoadingDialog.dismiss();
                            Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_LONG).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            myLoadingDialog.dismiss();
                            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            Navigation.findNavController(getView()).navigate(R.id.action_nav_editProfile_to_nav_home);
        }
    }

    public void getInformationForFirebase() {

        m2UserRef.child(m2User.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    imageV = snapshot.child("profilImage").getValue().toString();
                    nameV = snapshot.child("username").getValue().toString();
                    lastNameV = snapshot.child("LastName").getValue().toString();

                    Picasso.get().load(imageV).into(editProfileImage);
                    t1.setText(nameV);
                    t2.setText(lastNameV);
                    //editProfileName.setText(nameV);
                    //editProfileLastName.setText(snapshot.child("LastName").getValue().toString());


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //  Toast.makeText(, "Sorry, something went wrong", Toast.LENGTH_SHORT).show();

            }
        });


    }

//    private void showError(EditText field, String text) {
//        field.setError(text);
//        field.requestFocus();
//    }
    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }


    private Map<String, Object> toMap(String name,String Family,String image) {
        HashMap<String, Object> result = new HashMap<>();
        result.put("username", name);
        result.put("profilImage", image);
        result.put("LastName", Family);
        result.put("status", "offline");
        return result;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0: //return from camera
                    if (resultCode == RESULT_OK && data != null) {
                        imageUri1 = data.getData();
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        editProfileImage.setImageBitmap(selectedImage);
                        flagCheckImage1 = true;

                    }
                    break;
                case 1: //return from gallery
                    if (resultCode == RESULT_OK && data != null) {
                        imageUri1 = data.getData();
                        editProfileImage.setImageURI(imageUri1);
                        flagCheckImage1 = true;
                    }
                    break;
            }
        }
    }
}