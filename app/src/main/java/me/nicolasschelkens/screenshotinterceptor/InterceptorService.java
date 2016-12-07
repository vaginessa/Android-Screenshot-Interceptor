package me.nicolasschelkens.screenshotinterceptor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import me.nicolasschelkens.screenshotinterceptor.api.ApiClient;
import me.nicolasschelkens.screenshotinterceptor.api.ImgurInterface;
import me.nicolasschelkens.screenshotinterceptor.api.model.ImageResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InterceptorService extends Service {
    private ScreenshotObserver screenshotObserver;
    private NotificationHelper notificationHelper;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);

        screenshotObserver = new ScreenshotObserver(null);

        notificationHelper = new NotificationHelper(this);
        Notification notification  = notificationHelper.getRunningNotification();
        startForeground(getString(R.string.app_name).hashCode(), notification);
        getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                true,
                screenshotObserver
        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void screenshotIntercepted(ScreenshotEvent screenshotEvent) {
        notificationHelper.createInterceptedNotfication();

        java.io.File pictureFile = new java.io.File(screenshotEvent.getPath());
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/png"), pictureFile);
        ApiClient.createService(ImgurInterface.class).postImage(
                MultipartBody.Part.createFormData("image", pictureFile.getName(), requestFile)
        ).enqueue(new Callback<ImageResponse>() {
            @Override
            public void onResponse(Call<ImageResponse> call, Response<ImageResponse> response) {
                if(response.isSuccessful()) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText("screenshot", response.body().getData().getLink()));
                    notificationHelper.createUploadedNotification(response.body());
                }
            }

            @Override
            public void onFailure(Call<ImageResponse> call, Throwable t) {
                t.printStackTrace();
                notificationHelper.createFailedUploadNotification();
            }
        });
    }

}

