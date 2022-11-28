package com.coyni.mapp.custom_camera;

public class CameraUtility {

    public static final String CHOOSE_LIBRARY = "choose_library";
    public static final String TAKE_PHOTO = "take_photo";
    public static final String BROWSE = "browse";

    public static final String CAMERA_ACTION = "camera_action";
    public static final String SELECTING_ID = "selecting_id";
    public static final String TARGET_FILE = "target_file";

    public enum CAMERA_ACTION_SELECTOR {
        CAMERA_CROP,
        CAMERA_RETAKE,
        GALLERY,
        GALLERY_CROP,
        BROWSE
    }
}
