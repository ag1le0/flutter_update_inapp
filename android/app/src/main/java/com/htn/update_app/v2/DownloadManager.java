package com.fii.smartfactory.base.common.download;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;


public class DownloadManager implements OnProgress{
    private final Activity activity;
    private PowerManager.WakeLock mWakeLock;

    /**const*/
    private static final String TAG = "DownloadManager";

    private ProgressDialog mProgressDialog;

    private final DownloadTask downloadTask;

    public DownloadManager(Activity activity) {
        this.activity = activity;
        this.downloadTask = new DownloadTask(activity, this);

    }

    public void execute(String url, String fileName) {
        downloadTask.execute(url, fileName);
    }


    @Override
    public void onStart() {
        PowerManager pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire(60 * 1000L /*1 minutes*/);

        mProgressDialog = new ProgressDialog(activity);
        mProgressDialog.setTitle("Downloading...");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    @Override
    public void onDownloading(Integer percent) {
        mProgressDialog.setIndeterminate(false);
        mProgressDialog.setMax(100);
        mProgressDialog.setProgress(percent);
    }

    @Override
    public void onError(String error) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        Toast.makeText(activity, "ERROR: Download error: " + error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onSuccess(File file) {
        mWakeLock.release();
        mProgressDialog.dismiss();
        installApp(activity, file);
    }

    void installApp(Activity activity, File file){
        Intent intent;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Uri apkUri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", file);
            intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
            intent.setData(apkUri);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } else {
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        activity.startActivity(intent);

    }
}
