package com.hodayaandkineret.travelandshare.Model;

import android.graphics.Bitmap;
import android.net.Uri;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {
    StorageReference storageRef;
    public void addPost(Post post,  Model.AddPostListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference ref = db.collection("PostInformation").document();
        String pid=ref.getId();
        post.setId(pid);
        db.collection("PostInformation").document(pid).set(post.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {

                listener.onComplete(true);
                                        }
        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onComplete(false);
                    }
        });
    }

    public static void getAllPosts(Long lastUpdated,Model.GetAllPostsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp ts = new Timestamp(lastUpdated,0);
        db.collection("PostInformation").whereGreaterThanOrEqualTo("lastUpdated",ts).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Post> postList = new LinkedList<Post>();
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        Post post =new Post();
                        post.fromMap(doc.getData());
                        postList.add(post);
                    }
                    listener.onComplete(postList);
                }else{
                    listener.onComplete(null);

                }
            }
        });
    }

    public static void getAllUserPosts(Long lastUpdated,Model.GetAllUserPostsListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Timestamp ts = new Timestamp(lastUpdated,0);
        FirebaseUser muser= FirebaseAuth.getInstance().getCurrentUser();
        db.collection("PostInformation").whereGreaterThanOrEqualTo("lastUpdated",ts).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    List<Post> postList = new LinkedList<Post>();
                    for (QueryDocumentSnapshot doc: task.getResult()) {
                        Post post =new Post();
                        post.fromMap(doc.getData());
                        if(post.getOwnerUid().equals(muser.getUid())) {
                            postList.add(post);
                        }
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
                if (task.isSuccessful()) {
                    Post post = task.getResult().toObject(Post.class);
                    listener.onComplete(post);
                } else {
                    listener.onComplete(null);
                }
            }
        });
    }

    public void deletePost(Post post, Model.DeletePostListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        post.setDelete(true);
       db.collection("PostInformation").document(post.getId()).update(post.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
           @Override
           public void onSuccess(Void aVoid) {
               listener.onComplete();
           }
       }).addOnFailureListener(new OnFailureListener() {
           @Override
           public void onFailure(@NonNull Exception e) {
               listener.onComplete();
           }
       });
    }

    public void updatePost(Post post, Model.updatePostListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("PostInformation").document(post.getId()).set(post.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override public void onSuccess(Void aVoid) {

                listener.onComplete(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onComplete(false);
            }
        });
    }

    public interface UploadImageListener{
        public void onComplete(String url);
    }
    public static void uploadImage(Bitmap imageBmp, String fileName, final UploadImageListener listener){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference imagesRef = storage.getReference().child("PostImages").child(fileName);
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