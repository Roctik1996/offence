package com.matichuk.offense.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.matichuk.offense.R;
import com.matichuk.offense.model.User;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.text.TextUtils.isEmpty;

public class RegistrationActivity extends AppCompatActivity {

    private AppCompatEditText inputName, inputEmail, inputPhone, inputPassword;
    private TextInputLayout inputLayoutName, inputLayoutEmail, inputLayoutPhone, inputLayoutPassword;
    private Button btnSignUp;
    private ImageView back;
    private ProgressBar progressBar;
    private FrameLayout frame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        inputLayoutName = findViewById(R.id.input_layout_name);
        inputLayoutEmail = findViewById(R.id.input_layout_mail);
        inputLayoutPhone = findViewById(R.id.input_layout_phone);
        inputLayoutPassword = findViewById(R.id.input_layout_password);
        inputName = findViewById(R.id.edt_name);
        inputEmail = findViewById(R.id.edt_mail);
        inputPhone = findViewById(R.id.edt_phone);
        inputPassword = findViewById(R.id.edt_pass);
        btnSignUp = findViewById(R.id.btn_reg);
        back = findViewById(R.id.icon_back);

        progressBar = findViewById(R.id.progress);
        frame = findViewById(R.id.frame);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tableUser = database.getReference("User");

        back.setOnClickListener(v -> onBackPressed());

        btnSignUp.setOnClickListener(v -> {
            if (validate(inputName.getText().toString(), inputEmail.getText().toString(), inputPhone.getText().toString(), inputPassword.getText().toString())) {
                showProgress(true);
                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.child(inputPhone.getText().toString()).exists()) {
                            showProgress(false);
                            Toast.makeText(RegistrationActivity.this, "Користувач з таким номером телефону вже зареєстрований", Toast.LENGTH_LONG).show();
                        } else {
                            User user = new User(inputName.getText().toString(),inputEmail.getText().toString(),inputPassword.getText().toString(),"false","false","");
                            tableUser.child(inputPhone.getText().toString()).setValue(user);
                            showProgress(false);
                            Toast.makeText(RegistrationActivity.this, "Реєстрація успішна!", Toast.LENGTH_LONG).show();
                            Intent login = new Intent(RegistrationActivity.this, LoginActivity.class);
                            startActivity(login);
                            finish();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private boolean validate(String name, String email, String phone, String password) {

        if (isEmpty(name)) {
            inputLayoutName.setError("Введіть ім\'я");
            return false;
        }

        if (isEmpty(email)) {
            inputLayoutEmail.setError("Введіть e-mail");
            return false;
        } else if (!isEmailValid(email)) {
            inputLayoutEmail.setError("E-mail введено не коректно");
            return false;
        }

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
        } else if (!isPasswordValid(password)) {
            inputLayoutPassword.setError("Пароль повинен містити велику букву, цифру і бути більше 6 символів");
            return false;
        }

        return true;
    }

    private static boolean isEmailValid(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    private static boolean isPhoneValid(String phone) {
        if (!Pattern.matches("[a-zA-Z]", phone)) {
            return phone.length() > 9 && phone.length() <= 13;
        }
        return false;
    }

    private static boolean isPasswordValid(String password) {
        if (Pattern.matches("^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,25}$", password))
            return true;
        return false;
    }

    private void showProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        frame.setVisibility(isProgress ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent back = new Intent(this, LoginActivity.class);
        startActivity(back);
        finish();
    }
}
