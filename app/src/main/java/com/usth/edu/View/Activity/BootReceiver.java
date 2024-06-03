package com.usth.edu.View.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Objects.equals(intent.getAction(), Intent.ACTION_BOOT_COMPLETED)) {
            PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 5, TimeUnit.MINUTES)
                    .build();
            WorkManager.getInstance(context).enqueue(workRequest);
        }
    }
}
