package com.usth.edu.View.Activity;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Intent alarmIntent = new Intent(context, MyAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);

            long interval = 5 * 60 * 1000; // 5 phút
            long triggerAtMillis = System.currentTimeMillis() + interval;

            if (alarmManager != null) {
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, interval, pendingIntent);
            }
        }
    }
}
