package com.greenbox.coyni.utils;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.MediaStore.Files;
import android.provider.MediaStore.Files.FileColumns;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils {
    private static final String TAG = "FileUtils";

    @WorkerThread
    @Nullable
    public static String getReadablePathFromUri(Context context, Uri uri) {

        Log.e("uri.getAuthority()", uri.getAuthority());
        String path = null;
        if ("file".equalsIgnoreCase(uri.getScheme())) {
            path = uri.getPath();
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            path = getPath(context, uri);
        }

        if (TextUtils.isEmpty(path)) {
            return path;
        }

        Log.d(TAG, "get path from uri: " + path);
        if (!isReadablePath(path)) {
            int index = path.lastIndexOf("/");
            String name = path.substring(index + 1);
            String dstPath = context.getCacheDir().getAbsolutePath() + File.separator + name;
            if (copyFile(context, uri, dstPath)) {
                path = dstPath;
                Log.d(TAG, "copy file success: " + path);
            } else {
                Log.d(TAG, "copy file fail!");
            }
        }
        return path;
    }

    public static String getPath(final Context context, final Uri uri) {
        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                Log.d("External Storage", docId);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            } else if (isDownloadsDocument(uri)) {

                String dstPath = context.getCacheDir().getAbsolutePath() + File.separator + getFileName(context, uri);

                if (copyFile(context, uri, dstPath)) {
                    Log.d(TAG, "copy file success: " + dstPath);
                    return dstPath;

                } else {
                    Log.d(TAG, "copy file fail!");
                }


            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentUri  = MediaStore.Downloads.getContentUri("internal");
                    }
//                    ContentResolver cr = context.getContentResolver();
//                    contentUri = Files.getContentUri("external");
//                    contentUri = ContentUris.withAppendedId(Files.FileColumns("external"),
//                            getIdentForDocId(docId).id);
//                    contentUri = MediaStore.Files.getContentUri("internal", Long.parseLong(docId));


//                    Cursor cursor = cr.query(contentUri, null, null, null, null);

//                    getFileName(context, uri);
//                    final String selection = "_id=?";
//                    final String[] selectionArgs = new String[]{split[1]};
//                    String[] proj = {FileColumns.DATA};
//                    Cursor cursor = context.getContentResolver().query(contentUri, proj, selection, selectionArgs, null);
//                    do {
//                        if (cursor == null) return null;
//                        int column_index = cursor.getColumnIndexOrThrow(FileColumns.DATA);
//                        cursor.moveToFirst();
//                        Log.e("path", cursor.getString(column_index));
//                    } while (cursor.moveToNext());


//                    ---------------------------------------


//                    try {
//
//                        if (cursor != null && cursor.moveToFirst()) {
//                            do {
//                                @SuppressLint("Range") String fileID = cursor.getString(cursor.getColumnIndex(MediaStore.Files.FileColumns.RELATIVE_PATH));
//                                Log.d(TAG, "fileID = " + fileID+  "  "+split[1]);
//                                Uri fileUri = null;
//                                if (split[1].equals(fileID)) {
//                                    @SuppressLint("Range") long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Files.FileColumns._ID));
//                                    fileUri = ContentUris.withAppendedId(MediaStore.Files.getContentUri("external"), id);
//
//                                }
//                                Log.d(TAG, "file uri = " + fileUri);
//                            } while (cursor.moveToNext());
//                            cursor.close();
//                        }
//                    } finally {
//                        if (cursor != null)
//                            cursor.close();
//                    }
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static String getFileName(Context context, Uri uri) {

        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        int nameindex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
        cursor.moveToFirst();

        return cursor.getString(nameindex);
    }


    private static String getDataColumn(Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private static boolean isReadablePath(@Nullable String path) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        boolean isLocalPath;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (!TextUtils.isEmpty(path)) {
                File localFile = new File(path);
                isLocalPath = localFile.exists() && localFile.canRead();
            } else {
                isLocalPath = false;
            }
        } else {
            isLocalPath = path.startsWith(File.separator);
        }
        return isLocalPath;
    }

    private static boolean copyFile(Context context, Uri uri, String dstPath) {
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = context.getContentResolver().openInputStream(uri);
            outputStream = new FileOutputStream(dstPath);

            byte[] buff = new byte[100 * 1024];
            int len;
            while ((len = inputStream.read(buff)) != -1) {
                outputStream.write(buff, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    private static class Ident {
        public String type;
        public long id;
    }

    private static Ident getIdentForDocId(String docId) {
        final Ident ident = new Ident();
        final int split = docId.indexOf(':');
        if (split == -1) {
            ident.type = docId;
            ident.id = -1;
        } else {
            ident.type = docId.substring(0, split);
            ident.id = Long.parseLong(docId.substring(split + 1));
        }
        return ident;
    }
}