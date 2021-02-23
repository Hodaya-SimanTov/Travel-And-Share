package com.hodayaandkineret.travelandshare.Model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.widget.ImageView;

import androidx.lifecycle.LiveData;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hodayaandkineret.travelandshare.MyApplication;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static androidx.core.app.ActivityCompat.startActivityForResult;

public class Model {
    public final static  Model instance = new Model();
    private ModelFirebase PostModelFireBase=new ModelFirebase();
    private  ModelSql modelSql=new ModelSql();
    LiveData<List<Post>>postsList;
    LiveData<List<Post>>UserPostsList;
    FirebaseUser muser= FirebaseAuth.getInstance().getCurrentUser();

    public interface AddPostListener{
        void onComplete(boolean success);
    }
    public interface GetAllPostsListener{
        void onComplete(List<Post> data);
    }
    public interface GetAllUserPostsListener{
        void onComplete(List<Post> data);
    }
    public interface GetPostByIDsListener{
        void onComplete(Post post);
    }

    public LiveData<List<Post>> getAllPosts() {
       if(postsList==null){
           postsList=modelSql.getAllPosts();
           refreshAllPost(null);
       }
       return postsList;
    }
    public LiveData<List<Post>> getAllUserPosts(final String userId) {
        if(UserPostsList==null){
            UserPostsList=modelSql.getAllUserPosts(muser.getUid());
            refreshAllUserPost(null);
        }
        return  UserPostsList;
    }
    public interface refreshAllUserPostListener{
        void onComplete();
    }
    public interface DeletePostListener{
        void onComplete();
    }
    public void DeletePost(Post p,DeletePostListener lisener){
        PostModelFireBase.deletePost(p,lisener);
    }
    public void refreshAllUserPost(final refreshAllUserPostListener listener) {
        //1. get local last update date
        final SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long lastUpdated = sp.getLong("lastUpdated",0);
        //2. get all updated record from firebase from the last update date
        PostModelFireBase.getAllUserPosts(lastUpdated, new GetAllUserPostsListener() {
            @Override
            public void onComplete(List<Post> result) {
                //3. insert the new updates to the local db
                long lastU = 0;
                for (Post p : result) {
                    if(p.isDelete()){
                        ModelSql.deletePost(p,new DeletePostListener(){

                            @Override
                            public void onComplete() {

                            }
                        }

                       );
                    }else {
                        ModelSql.addPost(p, null);
                        if (p.getLastUpdated() > lastU) {
                            lastU = p.getLastUpdated();
                        }
                    }
                }
                //4. update the local last update date
                sp.edit().putLong("lastUpdated", lastU).commit();
                //5. return the updates data to the listeners
                if (listener != null) {
                    listener.onComplete();
                }
            }
        } );

    }

    public Post getPostById(final String id, final GetPostByIDsListener listener){
        PostModelFireBase.getPost(id,listener);
        return null;

    }
    public void addPost(final Post post,final AddPostListener listener){
        PostModelFireBase.addPost(post,listener);

    }
    public interface updatePostListener{
        void onComplete(boolean success);
    }
    public void uploadImage(Bitmap imageBmp, String fileName, final ModelFirebase.UploadImageListener listener) {
        ModelFirebase.uploadImage(imageBmp,fileName,listener);
    }
    public void UpdatePost(final Post post, final updatePostListener listener) {
        PostModelFireBase.updatePost(post,listener);

    }

    public interface refreshAllPostListener{
    void onComplete();
    }
    public void refreshAllPost(final refreshAllPostListener listener) {
        //1. get local last update date
        final SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long lastUpdated = sp.getLong("lastUpdated", 0);
        //2. get all updated record from firebase from the last update date
        PostModelFireBase.getAllPosts(lastUpdated, new GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> result) {
                //3. insert the new updates to the local db
                long lastU = 0;
                for (Post p : result) {
                    if (p.isDelete()) {
                        ModelSql.deletePost(p, new DeletePostListener() {
                            @Override
                            public void onComplete() {

                            }
                        });
                    } else {
                        ModelSql.addPost(p, null);
                        if (p.getLastUpdated() > lastU) {
                            lastU = p.getLastUpdated();
                        }
                    }
                }
                //4. update the local last update date
                sp.edit().putLong("lastUpdated", lastU).commit();
                //5. return the updates data to the listeners
                if (listener != null) {
                    listener.onComplete();
                }
            }
        });
    }

    private void showError(TextInputLayout field, String text) {
        field.setError(text);
        field.requestFocus();
    }

}

