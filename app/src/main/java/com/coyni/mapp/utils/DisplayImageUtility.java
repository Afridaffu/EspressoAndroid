package com.coyni.mapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.LruCache;
import android.widget.ImageView;

import com.coyni.mapp.model.profile.DownloadImageData;
import com.coyni.mapp.model.profile.DownloadImageResponse;
import com.coyni.mapp.model.profile.DownloadUrlRequest;
import com.coyni.mapp.network.ApiService;
import com.coyni.mapp.network.AuthApiClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayImageUtility {
    public final String TAG = getClass().getName();
    private Context context;
    private static DisplayImageUtility sInstance;
    private HashMap<String, List<ImageView>> imageIdMap;
    private LinkedHashMap<String, String> tempMap;
    private LruCache<String, Bitmap> imageCache;

    public static class ImageHolder {
        public String key;
        public ImageView imageView;
        public Integer resId;
    }

    private DisplayImageUtility(Context context) {
        this.context = context;
        imageIdMap = new HashMap<>();
        tempMap = new LinkedHashMap<>();
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
                    continue;
                }
                holder.imageView.setImageResource(holder.resId);
                if (!imageIdMap.containsKey(holder.key)) {
                    imageIdMap.put(holder.key, new ArrayList<>());
                    DownloadUrlRequest downloadUrlRequest = new DownloadUrlRequest();
                    downloadUrlRequest.setKey(holder.key);
                    urlList.add(downloadUrlRequest);
                }
                imageIdMap.get(holder.key).add(holder.imageView);
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
                LogUtils.v(TAG, "Glide from cache");
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
            if (imageCache.get(key) != null) {
                LogUtils.v(TAG, "Glide from cache1");
                imageView.setImageBitmap(imageCache.get(key));
                return;
            }
            tempMap.put(key, key);
            if (!imageIdMap.containsKey(key)) {
                imageIdMap.put(key, new ArrayList<>());
            }
            imageIdMap.get(key).add(imageView);
            getImageFromUrl(key);
        }
    }

    public void clearCache() {
        if(imageCache != null && imageCache.size() > 0) {
            imageCache.evictAll();
        }
    }

    private void setData(DownloadImageResponse response) {
        if (response == null || response.getStatus() == null
                || !response.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
            return;
        }

        List<DownloadImageData> dataList = response.getData();
        if (dataList != null) {
            for (DownloadImageData data : dataList) {
                LogUtils.v(TAG, "Glide loop triger " + data.getKey());
                tempMap.put(data.getDownloadUrl(), data.getKey());
                getImageFromUrl(data.getDownloadUrl());
            }
        }

    }

    private void updateViews(String key) {
        if(key == null || key.equals("")) {
            return;
        }
        List<ImageView> ivList = imageIdMap.remove(key);
        if (ivList != null) {
            for (int i = 0; i < ivList.size(); i++) {
                ivList.get(i).setImageBitmap(imageCache.get(key));
            }
        }
    }

    public class GetImageFromUrl extends AsyncTask<Void, Void, String> {
        private String url;
        public GetImageFromUrl(String url) {
            this.url = url;
        }

        @Override
        protected String doInBackground(Void... vals) {
            InputStream inputStream;
            String key = tempMap.remove(url);
            try {
                inputStream = new java.net.URL(url).openStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                LogUtils.v(TAG, url + " - Glide Image resource ready " + key);
                if(key != null && bitmap != null) {
                    imageCache.put(key, bitmap);
                }
            } catch (IOException e) {
                LogUtils.v(TAG, "Glide IOException " + url);
                e.printStackTrace();
            }
            return key;
        }

        @Override
        protected void onPostExecute(String key){
            super.onPostExecute(key);
            updateViews(key);
        }
    }

    private synchronized void getImageFromUrl(final String url) {
        LogUtils.v(TAG, "Glide load started");
        new GetImageFromUrl(url).execute();
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
