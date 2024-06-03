package com.usth.edu.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.usth.edu.Library.Action;
import com.usth.edu.Library.Key;

public class CountUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(@NonNull Context context, Intent intent) {
        int action = intent.getIntExtra(Key.SEND_ACTION, Action.NONE);
        Intent service = new Intent(context, CountUpService.class);
        service.putExtra(Key.SEND_ACTION, action);
        context.startService(service);
    }
}
