package com.usth.edu.Model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.usth.edu.Database.DateConvertor;

import java.io.Serializable;
import java.util.Date;

@Entity(foreignKeys = @ForeignKey(entity = Job.class,
        parentColumns = "ID",
        childColumns = "JobID",
        onDelete = ForeignKey.CASCADE))
public class NotificationModel implements Serializable {

    @ColumnInfo(name = "ID")
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "JobID", index = true)
    private int jobId;

    @ColumnInfo(name = "Message")
    private String message;

    @ColumnInfo(name = "statusJob")
    private int statusJob;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "DateOfRecord")
    @TypeConverters({DateConvertor.class})
    @NonNull
    private Date dateOfRecord;

    public NotificationModel(int jobId,int statusJob,Date dateOfRecord,String status) {
        this.jobId = jobId;
        this.statusJob =statusJob;
        this.dateOfRecord=dateOfRecord;
        this.status =status;
    }

    @Ignore
    public NotificationModel(int jobId, String message, int statusJob, @NonNull Date dateOfRecord, String status) {
        this.jobId = jobId;
        this.message = message;
        this.statusJob = statusJob;
        this.status = status;
        this.dateOfRecord = dateOfRecord;
    }

    @NonNull
    public Date getDateOfRecord() {
        return dateOfRecord;
    }

    public void setDateOfRecord(@NonNull Date dateOfRecord) {
        this.dateOfRecord = dateOfRecord;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJobId() {
        return jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public int getStatusJob() {
        return statusJob;
    }

    public void setStatusJob(int statusJob) {
        this.statusJob = statusJob;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
