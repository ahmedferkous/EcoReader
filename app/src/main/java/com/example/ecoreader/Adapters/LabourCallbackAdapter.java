package com.example.ecoreader.Adapters;

import com.example.ecoreader.DataRetrieval.GetLabourStatsData;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedLabourRequest;
import com.example.ecoreader.DataRetrieval.Interfaces.onCompletedRetrieval;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.StatisticsObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.CIVILIAN_POPULATION;

import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.EMPLOYED_FULL_TIME;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.EMPLOYMENT_TO_POPULATION_RATIO;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK;
import static com.example.ecoreader.DataRetrieval.GetLabourStatsData.UNEMPLOYED_PERSONS;

public class LabourCallbackAdapter implements Callback<StatisticsObject> {
    private final String type;
    private final FinishedLabourRequest serviceCallback;
    private final onCompletedRetrieval onCompletedRetrieval;

    public LabourCallbackAdapter(String type, FinishedLabourRequest serviceCallback, GetLabourStatsData getLabourStatsData) {
        this.type = type;
        this.serviceCallback = serviceCallback;
        this.onCompletedRetrieval = getLabourStatsData;
    }

    @Override
    public void onResponse(Call<StatisticsObject> call, Response<StatisticsObject> response) {
        if (response.isSuccessful() && response.body() != null) {
            ArrayList<StatisticsObject.Values> values = response.body().getLabourStatistics();
            float value = values.get(values.size() - 1).getObservationValue();
            switch (type) {
                case CIVILIAN_POPULATION:
                    serviceCallback.onReceivedPopulation((int) (value * 1000));
                    onCompletedRetrieval.onCompletedResultType(CIVILIAN_POPULATION);
                    break;
                case EMPLOYMENT_TO_POPULATION_RATIO:
                    serviceCallback.onReceivedEmploymentToPopulationRatio(value);
                    onCompletedRetrieval.onCompletedResultType(EMPLOYMENT_TO_POPULATION_RATIO);
                    break;
                case UNEMPLOYED_PERSONS:
                    serviceCallback.onReceivedUnemployedPersons((int) (value * 1000));
                    onCompletedRetrieval.onCompletedResultType(UNEMPLOYED_PERSONS);
                    break;
                case EMPLOYED_FULL_TIME:
                    serviceCallback.onReceivedEmployedFullTime((int) (value * 1000));
                    onCompletedRetrieval.onCompletedResultType(EMPLOYED_FULL_TIME);
                    break;
                case UNEMPLOYED_LOOKING_FOR_FULL_TIME_WORK:
                    serviceCallback.onReceivedUnemployedLookingForFullTimeWork((int) (value * 1000));
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onFailure(Call<StatisticsObject> call, Throwable t) {
        Call<StatisticsObject> retryCall = call.clone();
        new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(15000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                retryCall.enqueue(LabourCallbackAdapter.this);
            }
        }.start();
    }
}
