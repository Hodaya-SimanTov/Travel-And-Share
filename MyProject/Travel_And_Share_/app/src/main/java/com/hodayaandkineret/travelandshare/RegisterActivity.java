package com.hodayaandkineret.travelandshare;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import javax.annotation.Nullable;
import de.hdodenhof.circleimageview.CircleImageView;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hodayaandkineret.travelandshare.Model.ModelFirebase;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

public class RegisterActivity extends AppCompatActivity {
    private static final int REQUEST_CODE =101 ;
    private TextInputLayout InputsName, InputMail, InputPassword, InputVerifyPassword;
    CircleImageView ProfilImage;
    TextView haveAccount;
    Button signUpBtn;
    FirebaseAuth myAuth;
    FirebaseUser myUser;
    DatabaseReference mRef;
    ProgressDialog myLoadingDialog;
    StorageReference storageRef;
    Boolean flagCheckImage=false;
    Uri imageUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        InputsName =findViewById(R.id.activity_register_input_userName);
        InputMail =findViewById(R.id.activity_register_input_mail);
        InputPassword =findViewById(R.id.activity_register_input_password);
        InputVerifyPassword =findViewById(R.id.activity_register_input_verify_password);
        signUpBtn=findViewById(R.id.activity_register_btn_signup);
        myAuth=FirebaseAuth.getInstance();
        myLoadingDialog=new ProgressDialog(this);
        ProfilImage=findViewById(R.id.activity_register_profile_image);
        haveAccount=findViewById(R.id.activity_register_Have_Account);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singUpToApp();
            }
        });
        ProfilImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromPhone();
            }
        });
        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogIn();
            }
        });
    }

    private void goToLogIn() {
        Intent intent=new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
    }

    private void getImageFromPhone(){
        editImage();
    }
    //dialog for take an image for phone
    private void editImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                case 0: //return from camera
                    if (resultCode == RESULT_OK && data != null) {
                        imageUri=data.getData();
                        Bitmap selectedImage = (Bitmap) data.getExtras().get("data");
                        ProfilImage.setImageBitmap(selectedImage);
                        flagCheckImage=true;
                    }
                    break;
                case 1: //return from gallery
                    if( resultCode==RESULT_OK && data!=null){
                        imageUri=data.getData();
                        ProfilImage.setImageURI(imageUri);
                        flagCheckImage=true;
                    }
                    break;
            }
        }
    }



    private void singUpToApp(){
        String mail= InputMail.getEditText().getText().toString();
        String password= InputPassword.getEditText().getText().toString();
        String verifyPassword= InputVerifyPassword.getEditText().getText().toString();
        String username= InputsName.getEditText().getText().toString();

        if(mail.isEmpty() || !mail.contains("@")){
            showError(InputMail,"Email Address is not Valid");
        }
        else if(password.isEmpty() || password.length()<6){
            showError(InputPassword,"Password must be longer than 6 characters");
        }
        else if(!verifyPassword.equals(password)){
            showError(InputVerifyPassword,"Password does not equal");
        }
        else if(username.isEmpty() || username.length()<3)  {
            showError(InputsName,"UserName is not valid");
        }
        else   if(flagCheckImage==false) {
            Toast.makeText(this,"Please select an image",Toast.LENGTH_SHORT).show();
        }
        else {
           createAccountInFirebase(mail,password,username);
        }

    }
    private void createAccountInFirebase(String mail,String password,String username){
            myLoadingDialog.setTitle("Registration");
            myLoadingDialog.setMessage("Please Wait, Creating Your Account");
            myLoadingDialog.setCanceledOnTouchOutside(false);
            myLoadingDialog.show();
            myAuth.createUserWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        SaveUserData(username);
                    }
                    else{
                        myLoadingDialog.dismiss();
                        Toast.makeText(RegisterActivity.this,"Account Creation Failed",Toast.LENGTH_LONG).show();
                    }
                }
            });
    }

    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }

    private void SaveUserData(String username){
        myUser= myAuth.getCurrentUser();
        mRef=FirebaseDatabase.getInstance().getReference().child("User");
        SaveDataRealTime(username);
        //SaveData(username);
    }

    private void SaveDataRealTime(String username) {
        Bitmap bitmap = ((BitmapDrawable) ProfilImage.getDrawable()).getBitmap();
        uploadImage(bitmap, myUser.getUid(), new ModelFirebase.UploadImageListener() {

            @Override
            public void onComplete(String url) {
                HashMap hashMap = new HashMap();
                hashMap.put("username", username);
                hashMap.put("profilImage", url);
                hashMap.put("LastName", "");
                hashMap.put("status", "offline");
                mRef.child(myUser.getUid()).updateChildren(hashMap).addOnSuccessListener(new OnSuccessListener() {
                    @Override
                    public void onSuccess(Object o) {
                        myLoadingDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account  Created!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        myLoadingDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    public static void uploadImage(Bitmap imageBmp, String fileName, final ModelFirebase.UploadImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef = storage.getReference().child("ProfilImages").child(fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception exception) {
                listener.onComplete(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                imagesRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadUrl = uri;
                        listener.onComplete(downloadUrl.toString());
                    }
                });
            }
        });
    }

}