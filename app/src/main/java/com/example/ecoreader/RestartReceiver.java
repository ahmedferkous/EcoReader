package com.example.ecoreader;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class RestartReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();
        context.startService(new Intent(context, GetDataService.class));
    }
}
