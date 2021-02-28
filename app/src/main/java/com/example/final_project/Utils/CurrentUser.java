package com.example.final_project.Utils;

import android.content.Context;
import android.util.Log;

import com.example.final_project.Classes.Recipe;
import com.example.final_project.Classes.User;

public class CurrentUser {
    private static CurrentUser userInstance;
    private User user;
    private final Context context;
    private Recipe currentRecipe;

    public static CurrentUser getCUserInstance() {
        return userInstance;
    }

    public CurrentUser(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void init(Context context){
        if(userInstance==null){
            userInstance=new CurrentUser(context);
        }
    }

    public void setUser(User user) {
        Log.d("my_tag_Current:", "user set "+user.getPhoneNumber());
        userInstance.user = user;
    }

    public User getUser(){
        return user;
    }

    public Recipe getCurrentRecipe(){
        return this.currentRecipe;
    }

    public void setCurrentRecipe(Recipe currentRecipe) {
        this.currentRecipe = currentRecipe;
    }
}
