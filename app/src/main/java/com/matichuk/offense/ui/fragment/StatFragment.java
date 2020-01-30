package com.matichuk.offense.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.matichuk.offense.R;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatFragment extends Fragment {
    private ArrayList<String> parking = new ArrayList<>();
    private ArrayList<String> red = new ArrayList<>();
    private ArrayList<String> speed = new ArrayList<>();
    private ArrayList<String> doubleLine = new ArrayList<>();
    private ArrayList<String> accident = new ArrayList<>();
    private ArrayList<String> other = new ArrayList<>();

    private PieChart chart;
    private BarChart barChart;

    public StatFragment() {
    }

    public static StatFragment newInstance() {
        return new StatFragment();
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stat, container, false);
        chart = view.findViewById(R.id.chart);
        barChart = view.findViewById(R.id.bar_chart);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Offense");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(@NotNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.getValue() != null)
                            collectData((Map<String, Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(@NotNull DatabaseError databaseError) {
                    }
                });

        return view;
    }

    private void collectData(Map<String, Object> users) {
        //iterate through each user, ignoring their UID
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            String data = (String) singleUser.get("offenseType");
            if (data.equals("Неправильне паркування"))
                parking.add(data);
            else if (data.equals("Проїзд на червоне світло"))
                red.add(data);
            else if (data.equals("Перевищення швидкості"))
                speed.add(data);
            else if (data.equals("Подвійна суцільна"))
                doubleLine.add(data);
            else if (data.equals("Аварія"))
                accident.add(data);
            else if (data.equals("Інше"))
                other.add(data);
            setChart();
            setBarChart();
        }
    }

    private void collectDataCity(Map<String, Object> users){
        for (Map.Entry<String, Object> entry : users.entrySet()) {

            //Get user map
            Map singleUser = (Map) entry.getValue();
            //Get phone field and append to list
            String data = (String) singleUser.get("carNumber");
            if (data.substring(0,1).equals("CE"))
                parking.add(data);
            else if (data.equals("Проїзд на червоне світло"))
                red.add(data);
            else if (data.equals("Перевищення швидкості"))
                speed.add(data);
            else if (data.equals("Подвійна суцільна"))
                doubleLine.add(data);
            else if (data.equals("Аварія"))
                accident.add(data);
            else if (data.equals("Інше"))
                other.add(data);
            //setChart();
        }
    }

    private void setChart() {
        int total = parking.size() + red.size() + speed.size() + doubleLine.size() + accident.size() + other.size();
        List<PieEntry> entries = new ArrayList<>();

        if (parking.size() > 0)
            entries.add(new PieEntry(getPercent(total, parking.size()), "Неправильне паркування"));
        if (red.size() > 0)
            entries.add(new PieEntry(getPercent(total, red.size()), "Проїзд на червоне світло"));
        if (speed.size() > 0)
            entries.add(new PieEntry(getPercent(total, speed.size()), "Перевищення швидкості"));
        if (doubleLine.size() > 0)
            entries.add(new PieEntry(getPercent(total, doubleLine.size()), "Подвійна суцільна"));
        if (accident.size() > 0)
            entries.add(new PieEntry(getPercent(total, accident.size()), "Аварія"));
        if (other.size() > 0)
            entries.add(new PieEntry(getPercent(total, other.size()), "Інше"));

        PieDataSet set = new PieDataSet(entries, "");
        set.setValueLineVariableLength(false);
        set.setColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA, Color.parseColor("#ff7f27"));
        PieData data = new PieData(set);
        data.setValueTextSize(14);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.setDrawEntryLabels(false);
        chart.setCenterText("Загальна кількість порушень\n" + total);
        Legend l = chart.getLegend();
        l.setTextColor(Color.BLACK);
        l.setTextSize(14f);
        l.setForm(Legend.LegendForm.CIRCLE);
        l.setFormSize(10f);
        l.setXEntrySpace(5f);
        l.setFormToTextSpace(10f);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setWordWrapEnabled(true);
        l.setDirection(Legend.LegendDirection.LEFT_TO_RIGHT);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);

        chart.invalidate();
    }

    private void setBarChart(){
        BarDataSet barDataSet = new BarDataSet(getData(), "Inducesmile");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData barData = new BarData(barDataSet);
        XAxis xAxis = barChart.getXAxis();
        barChart.getAxisLeft().setAxisMaximum(100);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        final String[] city = new String[]{"АР Крим",
                "Вінницька",
                "Волинська",
                "Дніпропетровська",
                "Донецька",
                "Житомирська",
                "Закарпатська",
                "Запорізька",
                "Івано-Франківська",
                "Київська",
                "Кіровоградська",
                "Луганська",
                "Львівська",
                "Миколаївська",
                "Одеська",
                "Полтавська",
                "Рівненська",
                "Сумська",
                "Тернопільська",
                "Харківська",
                "Херсонська",
                "Хмельницька",
                "Черкаська",
                "Чернівецька",
                "Чернігівська"
        };
        IndexAxisValueFormatter formatter = new IndexAxisValueFormatter(city);
        xAxis.setGranularity(0.5f);
        xAxis.setValueFormatter(formatter);
        barChart.setData(barData);
        barChart.invalidate();
    }

    private List getData(){
        List<BarEntry> entries = new ArrayList<>();
        entries.add(new BarEntry(0f, 31f));
        entries.add(new BarEntry(1f, 81f));
        entries.add(new BarEntry(2f, 61f));
        entries.add(new BarEntry(3f, 51f));
        entries.add(new BarEntry(4f, 71f));
        entries.add(new BarEntry(5f, 61f));
        entries.add(new BarEntry(6f, 32f));
        entries.add(new BarEntry(7f, 82f));
        entries.add(new BarEntry(8f, 62f));
        entries.add(new BarEntry(9f, 52f));
        entries.add(new BarEntry(10f, 72f));
        entries.add(new BarEntry(11f, 62f));
        entries.add(new BarEntry(12f, 33f));
        entries.add(new BarEntry(13f, 83f));
        entries.add(new BarEntry(14f, 63f));
        entries.add(new BarEntry(15f, 53f));
        entries.add(new BarEntry(16f, 73f));
        entries.add(new BarEntry(17f, 63f));
        entries.add(new BarEntry(18f, 34f));
        entries.add(new BarEntry(19f, 84f));
        entries.add(new BarEntry(20f, 64f));
        entries.add(new BarEntry(21f, 54f));
        entries.add(new BarEntry(22f, 74f));
        entries.add(new BarEntry(23f, 65f));
        entries.add(new BarEntry(24f, 67f));
        return entries;
    }

    private float getPercent(int total, int fromValue) {
        float result = (fromValue * 100) / total;
        return result;
    }
}
