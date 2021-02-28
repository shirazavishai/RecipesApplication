package com.example.final_project.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.final_project.Classes.Adapter_UserPosts;
import com.example.final_project.Classes.Recipe;
import com.example.final_project.R;
import com.example.final_project.Utils.Recipes;
import com.example.final_project.Utils.Signals;
import com.example.final_project.Classes.User;
import com.example.final_project.Utils.CurrentUser;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Activity_UserPage extends AppCompatActivity {

    private ImageView background;
    private MaterialButton user_BTN_add;
    protected User currentUser;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        Log.d("my_tag_userPage:","onCreate user page");

        currentUser = CurrentUser.getCUserInstance().getUser();
        //Log.d("my_tag_userPage:","current user: "+currentUser.getPhoneNumber()+currentUser.getId());

        findViews();
        initViews();
    }

    private void loadRecipes() {
        if(currentUser.getRecipes().size()==0) {
            Log.d("my_tag_user:","there are no recipes to load");
            Signals.getInstance().myToast(Activity_UserPage.this, "No recipes yet");
        }else{
            Log.d("my_tag_user:","load recipes");
            ArrayList<Recipe> adapterRecipes = new ArrayList<>();
            for(String key:currentUser.getRecipes()){
                if(key!=null) {
                    adapterRecipes.add(Recipes.getRecipesInstance().getRecipeByKey(key));
                    Log.d("my_tag_user:", "add recipe ");
                }
            }
            recyclerView = findViewById(R.id.user_LST_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            Adapter_UserPosts adapterUserPosts = new Adapter_UserPosts(this,adapterRecipes);
            adapterUserPosts.setItemClickListener(new Adapter_UserPosts.UserItemsClickListener() {
                @Override
                public void onFullRecipeClick(int position) {
                    CurrentUser.getCUserInstance().setCurrentRecipe(adapterRecipes.get(position));
                    Intent intent = new Intent(Activity_UserPage.this,Activity_fullRecipe.class);
                    startActivity(intent);
                }

                @Override
                public void onEditRecipeClick(int position) {
                    CurrentUser.getCUserInstance().setCurrentRecipe(adapterRecipes.get(position));
                    Intent myIntent = new Intent(Activity_UserPage.this,Activity_CreateRecipe.class);
                    myIntent.putExtra(Activity_CreateRecipe.EXTRA_MODE,0);
                    startActivity(myIntent);
                }

                @Override
                public void onRemoveRecipeClick(int position) {
                    Recipe temp = adapterRecipes.get(position);
                    currentUser.removeRecipe(temp.getKey());
                    Recipes.getRecipesInstance().removeRecipe(temp.getKey());
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference recipeRootRef = database.getReference("recipes");
                    recipeRootRef.child(temp.getKey()).setValue(null);

                    DatabaseReference recipeRootUsers = database.getReference("users");
                    recipeRootUsers.child(currentUser.getId()).child("recipes").setValue(currentUser.getRecipes());
                    Log.d("my_tag_userPage:", "recipe removed");
                    finish();
                }
            });
            recyclerView.setAdapter(adapterUserPosts);
        }
    }

    private void initViews() {
        user_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Activity_UserPage.this,Activity_CreateRecipe.class);
                myIntent.putExtra(Activity_CreateRecipe.EXTRA_MODE,1);
                startActivity(myIntent);
            }
        });
    }


    private void findViews() {
        user_BTN_add = findViewById(R.id.user_BTN_add);
        background = findViewById(R.id.user_IMG_back);
        Glide
                .with(this)
                .load(R.drawable.tablecloth)
                .centerCrop()
                .into(background);
    }


    @Override
    protected void onResume() {
        Log.d("my_tag_userPage:", "onResume");
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        else actionBar.hide();
        loadRecipes();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("my_tag_userPage:", "onDestroy");
    }
}