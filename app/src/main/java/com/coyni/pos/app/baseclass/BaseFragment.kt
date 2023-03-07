package com.coyni.pos.app.baseclass

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.coyni.pos.app.utils.Utils
import okhttp3.*
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

abstract class BaseFragment : Fragment() {
    val TAG = javaClass.name
    lateinit var mActivity: BaseActivity
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mActivity = activity as BaseActivity
    }

    open fun onBackPressed() {}

    private class EchoWebSocketListener : WebSocketListener() {
        private val interval = 2000
        private val handler = Handler(Looper.getMainLooper())
        private var runnable: Runnable? = null
        private var CLOSE_STATUS = 1000
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.e("onOpen","onOpen")
            try {
                Log.e("authorization: ", Utils.strAuth.toString())
                val jsonObject = JSONObject()
                jsonObject.put("authorization", Utils.strAuth.toString())
                webSocket.send(jsonObject.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onMessage(webSocket: WebSocket, message: String) {
            Log.d("Receive Message: ", message)
            try {
                val obj = JSONObject(message)
                if (obj.getString("eventType") == "SERVER_CONNECTION") {
                    runnable = Runnable {
                        try {
                            val jsonObject = JSONObject()
                            jsonObject.put("authorization", Utils.strAuth.toString())
                            jsonObject.put("eventType", "ping")
                            webSocket.send(jsonObject.toString())
                            handler.postDelayed(runnable!!, interval.toLong())
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }
                    handler.postDelayed(runnable!!, 0)
                } else if (obj.getString("eventType") == "TXN_STATUS" && obj.getString("txnStatus")
                        .lowercase(
                            Locale.getDefault()
                        ) == "completed"
                ) {
//                    sendBroadcast(Intent().setAction(Utils.NOTIFICATION_ACTION))
                    webSocket.close(CLOSE_STATUS, null)
                } else if (obj.getString("eventType") == "SESSION_EXPIRY") {
                    handler.removeCallbacks(runnable!!)
                    webSocket.close(CLOSE_STATUS, null)
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d("Receive Bytes : ", bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocket.close(CLOSE_STATUS, null)
            handler.removeCallbacks(runnable!!)
            Log.d("Closing Socket : ", "$code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
            try {
                //webSocket.close(CLOSE_STATUS, null);
                if (throwable.message != null) Log.d(
                    "Error : ",
                    throwable.message!!
                ) else Log.d("Error : ", throwable.toString())
                handler.removeCallbacks(runnable!!)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }
    }

    fun startWebSocket(url: String) {
        try {
            val serverUrl: String = url
            val client: OkHttpClient =
                OkHttpClient.Builder().readTimeout(0, TimeUnit.MILLISECONDS).build()
            val request: Request = Request.Builder().url(serverUrl).build()
            val listener = EchoWebSocketListener()
            val webSocket = client.newWebSocket(request, listener)
            client.dispatcher.executorService.shutdown()
            Log.d("WebSocket Started with url : ", serverUrl)
        } catch (ex: java.lang.Exception) {
            ex.printStackTrace()
        }
    }
}