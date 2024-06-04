package com.usth.edu.View.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.usth.edu.Database.Repository.JobRepository;
import com.usth.edu.Library.CalendarExtension;
import com.usth.edu.Library.GeneralData;
import com.usth.edu.Model.Job;
import com.usth.edu.Model.NotificationModel;
import com.usth.edu.R;
import com.usth.edu.Utils.ChatGptCaller;
import com.usth.edu.ViewModel.NotificationViewModel;

import java.util.List;
import java.util.stream.Collectors;

public class MyAlarmReceiver extends BroadcastReceiver {

    private static final ChatGptCaller caller = new ChatGptCaller();

    private static final String CHANNEL_ID = "CalendarNotificationChannel";

    private JobRepository jobRepo;
    private NotificationViewModel notificationViewModel;

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);
        jobRepo = new JobRepository(context);
        notificationViewModel = new NotificationViewModel();
        notificationViewModel.setData(context);
        // Thực hiện công việc ở đây
        JobRepository jobRepo = new JobRepository(context);
        List<Job> suggestedJobs = jobRepo.getListJobWillStartByMinute(5);

        if (!suggestedJobs.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.d("SOBIN NOTIFICATION", "jobSize = " + suggestedJobs.size() + ", " + suggestedJobs.stream().map(Job::getName).collect(Collectors.joining(", ")));
            }
            for (Job job : suggestedJobs) {
                caller.callChatGpt(
                        job.getStartDate(),
                        job.getEndDate(),
                        job.getName(),
                        job.getDescription(),
                        result -> {
                            NotificationModel notificationModel = new NotificationModel(job.getId(), result, job.getStatus(), CalendarExtension.currDate(), GeneralData.STATUS_NOTIFICATION_ACTIVE);
                            notificationViewModel.insert(notificationModel);
                            Log.d("SOBIN NOTIFICATION", "message: " + result);
                            sendNotification("\uD83D\uDCE2 ⏰ Nhắc nhở chuẩn bị cho công việc ⏰ \uD83D\uDCE2" + job.getName(), result, context);
                        });
            }
        } else {
            Log.d("SOBIN NOTIFICATION", "No jobs to process.");
        }
    }

    private void sendNotification(String title, String message, Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo_final)
                .setContentIntent(pendingIntent)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }


    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Calendar Notification Channel";
            String description = "Channel for calendar notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
