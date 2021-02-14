package com.hodayaandkineret.travelandshare.Model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface PostDao {
    @Query("select * from Post")
    LiveData<List<Post>> getAllPosts();
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Post... posts);
}
