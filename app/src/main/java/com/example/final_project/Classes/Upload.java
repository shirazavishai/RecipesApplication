package com.example.final_project.Classes;

import android.net.Uri;

import com.google.android.gms.tasks.Task;

public class Upload {
    private String mName;
    private String mImageUrl;
    //private Task<Uri> imageUri;

    public Upload(){}

    public Upload(String name, String imageUrl){
        if(name.trim().equals("")){
            name = "No name";
        }
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

//    public void setImageUri(Task<Uri> imageUri) {
//        this.imageUri = imageUri;
//    }
//
//    public Task<Uri> getImageUri() {
//        return imageUri;
//    }
}