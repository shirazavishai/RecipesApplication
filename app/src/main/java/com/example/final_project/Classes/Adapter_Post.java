package com.example.final_project.Classes;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.final_project.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Adapter_Post extends RecyclerView.Adapter<Adapter_Post.MyViewHolder>{

    private ArrayList<Recipe> posts;
    private LayoutInflater inflater;
    private ItemClickListener myItemClickListener;
    private Context mContext;

    public Adapter_Post(Context context, ArrayList<Recipe> data){
        this.inflater = LayoutInflater.from(context);
        this.posts = data;
        this.mContext=context;
    }

    @NonNull
    @Override
    public Adapter_Post.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("my_tag_Adapter_Post:", "onCreateViewHolder");
        View view = inflater.inflate(R.layout.post_view,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Post.MyViewHolder holder, int position) {
        Log.d("my_tag_Adapter_Post:", "onBindViewHolder");
        Recipe post = posts.get(position);
        holder.post_LBL_title.setText(post.getTitle());
        holder.post_LBL_description.setText(post.getDescription());
        holder.post_LBL_date.setText(post.getDate());
        if(post.getImage()!=null) {
            Picasso.with(mContext)
                    .load(post.getImage().getmImageUrl())
                    .fit()
                    .noPlaceholder()
                    .centerCrop()
                    .into(holder.post_IMG_image);
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setItemClickListener(ItemClickListener itemClickListener){
        this.myItemClickListener = itemClickListener;
    }

    public interface ItemClickListener{
        void onFullRecipeClick(int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        MaterialTextView post_LBL_title;
        MaterialTextView post_LBL_description;
        MaterialButton post_BTN_full;
        ShapeableImageView post_IMG_image;
        MaterialTextView post_LBL_date;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d("my_tag_Adapter_Post:", "My View Holder");
            post_LBL_title = itemView.findViewById(R.id.post_LBL_title);
            post_LBL_description = itemView.findViewById(R.id.post_LBL_description);
            post_BTN_full = itemView.findViewById(R.id.post_BTN_full);
            post_IMG_image = itemView.findViewById(R.id.post_IMG_image);
            post_LBL_date = itemView.findViewById(R.id.post_LBL_date);

            post_BTN_full.setOnClickListener(new View.OnClickListener() {
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
