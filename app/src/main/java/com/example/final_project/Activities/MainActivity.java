package com.example.final_project.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.final_project.Classes.Adapter_Post;
import com.example.final_project.Classes.Recipe;
import com.example.final_project.R;
import com.example.final_project.Classes.User;
import com.example.final_project.Utils.CurrentUser;
import com.example.final_project.Utils.Recipes;
import com.example.final_project.Utils.Signals;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MaterialButton main_BTN_login;
    private MaterialButton main_BTN_logout;
    private MaterialButton main_BTN_search;
    private ImageButton main_BTN_userPage;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("my_tag_main:", "onCreate main");
        findViews();
        initViews();
        getRecipes();
        validateUser();
    }

    private void validateUser() {
        Log.d("my_tag_main:", "validate user");
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser == null) {
            Log.d("my_tag_main:", "no user in");
            main_BTN_logout.setEnabled(false);
            main_BTN_logout.setTextColor(getColor(R.color.lock));
        }else{
            main_BTN_login.setEnabled(false);
            main_BTN_login.setTextColor(getColor(R.color.lock));
            readUserData(firebaseUser);
        }
    }

    private void readUserData(FirebaseUser firebaseUser) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference userRootRef = firebaseDatabase.getReference("users");
        userRootRef.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.getValue() == null) {
                    Log.d("my_tag_main:", "adding user to Database");
                    user = new User(firebaseUser.getUid(),"Chef",new ArrayList<>(),firebaseUser.getPhoneNumber());
                    userRootRef.child(firebaseUser.getUid()).setValue(user);
                }else{
                    User val = snapshot.getValue(User.class);
                    user = new User(val.getId(),"Chef",val.getRecipes(),val.getPhoneNumber());
                    Log.d("my_tag_main:", "user updated"+user.getPhoneNumber());
                }
                CurrentUser.getCUserInstance().setUser(user);
                main_BTN_userPage.setVisibility(View.VISIBLE);
                Log.d("my_tag_main:", "loaded to CurrentUser");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("my_tag_main:", "Read user Failed", error.toException());
            }
        });
    }

    private void getRecipes() {
        Log.d("my_tag_main:", "start get recipes");
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference recipesRootRef = firebaseDatabase.getReference("recipes");
        recipesRootRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("my_tag_main:", "On data change recipes");
                Recipes.getRecipesInstance().removeAll();
                for(DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Recipe recipe = dataSnapshot.getValue(Recipe.class);
                    Recipes.getRecipesInstance().addToRecipes(recipe);
                }
                initPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("my_tag_main:", "Read recipes Failed", error.toException());
            }
        });
    }

    private void initPosts() {
        RecyclerView main_LST_posts = findViewById(R.id.main_LST_posts);
        main_LST_posts.setLayoutManager(new LinearLayoutManager(this));
        if(Recipes.getRecipesInstance().getRecipes().size()==0) {
            showFirst(main_LST_posts);
            return;
        }
        Log.d("my_tag_main:", "init posts");
        Adapter_Post adapter_post = new Adapter_Post(this,Recipes.getRecipesInstance().getRecipes());
        adapter_post.setItemClickListener(new Adapter_Post.ItemClickListener() {

            @Override
            public void onFullRecipeClick(int position) {
                Recipe temp = Recipes.getRecipesInstance().getRecipes().get(position);
                CurrentUser.getCUserInstance().setCurrentRecipe(temp);
                Intent intent = new Intent(MainActivity.this,Activity_fullRecipe.class);
                startActivity(intent);
            }
        });
        main_LST_posts.setAdapter(adapter_post);
    }

    private void showFirst(RecyclerView main_LST_posts) {
        Log.d("my_tag_main:", "init posts : Recipes empty");
        Recipe first = new Recipe("Be the first!","Enter your recipe and share everyone");
        ArrayList<Recipe> tempList = new ArrayList<>();
        tempList.add(first);
        Adapter_Post adapter_post = new Adapter_Post(this,tempList);
        adapter_post.setItemClickListener(new Adapter_Post.ItemClickListener() {

            @Override
            public void onFullRecipeClick(int position) {
                Signals.getInstance().myToast(MainActivity.this,"Open your recipe");
            }
        });
        main_LST_posts.setAdapter(adapter_post);
    }

    private void initViews() {
        Log.d("my_tag_main:", "initViews");
        main_BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("my_tag_main:", "login pressed");
                main_BTN_login.setEnabled(false);
                main_BTN_login.setTextColor(getColor(R.color.lock));
                main_BTN_logout.setEnabled(true);
                main_BTN_logout.setTextColor(Color.BLACK);
                Intent myIntent = new Intent(MainActivity.this, Activity_Login.class);
                startActivity(myIntent);
                finish();
                return;
            }
        });
        main_BTN_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("my_tag_main:", "logout pressed");
                main_BTN_login.setEnabled(true);
                main_BTN_login.setTextColor(Color.BLACK);
                main_BTN_logout.setEnabled(false);
                main_BTN_logout.setTextColor(getColor(R.color.lock));
                main_BTN_userPage.setVisibility(View.INVISIBLE);
                user=null;
                FirebaseAuth.getInstance().signOut();
            }
        });
        main_BTN_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("my_tag_main:", "search pressed");
                Intent myIntent = new Intent(MainActivity.this, Activity_Search.class);
                startActivity(myIntent);
            }
        });
        main_BTN_userPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("my_tag_main:", "user pressed");
                Intent myIntent = new Intent(MainActivity.this,Activity_UserPage.class);
                startActivity(myIntent);
            }
        });
    }


    private void findViews() {
        main_BTN_login = findViewById(R.id.main_BTN_login);
        main_BTN_logout = findViewById(R.id.main_BTN_logout);
        main_BTN_search = findViewById(R.id.main_BTN_search);
        ImageView background = findViewById(R.id.main_IMG_back);
        main_BTN_userPage = findViewById(R.id.main_BTN_user);
        main_BTN_userPage.setVisibility(View.INVISIBLE);
        Glide
                .with(this)
                .load(R.drawable.tablecloth)
                .centerCrop()
                .into(background);
    }

    @Override
    protected void onResume() {
        Log.d("my_tag_main:", "onResume");
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        else actionBar.hide();
        initPosts();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("my_tag_main:", "onDestroy");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("my_tag_main:", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("my_tag_main:", "onStop");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("my_tag_main:", "onStart");
    }
}
