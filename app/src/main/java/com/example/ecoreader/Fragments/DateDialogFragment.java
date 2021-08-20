package com.example.ecoreader.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ecoreader.Application.GetDataService;
import com.example.ecoreader.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateDialogFragment extends DialogFragment {
    private static final String TAG = "DateDialogFragment";
    public interface onDateChosen {
        void dateResult(String date);
    }
    public DateDialogFragment(ChartFragment fragment) {
        onDateChosen = fragment;
    }

    private onDateChosen onDateChosen;
    private DatePicker datePicker;
    private Button confirmButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment, container, false);
        datePicker = view.findViewById(R.id.datePicker);
        confirmButton = view.findViewById(R.id.confirmButton);

        datePicker.setMaxDate(dateToEpoch(getDate(Calendar.DATE,-7)));

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = "";
                String day = "";
                if (datePicker.getMonth()+1 < 10) {
                    month = "0"+(datePicker.getMonth()+1);
                } else {
                    month = (datePicker.getMonth()+1) + "";
                }
                if (datePicker.getDayOfMonth() < 10) {
                    day = "0"+datePicker.getDayOfMonth();
                } else {
                    day = datePicker.getDayOfMonth() + "";
                }
                String dateInformation = datePicker.getYear() + "-" + month + "-" + day;
                onDateChosen.dateResult(dateInformation);
                Log.d(TAG, "onClick: " + dateInformation);
                dismiss();
            }
        });
        return view;
    }

    private long dateToEpoch(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse(date).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getDate(int type, int subtraction) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(Calendar.getInstance().getTime());
        cal.add(type, subtraction);
        String newDate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
        cal.add(type, -subtraction);
        return newDate;
    }
}
