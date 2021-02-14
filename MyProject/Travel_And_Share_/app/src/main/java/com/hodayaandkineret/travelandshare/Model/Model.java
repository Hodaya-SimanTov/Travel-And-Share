package com.hodayaandkineret.travelandshare.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;

import com.hodayaandkineret.travelandshare.MyApplication;

import java.util.List;

public class Model {
    public final static  Model instance = new Model();
    private ModelFirebase PostModelFireBase=new ModelFirebase();
    private  ModelSql modelSql=new ModelSql();
    LiveData<List<Post>>postsList;

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
    public void getAllUserPosts(final String userId,final GetAllUserPostsListener listener) {
        PostModelFireBase.getAllUserPosts(userId,listener);
    }
    public Post getPostById(final String id, final GetPostByIDsListener listener){
        PostModelFireBase.getPost(id,listener);
        return null;

    }
    public void addPost(final Post post,final AddPostListener listener){
        PostModelFireBase.addPost(post,listener);

    }

    public interface updatePostListener{
        void onComplete();
    }
    public void uploadImage(Bitmap imageBmp, String fileName, final ModelFirebase.UploadImageListener listener) {
        ModelFirebase.uploadImage(imageBmp,fileName,listener);
    }
//    public void UpdatePost(final Post post, final updatePostListener listener) {
//        PostModelFireBase.updatePost(post,listener);
//
//    }
//
//    public interface deletePostListener{
//        void onComplete();
//    }
//    public void deletePost(final Post post, final deletePostListener listener){
//        PostModelFireBase.deletePost(post,listener);
//
//    }
    public interface refreshAllStudentsListener{
    void onComplete();
    }
    public void refreshAllPost(final refreshAllStudentsListener listener) {
        //1. get local last update date
        final SharedPreferences sp = MyApplication.getAppContext().getSharedPreferences("TAG", Context.MODE_PRIVATE);
        long lastUpdated = sp.getLong("lastUpdated",0);
        //2. get all updated record from firebase from the last update date
        PostModelFireBase.getAllPosts(lastUpdated, new GetAllPostsListener() {
            @Override
            public void onComplete(List<Post> result) {
                //3. insert the new updates to the local db
                long lastU = 0;
                for (Post p : result) {
                    ModelSql.addPost(p, null);
                    if (p.getLastUpdated() > lastU) {
                        lastU = p.getLastUpdated();
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

}

