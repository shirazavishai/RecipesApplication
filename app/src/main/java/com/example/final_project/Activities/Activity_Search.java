package com.example.final_project.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project.Classes.Adapter_Post;
import com.example.final_project.Classes.Recipe;
import com.example.final_project.R;
import com.example.final_project.Utils.CurrentUser;
import com.example.final_project.Utils.Recipes;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class Activity_Search extends AppCompatActivity {

    MaterialButton search_BTN_add;
    MaterialButton search_BTN_clear;
    TextInputLayout search_EDT_keyword;
    MaterialTextView search_LBL_groceries;
    ImageView background;
    ArrayList<String> keyWords;
    ArrayList<Recipe> adapterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        keyWords = new ArrayList<>();
        adapterList = new ArrayList<>();
        findViews();
        initViews();

    }


    private void initPosts() {
        Log.d("my_tag_search:", "init posts");
        RecyclerView recyclerView = findViewById(R.id.search_LST_posts);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        Adapter_Post adapter_post = new Adapter_Post(this,adapterList);
        adapter_post.setItemClickListener(new Adapter_Post.ItemClickListener() {

            @Override
            public void onFullRecipeClick(int position) {
                Recipe temp = adapterList.get(position);
                CurrentUser.getCUserInstance().setCurrentRecipe(temp);
                Intent intent = new Intent(Activity_Search.this,Activity_fullRecipe.class);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter_post);
    }

    private void initViews() {
        search_BTN_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp = search_EDT_keyword.getEditText().getText().toString();
                search_EDT_keyword.getEditText().setText("");
                keyWords.add(temp);
                search_LBL_groceries.setText(search_LBL_groceries.getText()+" "+temp);
                doSearch(temp);
            }
        });
        search_BTN_clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyWords.clear();
                search_LBL_groceries.setText("");
                adapterList.clear();
            }
        });
    }

    private void doSearch(String str) {
        str.replaceAll(", .","");
        for(Recipe r : Recipes.getRecipesInstance().getRecipes()){
            if(r.getKeyWord().contains(str) && !adapterList.contains(r))
                adapterList.add(r);
        }
        if(adapterList.size()>0)
            initPosts();
    }

    private void findViews() {
        search_BTN_add = findViewById(R.id.search_BTN_search);
        search_BTN_clear = findViewById(R.id.search_BTN_clear);
        search_EDT_keyword = findViewById(R.id.search_EDT_keyword);
        search_LBL_groceries = findViewById(R.id.search_LBL_groceries);
        background = findViewById(R.id.search_IMG_back);
        Glide
                .with(this)
                .load(R.drawable.tablecloth)
                .centerCrop()
                .into(background);
    }

    @Override
    protected void onResume() {
        Log.d("my_tag_search:", "onResume");
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        else actionBar.hide();
    }
}
