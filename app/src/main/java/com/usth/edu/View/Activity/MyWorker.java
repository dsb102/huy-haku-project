package com.usth.edu.View.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.stream.Collectors;

import com.usth.edu.Database.Repository.JobRepository;
import com.usth.edu.Library.CalendarExtension;
import com.usth.edu.Library.GeneralData;
import com.usth.edu.Model.Job;
import com.usth.edu.Model.NotificationModel;
import com.usth.edu.R;
import com.usth.edu.Utils.ChatGptCaller;
import com.usth.edu.ViewModel.NotificationViewModel;

public class MyWorker extends Worker {

    private final JobRepository jobRepo = new JobRepository(getApplicationContext());

    private static final String CHANNEL_ID = "CalendarNotificationChannel";

    private NotificationViewModel notificationViewModel;

    RemoteViews remoteViews;

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        createNotificationChannel();
        notificationViewModel = new NotificationViewModel();
        notificationViewModel.setData(context);
    }

    private static final ChatGptCaller caller = new ChatGptCaller();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        List<Job> suggestedJobs = jobRepo.getListJobWillStartByMinute(5);
        Log.d("SOBIN NOTIFICATION", "jobSize = " + suggestedJobs.size() + ", " + suggestedJobs.stream().map(Job::getName).collect(Collectors.joining(", ")));
        suggestedJobs.forEach(
                job -> caller.callChatGpt(
                        job.getStartDate(),
                        job.getEndDate(),
                        job.getName(),
                        job.getDescription(),
                        result -> {
                            NotificationModel notificationModel = new NotificationModel(job.getId(), result, job.getStatus(), CalendarExtension.currDate(), GeneralData.STATUS_NOTIFICATION_ACTIVE);
                            notificationViewModel.insert(notificationModel);
                            Log.d("SOBIN NOTIFICATION", "message: " + result);
                            sendNotification("\uD83D\uDCE2 ⏰ Nhắc nhở chuẩn bị cho công việc ⏰ \uD83D\uDCE2" + job.getName(), result);
                        })
        );
        return Result.success();
    }

    private void sendNotification(String title, String message) {
        Context context = getApplicationContext();
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_final)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Calendar Notification Channel";
            String description = "Channel for calendar notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getApplicationContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
