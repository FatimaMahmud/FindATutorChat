package com.exception.findatutor.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.exception.findatutor.R;
import com.exception.findatutor.conn.MongoDB;



class ThreadNotification extends Thread {
    @SuppressLint("StaticFieldLeak")
    private static ThreadNotification threadNotification;
    private String username;
    private Context context;
    private MongoDB mongoDB;

    private ThreadNotification(Context context,String name) {
        this.username = name;
        this.context = context;
        mongoDB = MongoDB.getInstance();
    }

    public static ThreadNotification getInstance(Object... params) {
        if (threadNotification == null) {
            MainActivity.count = 0;
            threadNotification = new ThreadNotification((Context) params[0],(String) params[1]);
        }
        return threadNotification;
    }

    @Override
    public void run() {
        try {
            if (mongoDB.CheckRegisteringAsForNotification(username).equals("tutor")) {
                MainActivity.count = 0;
                if (mongoDB.CheckTutorNotification(username, "true").equals("sent")) {
                    NotificationGenerator("Student Wants to connect", "Click to view profile", StudentProfile.class,
                            mongoDB.TutorNotificationSenderName(LoginActivity.uname));
//                        mongoDB.UpdateTheStudentNOtification(mongoDB.TutorNotificationSenderName(LoginActivity.uname),
//                                "accepted", LoginActivity.uname);
//                        mongoDB.UpdateTheTutorNOtification(username, "accepted", LoginActivity.uname);

                }
            }
            if (mongoDB.CheckRegisteringAsForNotification(username).equals("student")) {
                MainActivity.count = 0;
                if (mongoDB.CheckStudentNotification(username, "accepted").equals("accepted")) {
                    NotificationGenerator("Tutor has accepted your request", "Click to view full profile", ProfileActivity.class ,
                            mongoDB.StudentNotificationSenderName(LoginActivity.uname));
                    mongoDB.UpdateTheTutorNOtification(mongoDB.StudentNotificationSenderName(LoginActivity.uname), "false", null);
                    mongoDB.UpdateTheStudentNOtification(username, "false", null);
                }
            }
        }
        catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void NotificationGenerator(String title, String message, final Class<? extends Activity> ActivityToOpen, String username ) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.logo)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentTitle(title)
                        .setContentText(message);
        Intent resultIntent = new Intent(context, ActivityToOpen);
        resultIntent.putExtra("username", username);
        resultIntent.putExtra("state", "card");
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(ActivityToOpen);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());

    }

}
