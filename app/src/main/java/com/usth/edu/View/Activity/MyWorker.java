package com.usth.edu.View.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.List;
import java.util.stream.Collectors;

import com.usth.edu.Database.Repository.JobRepository;
import com.usth.edu.Model.Job;
import com.usth.edu.R;
import com.usth.edu.Utils.ChatGptCaller;

public class MyWorker extends Worker {

    private final JobRepository jobRepo = new JobRepository(getApplicationContext());

    private static final String CHANNEL_ID = "CalendarNotificationChannel";

    public MyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        createNotificationChannel();
    }

    private static final ChatGptCaller caller = new ChatGptCaller();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @NonNull
    @Override
    public Result doWork() {
        List<Job> suggestedJobs = jobRepo.getListJobWillStartByMinute(5);
        Log.d("SOBIN NOTIFICATION", "jobSize = " + suggestedJobs.size() + ", " + suggestedJobs.stream().map(Job::getName).collect(Collectors.joining(", ")));
        suggestedJobs.forEach(
                job -> {
                    if (job.getSuggestion() == null || job.getSuggestion().isEmpty() || job.getSuggestion().equals(job.getDescription())) {
                        caller.callChatGpt(
                                job.getStartDate(),
                                job.getEndDate(),
                                job.getName(),
                                job.getDescription(),
                                result -> sendNotification("Chuẩn bị cho công việc " + job.getName(), result));
                    } else {
                        sendNotification("Chuẩn bị cho công việc " + job.getName(), job.getSuggestion());
                    }
                }
        );
        return Result.success();
    }

    private void sendNotification(String title, String message) {
        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
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
