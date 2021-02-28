package com.example.final_project.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.example.final_project.Classes.Upload;
import com.example.final_project.R;
import com.example.final_project.Classes.Recipe;
import com.example.final_project.Classes.User;
import com.example.final_project.Utils.CurrentUser;
import com.example.final_project.Utils.Recipes;
import com.example.final_project.Utils.Signals;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class Activity_CreateRecipe extends AppCompatActivity {

    protected User currentUser;

    private TextInputLayout create_EDT_title;
    private TextInputLayout create_EDT_description;
    private TextInputLayout create_EDT_keyWords;
    private TextInputLayout create_EDT_groceries;
    private TextInputLayout create_EDT_recipe;
    private ImageView create_IMG_image;
    private ProgressBar create_PRG_load;
    private MaterialButton create_BTN_choose_image;
    private MaterialButton create_BTN_upload_image;
    private MaterialButton create_BTN_load;
    private ImageView create_IMG_back;

    private Uri imageUri;
    private Upload upload;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private StorageTask storageTask;

    private static final int PICK_IMAGE_REQUEST =1;


    private enum ACTION_STATE {
        EDIT,
        ADD
    }

    public static final String EXTRA_MODE = "GET_EXTRA_MODE";
    private ACTION_STATE modeState;

    private Recipe recipe;
    private boolean input = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_recipe);

        findViews();
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
        checkMode();
        currentUser = CurrentUser.getCUserInstance().getUser();
        Log.d("my_tag_createRecipe:", "current user: " + currentUser.getPhoneNumber() + currentUser.getId());
        initViews();
    }

    private void checkMode() {
        int mode = this.getIntent().getIntExtra(String.valueOf(EXTRA_MODE), -1);
        if (mode == 0) {
            modeState = ACTION_STATE.EDIT;
            fillEditsText();
        } else if (mode == 1)
            modeState = ACTION_STATE.ADD;
    }

    private void initViews() {
        create_BTN_choose_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        create_BTN_upload_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(storageTask != null && storageTask.isInProgress()){
                    Signals.getInstance().myToast(Activity_CreateRecipe.this,"Upload in progress");
                }else {
                    uploadFile();
                }
            }
        });

        create_BTN_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getInputRecipe()) {
                    if (modeState == ACTION_STATE.ADD) {
                        addRecipe();
                    }else updateRecipe();
                }
            }
        });
    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if(imageUri != null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()
            +"."+getFileExtension(imageUri));
            storageTask = fileReference.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final String downloadUrl = uri.toString();
                                    String titleName;
                                    do{
                                        titleName = create_EDT_title.getEditText().getText().toString();
                                    }while(titleName==null);
                                    upload = new Upload(titleName.trim(),downloadUrl);
                                    String uploadId = databaseReference.push().getKey();
                                    databaseReference.child("uploads").child(uploadId).setValue(upload);
                                    databaseReference.child("uploads").child(uploadId).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if(modeState==ACTION_STATE.EDIT){
                                                if(CurrentUser.getCUserInstance().getCurrentRecipe().getImage()!=null) {
                                                    deleteFromStorage(recipe.getImage());
                                                    CurrentUser.getCUserInstance().getCurrentRecipe().setImage(upload);
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }
                            });
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    create_PRG_load.setProgress(0);
                                }
                            },500);
                            Signals.getInstance().myToast(Activity_CreateRecipe.this,"Upload successful");
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Signals.getInstance().myToast(Activity_CreateRecipe.this,e.getMessage());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress = (100.0 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            create_PRG_load.setProgress((int)progress);
                        }
                    });

        }else{
            Signals.getInstance().myToast(this,"No file selected");
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            Picasso.with(this).load(imageUri).into(create_IMG_image);
        }
    }

    private void fillEditsText() {
        recipe = CurrentUser.getCUserInstance().getCurrentRecipe();
        create_EDT_title.getEditText().setText(recipe.getTitle());
        create_EDT_description.getEditText().setText(recipe.getDescription());
        create_EDT_recipe.getEditText().setText(recipe.getRecipe());
        create_EDT_keyWords.getEditText().setText(recipe.keyWordsToString());
        create_EDT_groceries.getEditText().setText(recipe.groceriesToString());
        if(recipe.getImage()!=null) {
            Picasso.with(getApplicationContext())
                    .load(recipe.getImage().getmImageUrl())
                    .fit()
                    .noPlaceholder()
                    .centerCrop()
                    .into(create_IMG_image);
        }
    }

    private void deleteFromStorage(Upload image) {
        FirebaseStorage firebaseStorage=FirebaseStorage.getInstance();
        StorageReference photoRef = firebaseStorage.getReferenceFromUrl(image.getmImageUrl());
        photoRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("my_tag_create", "onSuccess: deleted file");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.d("my_tag_create", "onFailure: did not delete file");
            }
        });
    }

    private boolean getInputRecipe() {
        if (modeState == ACTION_STATE.ADD) {
            recipe = new Recipe();
        }
        String title = create_EDT_title.getEditText().getText().toString();
        String description = create_EDT_description.getEditText().getText().toString();
        String fullRecipe = create_EDT_recipe.getEditText().getText().toString();
        String keyWords = create_EDT_keyWords.getEditText().getText().toString();
        String groceries = create_EDT_groceries.getEditText().getText().toString();
        if (!title.equals("") && !description.equals("") && !fullRecipe.equals("") && !keyWords.equals("") && !groceries.equals("")) {
            Log.d("my_tag_createRecipe:", "all the fields are full. Create recipe");
            recipe.setTitle(title);
            recipe.setDescription(description);
            recipe.setRecipe(fullRecipe);
            recipe.setGroceries(splitMyStrings(groceries));
            recipe.setKeyWord(splitMyStrings(keyWords));
            String date = DateFormat.format("dd.MM.yy HH:mm", System.currentTimeMillis() + (1000 * 60 * 60 * 24)).toString();
            recipe.setDate(date);
            return true;
        } else {
            Log.d("my_tag_createRecipe:", "empty fields");
            Signals.getInstance().myToast(Activity_CreateRecipe.this, "Must fill all fields!");
        }
        return false;
    }

    private ArrayList<String> splitMyStrings(String groceries) {
        ArrayList<String> list = new ArrayList<>();
        for (String str : groceries.split(",")) {
            Log.d("my_tag_createRecipe:", "split str to: " + str);
            str=str.replaceAll(",\n","");
            str=str.trim();
            list.add(str);
        }
        return list;
    }

    private void addRecipe() {
        Log.d("my_tag_createRecipe:", "addRecipe start");
        if(upload!=null){
            recipe.setImage(upload);
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRootRef = database.getReference("recipes");
        String recipeKey = recipeRootRef.push().getKey();
        recipe.setKey(recipeKey);
        recipeRootRef.child(recipeKey).setValue(recipe);
        Signals.getInstance().myToast(Activity_CreateRecipe.this, "Recipe add!");

        DatabaseReference recipeRootUsers = database.getReference("users");
        currentUser.addToRecipes(recipeKey);
        recipeRootUsers.child(currentUser.getId()).child("recipes").setValue(currentUser.getRecipes());

        recipeRootRef.child(recipeKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    recipe = snapshot.getValue(Recipe.class);
                    Recipes.getRecipesInstance().addToRecipes(recipe);
                    Log.d("my_tag_createRecipe:", "recipe updated");
                    Signals.getInstance().myToast(Activity_CreateRecipe.this, "Recipe update!");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("my_tag_createRecipe:", "Failed read update" + error.toException());
            }
        });

        finishHere();
    }

    private void updateRecipe() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference recipeRootRef = database.getReference("recipes");
        recipeRootRef.child(recipe.getKey()).setValue(recipe);
        finishHere();
    }

    private void finishHere() {
        Log.d("my_tag_createRecipe:", "finish");
        finish();
    }

    private void findViews() {
        create_EDT_title = findViewById(R.id.create_EDT_title);
        create_EDT_description = findViewById(R.id.create_EDT_description);
        create_EDT_keyWords = findViewById(R.id.create_EDT_keyWords);
        create_EDT_groceries = findViewById(R.id.create_EDT_groceries);
        create_EDT_recipe = findViewById(R.id.create_EDT_recipe);
        create_IMG_image= findViewById(R.id.create_IMG_image);
        create_BTN_choose_image = findViewById(R.id.create_BTN_choose_image);
        create_PRG_load = findViewById(R.id.create_PRG_load);
        create_BTN_upload_image = findViewById(R.id.create_BTN_upload_image);
        create_BTN_load = findViewById(R.id.create_BTN_load);
        create_IMG_back = findViewById(R.id.create_IMG_back);
        Glide
                .with(this)
                .load(R.drawable.tablecloth)
                .centerCrop()
                .into(create_IMG_back);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("my_tag_createRecipe:", "onDestroy");
    }
}