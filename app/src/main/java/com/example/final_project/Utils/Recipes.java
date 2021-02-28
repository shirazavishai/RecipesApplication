package com.example.final_project.Utils;

import android.content.Context;
import android.util.Log;

import com.example.final_project.Classes.Recipe;
import com.example.final_project.Classes.User;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.stream.Collectors;

public class Recipes {
    private static Recipes recipesInstance;
    private final Context context;
    private HashMap<String,Recipe> recipes = new HashMap<>();

    public static Recipes getRecipesInstance() {
        return recipesInstance;
    }

    public Recipes(Context context) {
        this.context = context.getApplicationContext();
    }

    public static void init(Context context){
        if(recipesInstance==null){
            recipesInstance=new Recipes(context);
        }
    }

    public ArrayList<Recipe> getRecipes() {
        return new ArrayList<>(this.recipes.values());
    }

    public Recipe getRecipeByKey(String key){
        return this.recipes.get(key);
    }

    public void addToRecipes(Recipe re) {
        this.recipes.put(re.getKey(),re);
    }

    public void setRecipes(HashMap<String,Recipe> map){
        this.recipes = map;
    }

    public void removeRecipe(String key){
        recipes.remove(key);
    }

    public void removeAll(){
        recipes.clear();
    }
}
