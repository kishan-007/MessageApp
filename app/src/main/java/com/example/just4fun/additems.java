package com.example.just4fun;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.Blob;

import java.sql.Time;
import java.sql.Timestamp;

public class additems {
   private String item;
    private String timestamp;
    private String uid;
    private String photourl;
    private  String username;

    public additems(){

    }
    public additems(String item, String timestamp,String uid,String photourl,String username){
        this.item=item;
        this.timestamp=timestamp;
        this.uid=uid;
        this.photourl=photourl;
        this.username=username;

    }

    public void setItem(String item){
        this.item=item;

    }
    public void setUsername(String username){
      this.username=username;

    }
    public void setPhotourl(String photourl){
        this.photourl=photourl;

    }
    public void setTimestamp(String timestamp){
        this.timestamp=timestamp;

    }
    public void setuid(String uid){
        this.uid=uid;

    }

    public String getItem(){
        return item;
    }
    public String getTimestamp(){
        return timestamp;
    }
    public String getUid(){
        return uid;
    }
    public String getPhotourl(){
        return photourl;
    }
    public String getUsername(){ return  username;}


}
