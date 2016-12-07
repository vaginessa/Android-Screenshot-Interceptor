package me.nicolasschelkens.screenshotinterceptor;

import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;

import org.greenrobot.eventbus.EventBus;

public class ScreenshotObserver extends ContentObserver {

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public ScreenshotObserver(Handler handler) {
        super(handler);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        if (uri.toString().startsWith(MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString())) {
            Cursor cursor = null;
            try {
                cursor = MyApplication.getAppContext().getContentResolver().query(uri, new String[] {
                                MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
                                MediaStore.Images.Media.DATE_ADDED
                        }, null, null,
                        MediaStore.Images.Media.DATE_ADDED + " DESC");
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    if (path.toLowerCase().contains("screenshot")) {
                        EventBus.getDefault().post(new ScreenshotEvent(path));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        super.onChange(selfChange, uri);
    }
}
