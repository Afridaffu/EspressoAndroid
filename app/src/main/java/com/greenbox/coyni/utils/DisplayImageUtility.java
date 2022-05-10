package com.greenbox.coyni.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.greenbox.coyni.model.profile.DownloadImageResponse;
import com.greenbox.coyni.model.profile.DownloadUrlRequest;
import com.greenbox.coyni.network.ApiService;
import com.greenbox.coyni.network.AuthApiClient;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayImageUtility {

    private Context context;
    private static DisplayImageUtility sInstance;
    private HashMap<String, ImageView> imageIdMap;
    private HashMap<String, Integer> imagePlaceHolderMap;

    private DisplayImageUtility(Context context) {
        this.context = context;
        imageIdMap = new HashMap<>();
        imagePlaceHolderMap = new HashMap<>();
    }

    public static synchronized DisplayImageUtility getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DisplayImageUtility(context);
        }
        return sInstance;
    }

    public void addImage(String key, ImageView imageView, Integer resId) {

        if (!android.util.Patterns.WEB_URL.matcher(key).matches()) {
            imageIdMap.put(key, imageView);
            imagePlaceHolderMap.put(key, resId);
            DownloadUrlRequest downloadUrlRequest = new DownloadUrlRequest();
            downloadUrlRequest.setKey(key);
            getDownloadUrl(downloadUrlRequest);
        } else {
            setImageWithUrl(key, imageView, resId);
        }

    }

    private void setData(DownloadImageResponse response) {
        if (response == null || response.getStatus() == null
                || !response.getStatus().equalsIgnoreCase(Utils.SUCCESS)) {
            return;
        }
        if (response.getData() != null && response.getData().getKey() != null) {
            int placeholder = imagePlaceHolderMap.remove(response.getData().getKey());
            ImageView iv = imageIdMap.remove(response.getData().getKey());
            if (iv != null) {
                setImageWithUrl(response.getData().getDownloadUrl(), iv, placeholder);
            }
        }
    }

    private void setImageWithUrl(String url, ImageView iv, Integer placeHolder) {
        Glide.with(context)
                .load(url)
                .placeholder(placeHolder)
                .into(iv);
    }

    private void getDownloadUrl(DownloadUrlRequest downloadUrlRequest) {
        try {
            ApiService apiService = AuthApiClient.getInstance().create(ApiService.class);
            Call<DownloadImageResponse> mCall = apiService.getDownloadUrl(downloadUrlRequest);
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
