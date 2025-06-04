package com.example.financeapp.ui.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.financeapp.R;
import com.example.financeapp.MainActivity;
import com.example.financeapp.ui.database.AppDatabase;
import com.example.financeapp.ui.models.BillReminder;

import javax.xml.transform.Result;

public class BillNotificationWorker extends Worker {
    public BillNotificationWorker(@NonNull Context context, @NonNull WorkerParameters params) { super(context, params); }

    @NonNull
    @Override
    public Result doWork() {
        int reminderId = getInputData().getInt("reminderId", -1);
        if (reminderId == -1) return Result.failure();

        BillReminder reminder = AppDatabase.getDatabase(getApplicationContext()).billReminderDao().getByIdSync(reminderId);
        if (reminder == null || reminder.paid) return Result.success();


        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        String channelId = "bill_reminders";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Przypomnienia o rachunkach", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("reminderId", reminderId);
        PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), reminderId, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), channelId)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle(reminder.title)
                .setContentText(reminder.message)
                .setContentIntent(pi)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        nm.notify(reminderId, builder.build());
        return Result.success();
    }
}