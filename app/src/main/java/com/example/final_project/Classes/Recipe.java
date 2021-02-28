package com.example.final_project.Classes;

import java.util.ArrayList;

public class Recipe {
    private String key;
    private String title;
    private String description;
    private ArrayList<String> groceries;
    private ArrayList<String> keyWord;
    private String recipe;
    private Upload image;
    private String date;

    public Recipe(){}

    public Recipe(String key,String title, String description, ArrayList<String> groceries,ArrayList<String> keyWord,String recipe, Upload picture){
        this.key=key;
        this.title=title;
        this.description=description;
        this.groceries=groceries;
        this.keyWord = keyWord;
        this.recipe=recipe;
        this.image=picture;
    }

    public Recipe(String title, String description, ArrayList<String> groceries, ArrayList<String> keyWord, String recipe) {
        this.title = title;
        this.description = description;
        this.groceries = groceries;
        this.keyWord = keyWord;
        this.recipe = recipe;
    }

    public Recipe(String title,String description){
        this.title=title;
        this.description=description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public ArrayList<String> getGroceries() {
        return groceries;
    }

    public ArrayList<String> getKeyWord() {
        return keyWord;
    }

    public String getRecipe() {
        return recipe;
    }

    public Upload getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setGroceries(ArrayList<String> groceries) {
        this.groceries = groceries;
    }

    public void setKeyWord(ArrayList<String> keyWord) {
        this.keyWord = keyWord;
    }

    public void setRecipe(String recipe) {
        this.recipe = recipe;
    }

    public void setImage(Upload image) {
        this.image = image;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String groceriesToString(){
        String res = "";
        for(String str:groceries){
            res += str +",";
        }
        return res;
    }

    public String keyWordsToString(){
        String res = "";
        for(String str:keyWord){
            res += str +",";
        }
        res=res.substring(0,res.length()-1);
        return res;
    }
}
