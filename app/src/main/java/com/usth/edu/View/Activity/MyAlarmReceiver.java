package com.usth.edu.View.Activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.usth.edu.Database.Repository.JobRepository;
import com.usth.edu.Model.Job;

import java.util.List;
import java.util.stream.Collectors;

public class MyAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Thực hiện công việc ở đây
        JobRepository jobRepo = new JobRepository(context);
        List<Job> suggestedJobs = jobRepo.getListJobWillStartByMinute(5);

        if (!suggestedJobs.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Log.d("SOBIN NOTIFICATION", "jobSize = " + suggestedJobs.size() + ", " + suggestedJobs.stream().map(Job::getName).collect(Collectors.joining(", ")));
            }
            for (Job job : suggestedJobs) {
                // Gọi ChatGPT và gửi thông báo
            }
        } else {
            Log.d("SOBIN NOTIFICATION", "No jobs to process.");
        }
    }
}
