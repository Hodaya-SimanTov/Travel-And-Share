package com.hodayaandkineret.travelandshare.ui.MyTrips;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hodayaandkineret.travelandshare.Model.Model;
import com.hodayaandkineret.travelandshare.Model.Post;

import java.util.List;

public class myPostListViewModel extends ViewModel {
    private LiveData<List<Post>> postList;
    FirebaseUser muser= FirebaseAuth.getInstance().getCurrentUser();
    public myPostListViewModel(){
        postList= Model.instance.getAllUserPosts(muser.getUid());
    }
    LiveData<List<Post>>getMyList(){
        return postList;
    }

}
