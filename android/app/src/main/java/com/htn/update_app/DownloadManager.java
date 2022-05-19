package com.htn.update_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import androidx.core.content.FileProvider;
import java.io.File;
import java.util.Arrays;
import io.flutter.plugin.common.EventChannel;


public class DownloadManager implements OnProgress {
    private final Context context;
    private final EventChannel.EventSink progressSink;
    private final DownloadTask downloadTask;

    private static final String TAG = "DownloadManager";


    public DownloadManager(Context context, EventChannel.EventSink progressSink) {
        this.context = context;
        this.progressSink = progressSink;
        this.downloadTask = new DownloadTask(this);
    }

    public void execute(String url, String fileName) {
        downloadTask.execute(url, fileName);
    }


    @Override
    public void onStart() {
        Log.d(TAG, "onStart: "+Status.STARTED.name());
        progressSink.success(Arrays.asList(Status.STARTED.ordinal() + "", ""));
    }

    @Override
    public void onDownloading(Integer percent) {
        Log.d(TAG, "onDownloading: "+Status.DOWNLOADING.name());
        progressSink.success(Arrays.asList(Status.DOWNLOADING.ordinal() + "", percent + ""));
    }

    @Override
    public void onError(String error) {
        Log.d(TAG, "onError: "+Status.DOWNLOAD_ERROR.name()+": "+error);
        progressSink.success(Arrays.asList(Status.DOWNLOAD_ERROR.ordinal() + "", error));
        progressSink.endOfStream();
    }

    @Override
    public void onSuccess(File file) {
        Log.d(TAG, "onSuccess: "+Status.DOWNLOADED.name()+": "+file.getPath());
        progressSink.success(Arrays.asList(Status.DOWNLOADED.ordinal() + "", ""));
        installApp(context, file);
    }

    void installApp(Context context, File file){
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(context, context.getPackageName() + ".fileprovider", file);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        Log.d(TAG, "installApp: "+Status.INSTALLING.name()+": "+file.getPath());
        progressSink.success(Arrays.asList(Status.INSTALLING.ordinal() + "", ""));
        progressSink.endOfStream();
        context.startActivity(intent);

    }
}
