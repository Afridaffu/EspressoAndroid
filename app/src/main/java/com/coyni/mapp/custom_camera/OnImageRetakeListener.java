package com.coyni.mapp.custom_camera;

import java.io.File;

public interface OnImageRetakeListener {

    void onImageRetake();

    void onImageSaved(File file);
}
