package com.hodayaandkineret.travelandshare.Model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.hodayaandkineret.travelandshare.MyApplication;

@Database(entities = {Post.class}, version = 1)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract PostDao postDao();
}
public class AppLocalDB {
    static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.getAppContext(),
                    AppLocalDbRepository.class,
                    "dbFileName.db")
                    .fallbackToDestructiveMigration()
                    .build();
}