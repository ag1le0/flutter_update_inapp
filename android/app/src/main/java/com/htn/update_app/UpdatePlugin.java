package com.htn.update_app;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import java.util.Map;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.PluginRegistry;

public class UpdatePlugin implements FlutterPlugin, EventChannel.StreamHandler {
    //CONSTANTS
    private static final String ARG_URL = "url";
    private static final String FILE_NAME = "file_name";
    public static final String TAG = "UpdatePlugin";
    //BASIC PLUGIN STATE
    private Context context;
    private Activity activity;
    private EventChannel.EventSink progressSink;
    private static String url = "";
    private static String fileName = "";


    private void initialize(Context context, BinaryMessenger messenger) {
        final EventChannel progressChannel = new EventChannel(messenger, "com.htn.update_app");
        progressChannel.setStreamHandler(this);
        this.context = context;
    }


    public static void registerWith(PluginRegistry.Registrar registrar) {
        UpdatePlugin plugin = new UpdatePlugin();
        plugin.initialize(registrar.context(), registrar.messenger());
        plugin.activity = registrar.activity();
    }


    @Override
    public void onAttachedToEngine(FlutterPluginBinding binding) {
        initialize(binding.getApplicationContext(), binding.getBinaryMessenger());
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
    }

    @Override
    public void onListen(Object arguments, EventChannel.EventSink events) {
        if (progressSink != null) {
            progressSink.error("" + Status.ALREADY_RUNNING_ERROR.ordinal(), Status.ALREADY_RUNNING_ERROR.name(), "Method call was cancelled. One method call is already running!");
        }
        Log.d(TAG, "STREAM OPENED");
        progressSink = events;
        Map argumentsMap = (Map) arguments;
        url = argumentsMap.get(ARG_URL).toString();
        fileName = argumentsMap.get(FILE_NAME).toString();

        executeDownload();
    }


    @Override
    public void onCancel(Object o) {
        Log.d(TAG, "STREAM CLOSED");
        progressSink = null;
    }

    private void executeDownload() {
        new DownloadManager(
                context,
                progressSink).execute(url, fileName);
    }
}
