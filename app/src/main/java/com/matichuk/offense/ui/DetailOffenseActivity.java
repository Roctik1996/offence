package com.matichuk.offense.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matichuk.offense.R;
import com.matichuk.offense.model.CarData;
import com.matichuk.offense.model.Offense;
import com.matichuk.offense.model.User;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class DetailOffenseActivity extends AppCompatActivity {

    private TextView itemName;
    private TextView itemEmail;
    private TextView itemPhone;
    private TextView title, year,color, reg,fuel,kind,body,actions;
    private ProgressBar progressBar;
    private FrameLayout frame;
    private ArrayList<String> carData = new ArrayList<>();
    FirebaseDatabase database;
    DatabaseReference tableUser,tableOffense;
    private ImageView photo;
    private ImageView back;
    private ImageView imageCar;
    private DatabaseReference tableCarData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_offense);
        itemName = findViewById(R.id.item_name);
        itemEmail = findViewById(R.id.item_email);
        itemPhone = findViewById(R.id.item_phone);
        progressBar = findViewById(R.id.progress);
        frame = findViewById(R.id.frame);
        title = findViewById(R.id.txt_title);
        year = findViewById(R.id.txt_year);
        color = findViewById(R.id.txt_color);
        fuel = findViewById(R.id.txt_fuel);
        body = findViewById(R.id.txt_body);
        kind = findViewById(R.id.txt_kind);
        reg = findViewById(R.id.txt_reg);
        actions = findViewById(R.id.txt_actions);
        photo = findViewById(R.id.photo);
        back = findViewById(R.id.icon_back);
        imageCar = findViewById(R.id.image_car);

        back.setOnClickListener(v -> onBackPressed());

        database = FirebaseDatabase.getInstance();
        tableUser = database.getReference("User");
        tableOffense = database.getReference("Offense");

        getInfo(getIntent().getStringExtra("car_number"));

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void getInfo(String number) {
        showProgress(true);
        carData.clear();
        tableCarData = database.getReference("CarData").child(number);
        tableCarData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                CarData carData = dataSnapshot.getValue(CarData.class);
                setData(carData);
                showProgress(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //getInfo(txtScan.getText().toString());
    }
    @SuppressLint("SetTextI18n")
    private void setData(CarData carData) {
        tableUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.child(getIntent().getStringExtra("phone")).getValue(User.class);
                itemName.setText(user.getName());
                if (user.getVisibleEmail().equals("false"))
                    itemEmail.setText("E-mail приховано");
                else
                    itemEmail.setText(user.getEmail());

                if (user.getVisiblePhone().equals("false"))
                    itemPhone.setText("Номер телефону приховано");
                else
                    itemPhone.setText(getIntent().getStringExtra("phone"));

                Glide.with(DetailOffenseActivity.this).load(user.getImage())
                        .into(photo);
                photo.setColorFilter(ContextCompat.getColor(DetailOffenseActivity.this, android.R.color.transparent));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        tableOffense.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Offense offense = dataSnapshot.child(getIntent().getStringExtra("key")).getValue(Offense.class);
                if (!offense.getImage().equals(""))
                    Glide.with(DetailOffenseActivity.this).load(offense.getImage())
                            .into(imageCar);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        if (carData!=null) {
            title.setVisibility(View.VISIBLE);
            year.setVisibility(View.VISIBLE);
            color.setVisibility(View.VISIBLE);
            fuel.setVisibility(View.VISIBLE);
            body.setVisibility(View.VISIBLE);
            kind.setVisibility(View.VISIBLE);
            reg.setVisibility(View.VISIBLE);
            actions.setVisibility(View.VISIBLE);
            title.setText(carData.getBRAND()+" "+carData.getMODEL());
            year.setText("Рік випуску: "+ carData.getMAKE_YEAR());
            color.setText("Колір: "+ carData.getCOLOR());
            fuel.setText("Паливо: "+ carData.getFUEL());
            kind.setText("Тип: "+ carData.getKIND());
            body.setText("Кузов: "+ carData.getBODY());
            reg.setText("Дата реєстрації: "+ carData.getD_REG());
            actions.setText("Дії: " + carData.getOPER_NAME());
        } else {
            clearData();
            title.setVisibility(View.VISIBLE);
            reg.setVisibility(View.VISIBLE);
            title.setText("Номер авто: "+getIntent().getStringExtra("car_number"));
            reg.setText("Інших даних про авто не знайдено");
        }
    }



    private void clearData() {
        carData.clear();
        title.setVisibility(View.GONE);
        year.setVisibility(View.GONE);
        color.setVisibility(View.GONE);
        fuel.setVisibility(View.GONE);
        body.setVisibility(View.GONE);
        kind.setVisibility(View.GONE);
        reg.setVisibility(View.GONE);
        actions.setVisibility(View.GONE);
    }

    private void showProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        frame.setVisibility(isProgress ? View.VISIBLE : View.GONE);
    }
}
