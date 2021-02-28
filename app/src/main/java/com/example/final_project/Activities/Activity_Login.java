package com.example.final_project.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.final_project.R;
import com.example.final_project.Utils.Signals;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthSettings;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Activity_Login extends AppCompatActivity {
    private enum LOGIN_STATE {
        ENTERING_NUMBER,
        ENTERING_CODE,
    }

    MaterialButton login_BTN_continue;
    TextInputLayout login_EDT_phone;
    ImageView backround;

    private LOGIN_STATE login_state = LOGIN_STATE.ENTERING_NUMBER;
    private  String phoneInput = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        findViews();
        initViews();
        updateUI();
    }


    private void continueClicked() {
        if (login_state == LOGIN_STATE.ENTERING_NUMBER) {
            login_BTN_continue.setEnabled(false);
            login_BTN_continue.setTextColor(getColor(R.color.lock));
            startLoginProcess();
        } else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            login_BTN_continue.setEnabled(false);
            login_BTN_continue.setTextColor(getColor(R.color.lock));
            codeEntered();
        }
    }

    private void codeEntered() {
        String smsVerificationCode = login_EDT_phone.getEditText().getText().toString();
        Log.d("my_tag_login", "smsVerificationCode:" + smsVerificationCode);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuthSettings firebaseAuthSettings = firebaseAuth.getFirebaseAuthSettings();
        firebaseAuthSettings.setAutoRetrievedSmsCodeForPhoneNumber(phoneInput, smsVerificationCode);

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneInput)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(onVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void startLoginProcess() {
        phoneInput = login_EDT_phone.getEditText().getText().toString();
        Log.d("my_tag_login", "phoneInput:" + phoneInput);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneInput)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(onVerificationStateChangedCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks onVerificationStateChangedCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        //Send code to the user
        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            Log.d("my_tag_login", "onCodeSent: " + verificationId);
            login_state = LOGIN_STATE.ENTERING_CODE;
            updateUI();
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            Log.d("my_tag_login", "onVerificationCompleted");
            signInWithPhoneAuthCredential(phoneAuthCredential);
        }

        @Override
        public void onCodeAutoRetrievalTimeOut(@NonNull String s) {
            Log.d("my_tag_login", "onCodeAutoRetrievalTimeOut " + s);
            super.onCodeAutoRetrievalTimeOut(s);
            Signals.getInstance().myToast(Activity_Login.this,"Technical error. Enter again please");
            login_state = LOGIN_STATE.ENTERING_NUMBER;
            updateUI();
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Log.d("my_tag_login", "onVerificationFailed: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(Activity_Login.this, "VerificationFailed " + e.getMessage(), Toast.LENGTH_LONG).show();
            login_state = LOGIN_STATE.ENTERING_NUMBER;
            updateUI();
        }
    };

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("my_tag_login", "signInWithCredential:success");
                            FirebaseUser user = task.getResult().getUser();
                            userSignedIn();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("my_tag_login", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                Signals.getInstance().myToast(Activity_Login.this, "Wrong Code");
                                login_state=LOGIN_STATE.ENTERING_CODE;
                                updateUI();
                            }
                        }
                    }
                });
    }

    private void userSignedIn()
    {
        Signals.getInstance().myToast(Activity_Login.this, "Logged in");
        openMain();
    }

    private void openMain() {
        Intent intent = new Intent(Activity_Login.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateUI() {
        if (login_state == LOGIN_STATE.ENTERING_NUMBER) {
            login_BTN_continue.setEnabled(true);
            login_BTN_continue.setTextColor(Color.BLACK);
            login_EDT_phone.getEditText().setText("");
            login_EDT_phone.setHint(getString(R.string.phone_number));
            login_EDT_phone.setPlaceholderText("+972 55 11111");
            login_BTN_continue.setText(getString(R.string.continue_));
        } else if (login_state == LOGIN_STATE.ENTERING_CODE) {
            login_BTN_continue.setEnabled(true);
                login_BTN_continue.setTextColor(Color.BLACK);

            login_EDT_phone.getEditText().setText("");
            login_EDT_phone.setHint(getString(R.string.enter_code));
            login_EDT_phone.setPlaceholderText("******");
            login_BTN_continue.setText(getString(R.string.login));
        }
    }

    private void initViews() {
        login_BTN_continue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                continueClicked();
            }
        });
    }

    private void findViews() {
        login_BTN_continue = findViewById(R.id.login_BTN_continue);
        login_EDT_phone = findViewById(R.id.login_EDT_phone);
        backround = findViewById(R.id.login_IMG_back);
        Glide
                .with(this)
                .load(R.drawable.tablecloth)
                .centerCrop()
                .into(backround);
    }

    @Override
    public void onBackPressed(){
        if(login_state == LOGIN_STATE.ENTERING_NUMBER){
            super.onBackPressed();
            openMain();
        }else if(login_state == LOGIN_STATE.ENTERING_CODE){
            login_state = LOGIN_STATE.ENTERING_NUMBER;
            updateUI();
        }
    }
    @Override
    protected void onResume() {
        Log.d("my_tag_login:", "onResume");
        super.onResume();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar == null)
            throw new AssertionError();
        else actionBar.hide();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("my_tag_login:", "onDestroy");
    }
}
