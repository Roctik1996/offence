package com.matichuk.offense.ui.adapter;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.matichuk.offense.R;
import com.matichuk.offense.model.PriceData;
import com.matichuk.offense.ui.DetailOffenseActivity;

import java.util.ArrayList;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {

    private ArrayList<PriceData> data;

    public PriceAdapter(ArrayList<PriceData> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_price, viewGroup, false);
        return new PriceViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder priceViewHolder, int i) {
        Glide.with(priceViewHolder.itemView.getContext()).load(data.get(i).getPhoto())
                .into(priceViewHolder.photo);

        priceViewHolder.carName.setText(data.get(i).getCarTitle());
        priceViewHolder.info.setText(data.get(i).getPrice()+"\n"+
                        "Місто: "+data.get(i).getLocation()+"\n"+
                        "Тип палива: "+data.get(i).getFuel()+"\n"+
                        "Пробіг: "+data.get(i).getMileage()+"\n"+
                        "Тип коробки передач: "+data.get(i).getTransmission()+"\n"
                );

    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    static class PriceViewHolder extends RecyclerView.ViewHolder {

        ImageView photo;
        TextView carName, info;

        PriceViewHolder(@NonNull View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.item_photo);
            carName = itemView.findViewById(R.id.car_name);
            info = itemView.findViewById(R.id.car_info);
        }
    }
}

