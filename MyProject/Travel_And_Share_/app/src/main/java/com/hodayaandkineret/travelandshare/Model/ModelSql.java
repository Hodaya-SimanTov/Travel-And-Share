package com.hodayaandkineret.travelandshare.Model;

import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ModelSql {
    public LiveData<List<Post>> getAllPosts(){
        return AppLocalDB.db.postDao().getAllPosts();
    }

    public static void addPost(Post post,final Model.AddPostListener listener) {
        class MyAsynchTask extends AsyncTask {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppLocalDB.db.postDao().insertAll(post);
                return null;
            }

            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);
                if (listener != null) {
                    listener.onComplete(true);
                }
            }
        }
        MyAsynchTask task = new MyAsynchTask();
        task.execute();

    }
}
