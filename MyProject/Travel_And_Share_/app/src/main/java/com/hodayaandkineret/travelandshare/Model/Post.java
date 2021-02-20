package com.hodayaandkineret.travelandshare.Model;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;
@Entity
public class Post implements Parcelable {

    public boolean IsDelete;
    @PrimaryKey
    @NonNull
    private String id;
    private String name;
    private String cost;
    private String location;
    private boolean forFamilies;
    private boolean forBenefactors;
    private boolean Accessible;
    private String imageUrl;
    private String openText;
    private String ownerUid;
    private Long lastUpdated;




    public Post() {

    }
    public boolean isDelete() {
        return IsDelete;
    }

    public void setDelete(boolean delete) {
        IsDelete = delete;
    }
    protected Post(Parcel in) {
        id = in.readString();
        name = in.readString();
        cost = in.readString();
        location = in.readString();
        IsDelete = in.readByte() != 0;
        forFamilies = in.readByte() != 0;
        forBenefactors = in.readByte() != 0;
        Accessible = in.readByte() != 0;
        imageUrl = in.readString();
        openText = in.readString();
        ownerUid = in.readString();
        if (in.readByte() == 0) {
            lastUpdated = null;
        } else {
            lastUpdated = in.readLong();
        }
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    public boolean isAccessible() {
        return Accessible;
    }

    public void setAccessible(boolean accessible) {
        Accessible = accessible;
    }
    public boolean isForBenefactors() {
        return forBenefactors;
    }

    public void setForBenefactors(boolean forBenefactors) {
        this.forBenefactors = forBenefactors;
    }

    public boolean isForFamilies() {
        return forFamilies;
    }

    public void setForFamilies(boolean forFamilies) {
        this.forFamilies = forFamilies;
    }

    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl( String imageUrl) {
        this.imageUrl = imageUrl;
    }
    public String getName() {
        return name;
    }



    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOpenText() {
        return openText;
    }

    public void setOpenText(String openText) {
        this.openText = openText;
    }

    public String getOwnerUid() {
        return ownerUid;
    }

    public void setOwnerUid(String ownerUid) {
        this.ownerUid = ownerUid;
    }
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("cost", cost);
        result.put("location", location);
        result.put("forFamilies", forFamilies);
        result.put("forBenefactors",forBenefactors);
        result.put("Accessible",Accessible);
        result.put("imageUrl",imageUrl);
        result.put("openText",openText);
        result.put("ownerUid",ownerUid);
        result.put("isDelete",IsDelete);
        result.put("lastUpdated", FieldValue.serverTimestamp());
        return result;
    }
    public void fromMap(Map<String, Object> map){
        id=(String)map.get("id");
        name = (String)map.get("name");
        cost=(String)map.get("cost");
        location=(String)map.get("location");
        forFamilies=(Boolean) map.get("forFamilies");
        forBenefactors=(Boolean)map.get("forBenefactors");
        Accessible=(Boolean)map.get("Accessible");
        imageUrl=(String)map.get("imageUrl");
        openText=(String)map.get("openText");
        ownerUid=(String)map.get("ownerUid");
        Timestamp ts = (Timestamp)map.get("lastUpdated");
        lastUpdated = ts.getSeconds();
        IsDelete=(Boolean)map.get("isDelete");
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
