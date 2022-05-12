package com.greenbox.coyni.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.LruCache;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.greenbox.coyni.model.profile.DownloadImageData;
import com.greenbox.coyni.model.profile.DownloadImageResponse;
import com.greenbox.coyni.model.profile.DownloadUrlRequest;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayImageUtility {
    public final String TAG = getClass().getName();
    private Context context;
    private static DisplayImageUtility sInstance;
    private HashMap<String, List<ImageView>> imageIdMap;
    private HashMap<String, String> tempMap;
    private LruCache<String, Bitmap> imageCache;

    public static class ImageHolder {
        public String key;
        public ImageView imageView;
        public Integer resId;
    }

    private DisplayImageUtility(Context context) {
        this.context = context;
        imageIdMap = new HashMap<>();
        tempMap = new HashMap<>();
        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
        final int cacheSize = maxMemory / 8;
        imageCache = new LruCache<>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public static synchronized DisplayImageUtility getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DisplayImageUtility(context);
        }
        return sInstance;
    }

    public void addImages(List<ImageHolder> imagesList) {
        if (imagesList == null) {
            return;
        }
        ArrayList<DownloadUrlRequest> urlList = new ArrayList<>();
        for (ImageHolder holder : imagesList) {
            if (!android.util.Patterns.WEB_URL.matcher(holder.key).matches()) {
                if (imageCache.get(holder.key) != null) {
                    LogUtils.v(TAG, "from cache");
                    holder.imageView.setImageBitmap(imageCache.get(holder.key));
                    return;
                }
                holder.imageView.setImageResource(holder.resId);
                if (!imageIdMap.containsKey(holder.key)) {
                    imageIdMap.put(holder.key, new ArrayList<>());
                }
                imageIdMap.get(holder.key).add(holder.imageView);
                DownloadUrlRequest downloadUrlRequest = new DownloadUrlRequest();
                downloadUrlRequest.setKey(holder.key);
                urlList.add(downloadUrlRequest);
            } else {
                holder.imageView.setImageResource(holder.resId);
                getImageFromUrl(holder.key);
            }
        }
        if (urlList.size() > 0) {
            getDownloadUrl(urlList);
        }
    }

    public void addImage(String key, ImageView imageView, Integer resId) {
        if (!android.util.Patterns.WEB_URL.matcher(key).matches()) {
            if (imageCache.get(key) != null) {
                LogUtils.v(TAG, "from cache");
                imageView.setImageBitmap(imageCache.get(key));
                return;
            }
            imageView.setImageResource(resId);
            if (!imageIdMap.containsKey(key)) {
                imageIdMap.put(key, new ArrayList<>());
            }
            imageIdMap.get(key).add(imageView);
            DownloadUrlRequest downloadUrlRequest = new DownloadUrlRequest();
            downloadUrlRequest.setKey(key);
            ArrayList<DownloadUrlRequest> urlList = new ArrayList<>();
            urlList.add(downloadUrlRequest);
            LogUtils.v(TAG, "from url " + key);
            getDownloadUrl(urlList);
        } else {
            imageView.setImageResource(resId);
            getImageFromUrl(key);
        }
    }

    public void clearCache() {

    }

    private void setData(DownloadImageResponse response) {
        if (response == null || response.getStatus() == null
                || !response.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
            return;
        }

        List<DownloadImageData> dataList = response.getData();
        if (dataList != null) {
            for (DownloadImageData data : dataList) {
                tempMap.put(data.getDownloadUrl(), data.getKey());
                getImageFromUrl(data.getDownloadUrl());
            }
        }
    }

    private void updateViews(String key) {
        List<ImageView> ivList = imageIdMap.remove(key);
        if (ivList != null) {
            for (int i = 0; i < ivList.size(); i++) {
                ivList.get(i).setImageBitmap(imageCache.get(key));
            }
        }
    }

    private void getImageFromUrl(String url) {
        LogUtils.v(TAG, "Glide load started");
        Glide.with(context)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        String key = tempMap.remove(url);
                        LogUtils.v(TAG, "Glide resource ready");
                        imageCache.put(key, bitmap);
                        updateViews(key);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        LogUtils.v(TAG, "Glide ronLoadCleared");
                    }
                });
    }

    private void getDownloadUrl(List<DownloadUrlRequest> downloadUrlRequestList) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DownloadImageResponse> mCall = apiService.getDownloadUrl(downloadUrlRequestList);
            mCall.enqueue(new Callback<DownloadImageResponse>() {
                @Override
                public void onResponse(Call<DownloadImageResponse> call, Response<DownloadImageResponse> response) {
                    try {
                        if (response.isSuccessful()) {
                            DownloadImageResponse obj = response.body();
                            setData(obj);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<DownloadImageResponse> call, Throwable t) {
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
