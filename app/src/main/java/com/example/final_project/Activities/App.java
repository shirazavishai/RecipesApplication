package com.example.final_project.Activities;

import android.app.Application;

import com.example.final_project.Utils.Recipes;
import com.example.final_project.Utils.Signals;
import com.example.final_project.Utils.CurrentUser;

public class App extends Application {
    @Override
    public void onCreate(){
        super.onCreate();

        Signals.init(this);
        CurrentUser.init(this);
        Recipes.init(this);
    }
}
