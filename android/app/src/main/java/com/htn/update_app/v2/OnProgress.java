package com.fii.smartfactory.base.common.download;

import java.io.File;

public interface OnProgress {
    void onStart();
    void onDownloading(Integer percent);
    void onError(String error);
    void onSuccess(File file);
}
