package com.matichuk.offense.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.matichuk.offense.R;
import com.matichuk.offense.model.PriceData;
import com.matichuk.offense.ui.adapter.PriceAdapter;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class PriceActivity extends AppCompatActivity {
    private ProgressBar progressBar;
    private FrameLayout frame;
    private RecyclerView recyclerView;
    private ImageView back;
    private TextView title;
    private String catTitle;
    private ArrayList<PriceData> priceData = new ArrayList<>();
    private ArrayList<String>itemDetailCar=new ArrayList<>();
    private ArrayList<String>itemPriceCar=new ArrayList<>();
    private ArrayList<String>itemTitleCar=new ArrayList<>();
    private ArrayList<String>itemPhotoCar=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price);
        progressBar = findViewById(R.id.progress);
        frame = findViewById(R.id.frame);
        back = findViewById(R.id.icon_back);
        title = findViewById(R.id.txt_title);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView = findViewById(R.id.recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        back.setOnClickListener(v -> onBackPressed());

        catTitle = getIntent().getStringExtra("carName");
        String url = "https://auto.ria.com/uk/car/"+ catTitle.toLowerCase() + "/?page=1";
        System.out.println(url);
        getPrice(url);
    }

    private void getPrice(String url) {
        showProgress(true);
        itemDetailCar.clear();
        priceData.clear();
        itemPriceCar.clear();
        itemTitleCar.clear();
        itemPhotoCar.clear();
        new Thread(() -> {
            final StringBuilder builder = new StringBuilder();
            int i = 0;

            try {
                Document doc = Jsoup.connect(url).get();
                Elements price = doc.select("img.m-auto");
                for (Element priceCar : price) {
                    itemPhotoCar.add(i, priceCar.attr("src"));
                    i++;
                }
            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }
            i=0;
            try {
                Document doc = Jsoup.connect(url).get();
                Elements price = doc.select("div.head-ticket");
                for (Element priceCar : price) {
                    itemTitleCar.add(i, priceCar.text());
                    i++;
                }
            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

            i=0;

            try {
                Document doc = Jsoup.connect(url).get();
                Elements price = doc.select("div.price-ticket");
                for (Element priceCar : price) {
                    itemPriceCar.add(i, priceCar.text());
                    i++;
                }
            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }
            i=0;
            try {
                Document doc = Jsoup.connect(url).get();
                Elements detail = doc.select("li.item-char");
                for (Element detailCar : detail) {
                    itemDetailCar.add(i,detailCar.text());
                    i++;
                }
            } catch (IOException e) {
                builder.append("Error : ").append(e.getMessage()).append("\n");
            }

            int el=0;
            for (int items=0;items<itemDetailCar.size();items++){
                priceData.add(el,new PriceData(
                        itemTitleCar.get(el),
                        itemPriceCar.get(el),itemDetailCar.get(items),
                        itemDetailCar.get(items+1),
                        itemDetailCar.get(items+2),
                        itemDetailCar.get(items+3),
                        itemPhotoCar.get(el)));
                el++;
                items=items+3;
            }

            runOnUiThread(this::setData);
        }).start();

    }

    private void setData() {
        showProgress(false);
        PriceAdapter adapter = new PriceAdapter(priceData);
        recyclerView.setAdapter(adapter);
        ArrayList<String> avr=  new ArrayList<>();
        String arr[];
        for (int i=0;i<priceData.size();i++) {
            arr = priceData.get(i).getPrice().split("•");
            avr.addAll(Arrays.asList(arr));
        }

        int resultUah=0,resultUsd=0;
        for (int k=0;k<avr.size();k++) {
            if (k % 2 == 0) {
                resultUsd = resultUsd + Integer.valueOf(avr.get(k).replaceAll("\\D+", ""));

            } else {
                resultUah = resultUah + Integer.valueOf(avr.get(k).replaceAll("\\D+", ""));
            }
        }
        title.setText("Середня вартість\n"+resultUsd/priceData.size()+"$ • "+resultUah/priceData.size()+" грн.");

    }

    private void showProgress(boolean isProgress) {
        progressBar.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        frame.setVisibility(isProgress ? View.VISIBLE : View.GONE);
    }
}
