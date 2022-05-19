package com.htn.update_app;

import android.util.Log;

import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;

public class MainActivity extends FlutterActivity {
    private static final String TAG = "FlutterActivity";

    @Override
    public void configureFlutterEngine(FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);

        try {
            flutterEngine.getPlugins().add(new UpdatePlugin());
        } catch(Exception e) {
            Log.e(TAG, "Error registering plugin UpdatePlugin, com.htn.update_app.UpdatePlugin()", e);
        }

    }
}
