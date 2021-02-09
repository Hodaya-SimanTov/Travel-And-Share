package com.hodayaandkineret.travelandshare.Model;

import java.util.List;

public class Model {
    Model instance = new Model();

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



}
