package com.example.ecoreader.DataRetrieval.Interfaces;

public interface FinishedLabourRequest {
    void onReceivedPopulation(int population);
    void onReceivedEmploymentToPopulationRatio(float ratio);
    void onReceivedUnemployedPersons(int persons);
    void onReceivedEmployedFullTime(int persons);
    void onReceivedUnemployedLookingForFullTimeWork(int persons);
}
