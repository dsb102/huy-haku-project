package com.usth.edu.Application;

import android.app.AlarmManager;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.usth.edu.Database.DataLocal.DataLocalManager;
import com.usth.edu.Library.Key;
import com.usth.edu.R;
import com.usth.edu.View.Activity.MyAlarmReceiver;
import com.usth.edu.View.Activity.MyWorker;

import java.util.concurrent.TimeUnit;

public class myApplication extends Application {

    private static boolean isAlarm = false;

    @Override
    public void onCreate() {
        super.onCreate();
        DataLocalManager.init(getApplicationContext());
        createChanelNotification();
//        setJobPeriodic();
        Log.d("SOBIN MYAPPLICATION", "onCreate");
        if (!isAlarm) {
            setAlarmManager();
            isAlarm = true;
        }
    }

    private void setAlarmManager() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, MyAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        long interval = 5 * 60 * 1000; // 5 phút
        long triggerAtMillis = System.currentTimeMillis() + interval;

        if (alarmManager != null) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtMillis, interval, pendingIntent);
        }
    }

//    private void setJobPeriodic() {
//        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(MyWorker.class, 5, TimeUnit.MINUTES)
//                .build();
//        WorkManager.getInstance(this).enqueueUniquePeriodicWork("ScheduleJobReminder", ExistingPeriodicWorkPolicy.REPLACE, workRequest);
//    }

    private void createChanelNotification() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(
                    Key.CHANNEL_NOTIFICATION,
                    this.getString(R.string.notification_job),
                    NotificationManager.IMPORTANCE_NONE);
            NotificationChannel channelCountUp = new NotificationChannel(
                    Key.CHANNEL_COUNT_UP,
                    this.getString(R.string.notification_count_up),
                    NotificationManager.IMPORTANCE_NONE);
            NotificationChannel notificationJobChannel = new NotificationChannel(
                    Key.CHANNEL_NOTIFICATION_JOB,
                    this.getString(R.string.notification_job),
                    NotificationManager.IMPORTANCE_HIGH);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if(manager !=null){
                manager.createNotificationChannel(channelCountUp);
                manager.createNotificationChannel(notificationChannel);
                manager.createNotificationChannel(notificationJobChannel);
            }

        }
    }


}
