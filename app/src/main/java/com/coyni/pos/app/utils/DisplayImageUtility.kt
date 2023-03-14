package com.coyni.pos.app.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.util.LruCache
import android.util.Patterns
import android.widget.ImageView
import com.coyni.pos.app.model.downloadurl.DownloadUrlData
import com.coyni.pos.app.model.downloadurl.DownloadUrlRequest
import com.coyni.pos.app.model.downloadurl.DownloadUrlResponse
import com.coyni.pos.app.network.ApiService
import com.coyni.pos.app.network.AuthApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import java.net.URL

class DisplayImageUtility private constructor(private val context: Context) {
    val TAG = javaClass.name
    private val imageIdMap: HashMap<String?, MutableList<ImageView?>> = HashMap()
    private val tempMap: LinkedHashMap<String?, String> = LinkedHashMap()
    private val imageCache: LruCache<String?, Bitmap?>?

    class ImageHolder {
        var imageView: ImageView? = null
    }

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        imageCache = object : LruCache<String?, Bitmap?>(cacheSize) {
            override fun sizeOf(key: String?, bitmap: Bitmap?): Int {
                return bitmap?.byteCount?.div(1024)!!
            }
        }
    }

    fun addImage(key: String, imageView: ImageView, resId: Int?) {
        if (!Patterns.WEB_URL.matcher(key).matches()) {
            if (imageCache!![key] != null) {
                LogUtils.v(TAG, "Glide from cache")
                imageView.setImageBitmap(imageCache[key])
                return
            }
            imageView.setImageResource(resId!!)
            if (!imageIdMap.containsKey(key)) {
                imageIdMap[key] = ArrayList()
            }
            imageIdMap[key]!!.add(imageView)
            val downloadUrlRequest = DownloadUrlRequest(key)
            val urlList: ArrayList<DownloadUrlRequest> = ArrayList<DownloadUrlRequest>()
            urlList.add(downloadUrlRequest)
            LogUtils.v(TAG, "from url $key")
            getDownloadUrl(urlList)
        } else {
            imageView.setImageResource(resId!!)
            if (imageCache!![key] != null) {
                LogUtils.v(TAG, "Glide from cache1")
                imageView.setImageBitmap(imageCache[key])
                return
            }
            tempMap[key] = key
            if (!imageIdMap.containsKey(key)) {
                imageIdMap[key] = ArrayList()
            }
            imageIdMap[key]!!.add(imageView)
            getImageFromUrl(key)
        }
    }

    fun clearCache() {
        if (imageCache != null && imageCache.size() > 0) {
            imageCache.evictAll()
        }
    }

    private fun setData(response: DownloadUrlResponse?) {
        if (response?.status == null || !response.status.equals(Utils.SUCCESS)
        ) {
            return
        }
        val dataList: ArrayList<DownloadUrlData>? = response.data
        if (dataList != null) {
            for (data in dataList) {
                LogUtils.v(TAG, "Glide loop triger " + data.key)
                tempMap.put(data.downloadUrl, data.key.toString())
                getImageFromUrl(data.downloadUrl)
            }
        }
    }

    private fun updateViews(key: String?) {
        if (key == null || key == "") {
            return
        }
        val ivList: List<ImageView?>? = imageIdMap.remove(key)
        if (ivList != null) {
            for (i in ivList.indices) {
                ivList[i]!!.setImageBitmap(imageCache!![key])
            }
        }
    }

    inner class GetImageFromUrl(private val url: String?) :
        AsyncTask<Void?, Void?, String?>() {
        override fun doInBackground(vararg p0: Void?): String? {
            val inputStream: InputStream
            val key = tempMap.remove(url)
            try {
                inputStream = URL(url).openStream()
                val bitmap = BitmapFactory.decodeStream(inputStream)
                LogUtils.v(TAG, "$url - Glide Image resource ready $key")
                val nh = (bitmap.height * (512.0 / bitmap.width)).toInt()
                val scaled = Bitmap.createScaledBitmap(bitmap, 512, nh, true)
                if (key != null && scaled != null) {
                    imageCache!!.put(key, scaled)
                }
            } catch (e: IOException) {
                LogUtils.v(TAG, "Glide IOException $url")
                e.printStackTrace()
            }
            return key
        }

        override fun onPostExecute(key: String?) {
            super.onPostExecute(key)
            updateViews(key)
        }
    }

    @Synchronized
    private fun getImageFromUrl(url: String?) {
        LogUtils.v(TAG, "Glide load started")
        GetImageFromUrl(url).execute()
    }

    private fun getDownloadUrl(downloadUrlRequestList: List<DownloadUrlRequest>) {
        try {
            val apiService: ApiService = AuthApiClient.instance.create(ApiService::class.java)
            val mCall: Call<DownloadUrlResponse> =
                apiService.downloadUrl(downloadUrlRequestList)
            mCall.enqueue(object : Callback<DownloadUrlResponse?> {
                override fun onResponse(
                    call: Call<DownloadUrlResponse?>,
                    response: Response<DownloadUrlResponse?>
                ) {
                    try {
                        if (response.isSuccessful) {
                            val obj: DownloadUrlResponse? = response.body()
                            setData(obj)
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                override fun onFailure(call: Call<DownloadUrlResponse?>, t: Throwable) {}
            })
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    companion object {
        private var sInstance: DisplayImageUtility? = null

        @Synchronized
        fun getInstance(context: Context): DisplayImageUtility? {
            if (sInstance == null) {
                sInstance = DisplayImageUtility(context)
            }
            return sInstance
        }
    }
}