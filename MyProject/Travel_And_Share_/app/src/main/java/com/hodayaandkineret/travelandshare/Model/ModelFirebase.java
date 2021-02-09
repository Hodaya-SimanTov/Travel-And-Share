package com.hodayaandkineret.travelandshare.Model;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hodayaandkineret.travelandshare.LoginActivity;
import com.hodayaandkineret.travelandshare.MainActivity;
import com.hodayaandkineret.travelandshare.RegisterActivity;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class ModelFirebase {
    StorageReference storageRef;
    public void addPost(Post post,  Model.AddPostListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String,Object> data = new HashMap<String,Object>();

        data.put("id",post.getId());
        data.put("name",post.getName());
        data.put("address",post.getAddress());
        data.put("cost",post.getCost());
        data.put("ageFrom",post.getAgeFrom());
        data.put("ageTo",post.getAgeTo());

        data.put("openText",post.getOpenText());
        data.put("ownerUid",post.getOwnerUid());
        storageRef= FirebaseStorage.getInstance().getReference().child("PostImages");
        storageRef.child(post.getOwnerUid()).putFile(post.getImageUrl()).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    storageRef.child(post.getOwnerUid()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            data.put("imagePostUrl",uri.toString());
                            db.collection("PostInformation").document(post.getOwnerUid())
                                    .set(data)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {

                                            listener.onComplete(true);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            listener.onComplete(false);
                                        }
                                    });
                        }
                    })  ;
                }
            }
        }) ;
    }

    public static void getAllPosts(Model.GetAllPostsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PostInformation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Post> postList = new LinkedList<Post>();
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        Post post = doc.toObject(Post.class);
                        postList.add(post);
                    }
                    listener.onComplete(postList);
                }else{
                    listener.onComplete(null);
                }
            }
        });
    }

    public static void getAllUserPosts(String userId,Model.GetAllUserPostsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PostInformation").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Post> postList = new LinkedList<Post>();
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        Post post = doc.toObject(Post.class);
                        if(post.getOwnerUid().equals(userId))
                            postList.add(post);
                    }
                    listener.onComplete(postList);
                }else{
                    listener.onComplete(null);
                }
            }
        });
    }

    public static void getPost(String pid, Model.GetPostByIDsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PostInformation").document(pid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()){
                    Post post  = task.getResult().toObject(Post.class);
                    listener.onComplete(post);
                }else{
                    listener.onComplete(null);
                }
            }
        });
    }













}
