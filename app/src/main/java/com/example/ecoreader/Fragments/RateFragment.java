package com.example.ecoreader.Fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecoreader.Adapters.NewsObject;
import com.example.ecoreader.DataRetrieval.Interfaces.FinishedRatesRequest;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.LatestRatesObject;
import com.example.ecoreader.DataRetrieval.PlainOldJavaObjects.TimeSeriesObject;
import com.example.ecoreader.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;
import static com.example.ecoreader.Application.GetDataService.AUD_CODE;
import static com.example.ecoreader.Application.GetDataService.ECO_UPDATES;
import static com.example.ecoreader.Application.GetDataService.SAVED_AVAILABLE_CURRENCIES;
import static com.example.ecoreader.Application.GetDataService.SAVED_RATES;
import static com.example.ecoreader.Application.GetDataService.USD_CODE;

// TODO: 13/08/2021 Save data for fragment transactions 
public class RateFragment extends Fragment{
    private static final String TAG = "RateFragment";

    private ArrayList<String> spinnerArray = new ArrayList<>();
    private HashMap<String, Float> rates = new HashMap<>();
    private Spinner spinner;
    private TextView txtEquals;
    private EditText edtTxtAmount, edtTxtConvert;
    private ChartFragment childFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exchange, container, false);
        edtTxtAmount = view.findViewById(R.id.edtTxtAmount);
        edtTxtConvert = view.findViewById(R.id.edtTxtConvert);
        spinner = view.findViewById(R.id.spinnerAmountConvert);
        txtEquals = view.findViewById(R.id.txtEquals);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String currencyCode = getCurrencyCode(spinnerArray.get(position));
                //edtTxtConvert.setHint(convertCurrency(currencyCode, 1, true) + " " + currencyCode);
                String data = "1 AUD = " + convertCurrency(currencyCode, 1, true) + " " + currencyCode;
                txtEquals.setText(data);
                edtTxtConvert.setText("");
                edtTxtAmount.setText("");
                childFragment.retrieveChartStats(currencyCode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtTxtConvert.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override // TODO: 13/08/2021 recursive call fix "ping pong"
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*
                String amount = s + "";
                if (amount.length() > 0) {
                    double amountToConvert = Double.parseDouble(amount);
                    if (amountToConvert > 0.0) {
                        edtTxtAmount.setText(convertCurrency(getCurrencyCode((String) spinner.getSelectedItem()), amountToConvert, false));
                    }
                } else {
                    edtTxtAmount.setText("");
                }

                 */
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edtTxtAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String amount = s + "";
                if (amount.length() > 0) {
                    double amountToConvert = Double.parseDouble(amount);
                    if (amountToConvert > 0.0) {
                        edtTxtConvert.setText(convertCurrency(getCurrencyCode((String) spinner.getSelectedItem()), amountToConvert, true));
                    }
                } else {
                    edtTxtConvert.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        loadRates();
        loadCurrencies();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        childFragment = new ChartFragment();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.chartFragmentContainer, childFragment).commit();
    }


    private String convertCurrency(String currencyCode, double numberToConvert, boolean multiply) {
        DecimalFormat format = new DecimalFormat("#0.000");
        Float conversionRate = rates.get(currencyCode);
        if (multiply) {
            return format.format( numberToConvert*conversionRate);
        } else {
            return format.format( numberToConvert/conversionRate);
        }
    }

    private String getCurrencyCode(String country) {
        String[] strArray = country.split("[()]");
        return strArray[strArray.length-1];
    }

    private void loadRates() {
        Gson gson = new Gson();
        Type ratesTypeToken = new TypeToken<HashMap<String, Float>>(){}.getType();
        rates = gson.fromJson(getContext().getSharedPreferences(ECO_UPDATES, MODE_PRIVATE).getString(SAVED_RATES, gson.toJson(new ArrayList<NewsObject>())), ratesTypeToken);
        String data = "1 AUD = " + convertCurrency(USD_CODE, 1, true) + " " + USD_CODE;
        txtEquals.setText(data);
    }

    private void loadCurrencies() {
        Gson gson = new Gson();
        Type currencyTypeToken = new TypeToken<HashMap<String, String>>(){}.getType();
        HashMap<String, String> currencies = gson.fromJson(getContext().getSharedPreferences(ECO_UPDATES, MODE_PRIVATE).getString(SAVED_AVAILABLE_CURRENCIES, gson.toJson(new ArrayList<NewsObject>())), currencyTypeToken);
        if (currencies != null) {
            spinnerArray = new ArrayList<>(currencies.size());
            String defaultStr = "";
            for (String currencyCode : currencies.keySet()) {
                String newRate = currencies.get(currencyCode) + " (" + currencyCode + ")";
                if (!currencyCode.equals(AUD_CODE) && !(currencyCode.equals(USD_CODE))) {
                    spinnerArray.add(newRate);
                }
                if (currencyCode.equals(USD_CODE)) {
                    defaultStr = newRate;
                }
            }
            Collections.sort(spinnerArray);
            spinnerArray.set(0, defaultStr);
            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,
                    spinnerArray);
            spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(spinnerArrayAdapter);
        }
    }

}
