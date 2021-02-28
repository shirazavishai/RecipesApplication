package com.example.final_project.Classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

public class Adapter_UserPosts extends RecyclerView.Adapter<Adapter_UserPosts.MyViewHolder>{

    private ArrayList<Recipe> user_posts;
    private LayoutInflater inflater;
    private UserItemsClickListener myItemClickListener;

    public Adapter_UserPosts(Context context, ArrayList<Recipe> data){
        this.inflater = LayoutInflater.from(context);
        this.user_posts = data;
    }

    @NonNull
    @Override
    public Adapter_UserPosts.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("my_tag_Adapter_user:", "onCreateViewHolder");
        View view = inflater.inflate(R.layout.user_post_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_UserPosts.MyViewHolder holder, int position) {
        Log.d("my_tag_Adapter_user:", "onBindViewHolder");
        Recipe post = user_posts.get(position);
        holder.userPost_LBL_title.setText(post.getTitle());
        holder.userPost_BTN_date.setText(post.getDate());
    }

    @Override
    public int getItemCount() {
        return user_posts.size();
    }

    public void setItemClickListener(UserItemsClickListener itemClickListener){
        this.myItemClickListener = itemClickListener;
    }

    public interface UserItemsClickListener{
        void onFullRecipeClick(int position);
        void onEditRecipeClick(int position);
        void onRemoveRecipeClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        MaterialTextView userPost_LBL_title;
        MaterialButton userPost_BTN_full;
        MaterialButton userPost_BTN_edit;
        MaterialButton userPost_BTN_remove;
        MaterialTextView userPost_BTN_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("my_tag_Adapter_user:", "MyViewHolder");
            userPost_LBL_title = itemView.findViewById(R.id.userPost_LBL_title);
            userPost_BTN_full = itemView.findViewById(R.id.userPost_BTN_full);
            userPost_BTN_edit = itemView.findViewById(R.id.userPost_BTN_edit);
            userPost_BTN_remove = itemView.findViewById(R.id.userPost_BTN_remove);
            userPost_BTN_date = itemView.findViewById(R.id.userPost_LBL_date);

            userPost_BTN_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myItemClickListener != null){
                        myItemClickListener.onEditRecipeClick(getAdapterPosition());
                    }
                }
            });

            userPost_BTN_remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myItemClickListener != null){
                        myItemClickListener.onRemoveRecipeClick(getAdapterPosition());
                    }
                }
            });

            userPost_BTN_full.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(myItemClickListener != null){
                        myItemClickListener.onFullRecipeClick(getAdapterPosition());
                    }
                }
            });
        }
    }
}
