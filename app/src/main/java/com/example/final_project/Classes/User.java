package com.example.final_project.Classes;

import android.util.Log;

import java.util.ArrayList;

public class User {
    private String id;
    private String name;
    private ArrayList<String> recipes=new ArrayList<>();
    private String phoneNumber;

    public User(String id, String name, ArrayList<String> recipes, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.recipes = recipes;
        this.phoneNumber = phoneNumber;
    }

    public User(String id, String phoneNumber) {
        this.id = id;
        this.phoneNumber = phoneNumber;
    }

    public User(String id, String phoneNumber,ArrayList<String> recipes) {
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.recipes = recipes;
    }

    public User() {
    }

    public void setId(String id) { /////////////////////////// ????????????????
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addToRecipes(String recipe) {
        this.recipes.add(recipe);
        Log.d("my_tag_userClass:", "added"+ recipes.size());
    }

    public void setRecipes(ArrayList<String> recipes) {
        this.recipes = recipes;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getRecipes() {
        return recipes;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void removeRecipe(String key){
        recipes.remove(key);
        recipes.remove(null);
        Log.d("my_tag_userClass:", "removed"+ recipes.size());
    }

}
