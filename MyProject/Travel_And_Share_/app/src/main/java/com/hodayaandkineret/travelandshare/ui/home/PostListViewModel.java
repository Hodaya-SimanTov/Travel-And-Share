package com.hodayaandkineret.travelandshare.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.hodayaandkineret.travelandshare.Model.Model;
import com.hodayaandkineret.travelandshare.Model.Post;

import java.util.List;

public class PostListViewModel extends ViewModel {
    private LiveData<List<Post>>postList;
    public PostListViewModel(){
        postList= Model.instance.getAllPosts();
    }
    LiveData<List<Post>>getList(){return postList;}

}
