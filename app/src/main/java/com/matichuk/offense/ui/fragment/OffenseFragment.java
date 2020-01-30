package com.matichuk.offense.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.firebase.ui.database.SnapshotParser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.matichuk.offense.R;
import com.matichuk.offense.model.Offense;
import com.matichuk.offense.model.User;
import com.matichuk.offense.ui.DetailOffenseActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class OffenseFragment extends Fragment {
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter adapter;
    FirebaseDatabase database;
    DatabaseReference tableUser;
    FirebaseRecyclerOptions<Offense> options;
    private Offense offense;

    public OffenseFragment() {
    }

    public static OffenseFragment newInstance() {
        return new OffenseFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_offense, container, false);

        recyclerView = view.findViewById(R.id.recycler);

        database = FirebaseDatabase.getInstance();
        tableUser = database.getReference("User");

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);
        fetch();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onPause() {
        super.onPause();
        adapter.stopListening();
    }

    private void fetch() {
        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Offense");

         options= new FirebaseRecyclerOptions.Builder<Offense>()
                        .setQuery(query, new SnapshotParser<Offense>() {
                            @NonNull
                            @Override
                            public Offense parseSnapshot(@NonNull DataSnapshot snapshot) {
                                offense = new Offense(snapshot.child("userPhone").getValue().toString(),
                                        snapshot.child("carNumber").getValue().toString(),
                                        snapshot.child("offenseType").getValue().toString(),
                                        snapshot.child("image").getValue().toString(),
                                        snapshot.getKey());
                                return offense;
                            }
                        })
                        .build();

        adapter = new FirebaseRecyclerAdapter<Offense, ViewHolder>(options) {
            @Override
            public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_offense, parent, false);

                return new ViewHolder(view);
            }


            @SuppressLint("SetTextI18n")
            @Override
            protected void onBindViewHolder(@NotNull ViewHolder holder, final int position, @NotNull Offense model) {
                tableUser.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.child(model.getUserPhone()).getValue(User.class);
                        holder.itemName.setText(user.getName());
                        if (user.getVisibleEmail().equals("false"))
                            holder.itemEmail.setText("E-mail приховано");
                        else
                            holder.itemEmail.setText(user.getEmail());

                        if (user.getVisiblePhone().equals("false"))
                            holder.itemPhone.setText("Номер телефону приховано");
                        else
                            holder.itemPhone.setText(model.getUserPhone());

                        if (user.getImage()!=null) {
                            Glide.with(getContext()).load(user.getImage())
                                    .into(holder.photo);
                            holder.photo.setColorFilter(ContextCompat.getColor(Objects.requireNonNull(getContext()), android.R.color.transparent));
                        }
                        holder.itemNumber.setText("Номер авто"+" "+model.getCarNumber());
                        holder.itemOffense.setText("Порушення "+" "+model.getOffenseType());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


                holder.root.setOnClickListener(view -> {
                    Intent detail = new Intent(getContext(), DetailOffenseActivity.class);
                    detail.putExtra("car_number",model.getCarNumber());
                    detail.putExtra("phone",model.getUserPhone());
                    detail.putExtra("key",model.getKey());
                    startActivity(detail);
                });
            }

        };
        recyclerView.setAdapter(adapter);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout root;
        private TextView itemName;
        private TextView itemEmail;
        private TextView itemPhone;
        private TextView itemNumber;
        private TextView itemOffense;
        private ImageView photo;

        ViewHolder(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.list_root);
            itemName = itemView.findViewById(R.id.item_name);
            itemEmail = itemView.findViewById(R.id.item_email);
            itemPhone = itemView.findViewById(R.id.item_phone);
            itemNumber = itemView.findViewById(R.id.item_car_number);
            itemOffense = itemView.findViewById(R.id.item_offense);
            photo = itemView.findViewById(R.id.photo);
        }
    }
}
