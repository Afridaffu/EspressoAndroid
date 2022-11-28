package com.coyni.mapp.custom_camera;

public interface OnPictureTakenListener {
    void onPictureTakingCompleted(byte[] data, int rotation);
    void onPictureTakingCancelled();
}
