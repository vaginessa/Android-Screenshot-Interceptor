package me.nicolasschelkens.screenshotinterceptor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import java.lang.ref.WeakReference;

import me.nicolasschelkens.screenshotinterceptor.api.model.ImageResponse;


public class NotificationHelper {
    private WeakReference<Context> mContext;

    public NotificationHelper(Context context) {
        this.mContext = new WeakReference<>(context);
    }

    public void createInterceptedNotfication() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setContentTitle("Screenshot intercepted")
                .setContentText("Screenshot uploading...")
                .setSmallIcon(R.drawable.ic_cloud_upload_white_24dp);
        mBuilder.setProgress(0, 0, true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode()+1, mBuilder.build());
    }

    public Notification getRunningNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setContentTitle("Screenshot interceptor is running")
                .setContentText("Watching for screenshots to intercept")
                .setSmallIcon(R.mipmap.ic_launcher);

        Intent deleteIntent = new Intent(mContext.get(), InterceptorService.class);
        PendingIntent deletePendingIntent = PendingIntent.getService(mContext.get(),
                2,
                deleteIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setDeleteIntent(deletePendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = mBuilder.build();
        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), notification);
        return notification;
    }

    public void createUploadedNotification(ImageResponse response) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        mBuilder.setContentTitle("Upload success");
        mBuilder.setPriority(Notification.PRIORITY_MAX);
        mBuilder.setContentText(response.getData().getLink());
        mBuilder.setSmallIcon(R.drawable.ic_cloud_done_white_24dp);

        mBuilder.setColor(ContextCompat.getColor(mContext.get(), R.color.colorPrimary));
        mBuilder.setDefaults(Notification.DEFAULT_VIBRATE);

        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(response.getData().getLink()));
        PendingIntent intent = PendingIntent.getActivity(mContext.get(), 0, resultIntent, 0);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(response.getData().getLink()));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, response.getData().getLink());
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pIntent = PendingIntent.getActivity(mContext.get(), 0, shareIntent, 0);
        mBuilder.addAction(new NotificationCompat.Action(R.drawable.abc_ic_menu_share_mtrl_alpha,
                "Share link", pIntent));

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode()+1, mBuilder.build());
    }


    public void createFailedUploadNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle("Upload failed");


        mBuilder.setColor(ContextCompat.getColor(mContext.get(), R.color.colorPrimary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode()+1, mBuilder.build());
    }
}
