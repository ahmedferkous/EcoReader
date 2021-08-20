package com.example.ecoreader.Fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ecoreader.DataRetrieval.Interfaces.FinishedLabourRequest;
import com.example.ecoreader.R;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.text.DecimalFormat;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ecoreader.Application.GetDataService.ECO_UPDATES;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.CIVILIAN_POPULATION;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.EMPLOYED_FULL_TIME;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.EMPLOYMENT_TO_POPULATION_RATIO;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.UNEMPLOYED_PERSONS;

public class LabourFragment extends Fragment {
    private static final String TAG = "LabourFragment";
    private int population, unemployedPersons, employedFullTime, unemployedLookingForFullTimeWork, employedPersons, labourForce, notInLabourForce, employedPartTime, unemployedLookingForPartTimeWork;
    private String unemploymentRate, participationRate;
    private TextView txtNotInWorkforce, txtEmployed, txtUnemployed, txtTotalPopulation, txtUnemploymentRate, txtParticipationRate, txtEmployedFullTime, txtEmployedPartTime, txtUnemployedFullTime, txtUnemployedPartTime, txtTotalLabourForce;
    private PieChart pieChartPop, pieChartLab;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_labour, container, false);
        initView(view);
        initData();
        setUpData();
        return view;
    }

    private void initView(View view) {
        txtNotInWorkforce = view.findViewById(R.id.txtNotInWorkforce);
        txtEmployed = view.findViewById(R.id.txtEmployed);
        txtUnemployed = view.findViewById(R.id.txtUnemployed);
        txtTotalPopulation = view.findViewById(R.id.txtTotalPopulation);
        txtEmployedFullTime = view.findViewById(R.id.txtEmployedFullTime);
        txtEmployedPartTime = view.findViewById(R.id.txtEmployedPartTime);
        txtUnemployedFullTime = view.findViewById(R.id.txtUnemployedFullTime);
        txtUnemployedPartTime = view.findViewById(R.id.txtUnEmployedPartTime);
        txtTotalLabourForce = view.findViewById(R.id.txtTotalLabourForce);
        txtUnemploymentRate = view.findViewById(R.id.txtUnemploymentRate);
        txtParticipationRate = view.findViewById(R.id.txtParticipationRate);
        pieChartPop = view.findViewById(R.id.pieChartPop);
        pieChartLab = view.findViewById(R.id.pieChartLabour);
    }

    private void initData() {
        if (getContext() != null) {
            DecimalFormat format = new DecimalFormat("#0.00");
            SharedPreferences preferences = getContext().getSharedPreferences(ECO_UPDATES, MODE_PRIVATE);
            population = preferences.getInt(CIVILIAN_POPULATION, 1);
            float employmentToPopulationRatio = preferences.getFloat(EMPLOYMENT_TO_POPULATION_RATIO, 1);
            unemployedPersons = preferences.getInt(UNEMPLOYED_PERSONS, 1);
            employedFullTime = preferences.getInt(EMPLOYED_FULL_TIME, 1);
            unemployedLookingForFullTimeWork = preferences.getInt(UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK, 1);

            employedPersons = (int) (population * employmentToPopulationRatio);
            labourForce = unemployedPersons + employedPersons;
            notInLabourForce = population - labourForce;
            unemploymentRate = format.format(((float) unemployedPersons / (float) labourForce) * 100);
            participationRate = format.format(((float) labourForce / (float) population) * 100);
            employedPartTime = employedPersons - employedFullTime;
            unemployedLookingForPartTimeWork = unemployedPersons - unemployedLookingForFullTimeWork;
        }
    }

    @SuppressLint("SetTextI18n")
    private void setUpData() {
        txtUnemploymentRate.setText(unemploymentRate + "%");
        txtParticipationRate.setText(participationRate + "%");
        txtTotalPopulation.setText(getReadableNumber(population));
        txtNotInWorkforce.setText(getReadableNumber(notInLabourForce));
        txtEmployed.setText(getReadableNumber(employedPersons));
        txtUnemployed.setText(getReadableNumber(unemployedPersons));
        txtEmployedFullTime.setText(getReadableNumber(employedFullTime));
        txtEmployedPartTime.setText(getReadableNumber(employedPartTime));
        txtUnemployedFullTime.setText(getReadableNumber(unemployedLookingForFullTimeWork));
        txtUnemployedPartTime.setText(getReadableNumber(unemployedLookingForPartTimeWork));
        txtTotalLabourForce.setText(getReadableNumber(labourForce));

        pieChartPop.addPieSlice(
                new PieModel(
                        "Not In Workforce",
                        notInLabourForce,
                        Color.parseColor("#595959")));
        pieChartPop.addPieSlice(
                new PieModel(
                        "Employed Persons",
                        employedPersons,
                        Color.parseColor("#3DDC84")));
        pieChartPop.addPieSlice(
                new PieModel(
                        "Unemployed Persons",
                        unemployedPersons,
                        Color.parseColor("#2495d6")));

        pieChartLab.addPieSlice(
                new PieModel(
                        "Employed Full Time",
                        employedFullTime,
                        Color.parseColor("#3DDC84")));
        pieChartLab.addPieSlice(
                new PieModel(
                        "Employed Part Time",
                        employedPartTime,
                        Color.parseColor("#31734e")));
        pieChartLab.addPieSlice(
                new PieModel(
                        "Unemployed Looking For Full Time Work",
                        unemployedLookingForFullTimeWork,
                        Color.parseColor("#2495d6")));
        pieChartLab.addPieSlice(
                new PieModel(
                        "Unemployed Looking For Part Time Work",
                        unemployedLookingForPartTimeWork,
                        Color.parseColor("#1a4782")));

        pieChartPop.startAnimation();
        pieChartLab.startAnimation();
    }

    private String getReadableNumber(int number) {
        DecimalFormat format = new DecimalFormat("0");
        format.setGroupingUsed(true);
        format.setGroupingSize(3);
        return format.format(number);
    }
}
