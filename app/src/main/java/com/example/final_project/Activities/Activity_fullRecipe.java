package com.example.final_project.Activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.final_project.Classes.Recipe;
import com.example.final_project.R;
import com.example.final_project.Utils.CurrentUser;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

public class Activity_fullRecipe extends AppCompatActivity {
    TextView title;
    TextView description;
    TextView groceries;
    TextView recipe;
    ShapeableImageView imageView;
    MaterialTextView date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_recipe);
        Recipe recipeToShow = CurrentUser.getCUserInstance().getCurrentRecipe();
        findViews(recipeToShow);

    }

    private void findViews(Recipe recipeToShow) {
        title = findViewById(R.id.full_LBL_title);
        title.setText(recipeToShow.getTitle());
        description = findViewById(R.id.full_LBL_description);
        description.setText(recipeToShow.getDescription());
        groceries = findViewById(R.id.full_LBL_groceries);
        groceries.setText(recipeToShow.groceriesToString());
        recipe = findViewById(R.id.full_LBL_recipe);
        recipe.setText(recipeToShow.getRecipe());
        imageView = findViewById(R.id.full_IMG_image);
        date = findViewById(R.id.full_LBL_date);
        date.setText(recipeToShow.getDate());
        if(recipeToShow.getImage()!=null) {
            Picasso.with(getApplicationContext())
                    .load(recipeToShow.getImage().getmImageUrl())
                    .fit()
                    .noPlaceholder()
                    .centerCrop()
                    .into(imageView);
        }
    }


    @Override
    protected void onResume() {
        Log.d("my_tag_fullRecipe:", "onResume");
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