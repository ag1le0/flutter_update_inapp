package com.htn.update_app;


import java.io.File;

public interface OnProgress {
    void onStart();
    void onDownloading(Integer percent);
    void onError(String error);
    void onSuccess(File file);
}
