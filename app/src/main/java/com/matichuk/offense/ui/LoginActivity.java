package com.matichuk.offense.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.matichuk.offense.R;
import com.matichuk.offense.model.User;

import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_IMAGE;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_MAIL;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_NAME;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_PASS;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_PHONE;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_VISIBLE_NAME;
import static com.matichuk.offense.utils.Const.APP_PREFERENCES_VISIBLE_PHONE;

public class LoginActivity extends AppCompatActivity {

    private AppCompatEditText inputPhone, inputPassword;
    private TextInputLayout inputLayoutPhone, inputLayoutPassword;
    private Button btnSignUp, btnLogin;
    private ProgressBar progressBar;
    private FrameLayout frame;
    private SharedPreferences mSettings;
    private Intent main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputLayoutPhone = findViewById(R.id.input_layout_phone);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputPhone = findViewById(R.id.edt_phone);
        inputPassword = findViewById(R.id.edt_pass);
        btnSignUp = findViewById(R.id.btn_reg);
        btnLogin = findViewById(R.id.btn_login);

        progressBar = findViewById(R.id.progress);
        frame = findViewById(R.id.frame);

        mSettings = getSharedPreferences(APP_PREFERENCES, Context.MODE_PRIVATE);

        main = new Intent(this, MainActivity.class);

        FirebaseMessaging.getInstance().subscribeToTopic("new_offense")
                .addOnCompleteListener(task -> {
                    String msg = "subscribe";
                    if (!task.isSuccessful()) {
                        msg = "fail";
                    }
                    System.out.println(msg);
                });

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tableUser = database.getReference("User");

        btnSignUp.setOnClickListener(v -> {
            Intent signIn = new Intent(this, RegistrationActivity.class);
            startActivity(signIn);
            finish();
        });

        if (mSettings.contains(APP_PREFERENCES_PHONE) && mSettings.contains(APP_PREFERENCES_PASS)) {
            if (!mSettings.getString(APP_PREFERENCES_PHONE, "").equals("") && !mSettings.getString(APP_PREFERENCES_PASS, "").equals("")) {
                showProgress(true);
                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(mSettings.getString(APP_PREFERENCES_PHONE, "")).exists()) {
                            User user = dataSnapshot.child(mSettings.getString(APP_PREFERENCES_PHONE, "")).getValue(User.class);
                            if (user.getPassword().equals(mSettings.getString(APP_PREFERENCES_PASS, ""))) {
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putString(APP_PREFERENCES_NAME, user.getName());
                                editor.putString(APP_PREFERENCES_MAIL, user.getEmail());
                                editor.putString(APP_PREFERENCES_PASS, user.getPassword());
                                editor.putString(APP_PREFERENCES_PHONE, mSettings.getString(APP_PREFERENCES_PHONE, ""));
                                editor.putString(APP_PREFERENCES_VISIBLE_NAME, user.getVisibleEmail());
                                editor.putString(APP_PREFERENCES_VISIBLE_PHONE, user.getVisiblePhone());
                                editor.putString(APP_PREFERENCES_IMAGE, user.getImage());
                                editor.apply();
                                showProgress(false);
                                startActivity(main);
                                finish();
                            } else {
                                showProgress(false);
                                Toast.makeText(LoginActivity.this, "Номер телефону або пароль не правильні!\nПовторіть спробу", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            showProgress(false);
                            Toast.makeText(LoginActivity.this, "Користувача не знайдено\nСпробуйте зареєструватись", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

        }

        btnLogin.setOnClickListener(v -> {
            if (validate(inputPhone.getText().toString(), inputPassword.getText().toString())) {
                showProgress(true);
                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(inputPhone.getText().toString()).exists()) {
                            User user = dataSnapshot.child(inputPhone.getText().toString()).getValue(User.class);
                            if (user.getPassword().equals(inputPassword.getText().toString())) {
                                SharedPreferences.Editor editor = mSettings.edit();
                                editor.putString(APP_PREFERENCES_NAME, user.getName());
                                editor.putString(APP_PREFERENCES_MAIL, user.getEmail());
                                editor.putString(APP_PREFERENCES_PASS, user.getPassword());
                                editor.putString(APP_PREFERENCES_PHONE, inputPhone.getText().toString());
                                editor.putString(APP_PREFERENCES_VISIBLE_NAME, user.getVisibleEmail());
                                editor.putString(APP_PREFERENCES_VISIBLE_PHONE, user.getVisiblePhone());
                                editor.putString(APP_PREFERENCES_IMAGE, user.getImage());
                                editor.apply();
                                showProgress(false);
                                startActivity(main);
                                finish();
                            } else {
                                showProgress(false);
                                Toast.makeText(LoginActivity.this, "Номер телефону або пароль не правильні!\nПовторіть спробу", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            showProgress(false);
                            Toast.makeText(LoginActivity.this, "Користувача не знайдено\nСпробуйте зареєструватись", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });


    }

    private boolean validate(String phone, String password) {

        if (isEmpty(phone)) {
            inputLayoutPhone.setError("Введіть номер телефону");
            return false;
        } else if (!isPhoneValid(phone)) {
            inputLayoutPhone.setError("Номер телефону введено не коректно");
            return false;
        }
        if (isEmpty(password)) {
            inputLayoutPassword.setError("Введіть пароль");
            return false;
        }

        return true;
    }

    private static boolean isPhoneValid(String phone) {
        if (!Pattern.matches("[a-zA-Z]", phone)) {
            return phone.length() > 9 && phone.length() <= 13;
        }
        return false;
    }

    private void showProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        frame.setVisibility(isProgress ? View.VISIBLE : View.GONE);
    }
}
