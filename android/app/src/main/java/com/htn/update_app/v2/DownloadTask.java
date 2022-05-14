package com.fii.smartfactory.base.common.download;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import com.fii.smartfactory.data.rest.HttpsTrustManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadTask extends AsyncTask<String, Integer, String> {


    private static final String TAG = "DownloadTask";
    private final Context context;
    private final OnProgress onProgress;
    private File file;

    public DownloadTask(Context context, OnProgress onProgress) {
        this.context = context;
        this.onProgress = onProgress;
    }

    @Override
    protected String doInBackground(String... strings) {
        InputStream input = null;
        OutputStream output = null;
        HttpsURLConnection connection = null;
        try {
            file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), strings[1] + ".apk");
            if (file.exists()) {
                if (!file.delete()) {
                    String error = "ERROR: unable to delete old apk file before starting OTA";
                    Log.e(TAG, error);
                    return error;
                }
            }
            if (!file.createNewFile()) {
                String error = "ERROR: unable to create apk file before starting OTA";
                Log.e(TAG, error);
                return error;
            }
            Log.d(TAG, "DOWNLOAD STARTING");


            HttpsTrustManager.allowAllSSL();
            URL url = new URL(strings[0]);
            connection = (HttpsURLConnection) url.openConnection();
            connection.connect();

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpsURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(file.toString());

            byte[] data = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        onProgress.onStart();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        onProgress.onDownloading(progress[0]);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onPostExecute(String result) {
        if (result != null || !file.exists()) {
            onProgress.onError("ERROR: Download error: " + result);
        } else {
            onProgress.onSuccess(file);
        }
    }
}