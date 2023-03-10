package com.coyni.pos.app.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidmads.library.qrgenearator.QRGEncoder
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.MerchantQrBinding
import com.coyni.pos.app.dialog.DiscardSaleDialog
import com.coyni.pos.app.dialog.OnDialogClickListener
import com.coyni.pos.app.model.discard.DiscardSaleRequest
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.view.DashboardActivity
import com.coyni.pos.app.viewmodel.GenerateQrViewModel
import okhttp3.*
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MerchantQrFragment : BaseFragment() {
    lateinit var binding: MerchantQrBinding
    var fontSize: Float = 0.0f;
    private var isPayClickable: Boolean = false
    private lateinit var screen: String
    private lateinit var amount: String
    private lateinit var strWallet: String
    private lateinit var webSocketUrl: String
    lateinit var bitmap: Bitmap
    lateinit var qrgEncoder: QRGEncoder
    var rotate: Animation? = null
    var myApplication: MyApplication? = null
    lateinit var generateQrViewModel: GenerateQrViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and bind to the _binding
        binding = MerchantQrBinding.inflate(layoutInflater, container, false)
        inItFields()
        inItObservers()
        return binding.root
    }


    private fun inItFields() {
        binding.discardSaleLL.isEnabled = false
        myApplication = requireActivity().application as MyApplication
        generateQrViewModel =
            ViewModelProvider(requireActivity()).get(GenerateQrViewModel::class.java)
        getValues()
        strWallet = myApplication!!.mCurrentUserData.generateQrResponseData?.walletId.toString()
        binding.idIVQrcode.setImageBitmap(Utils.convertBase64ToBitmap(myApplication!!.mCurrentUserData.generateQrResponseData?.image.toString()))
        webSocketUrl =
            myApplication!!.mCurrentUserData.generateQrResponseData?.mposWebsocket.toString()
        Utils.qrUniqueCode = myApplication!!.mCurrentUserData.generateQrResponseData?.uniqueId
//        binding.lottieAnimV.loop(false)

        startWebSocket(webSocketUrl)
//        startWebSocket("ws://DEV23-API-GATEWAY-511281183.us-east-1.elb.amazonaws.com:58080/pos")

        Handler().postDelayed({
//            rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
//            binding.lottieAnimV.startAnimation(rotate)

            binding.amountTV.text = amount.toString().replace("$", "")
            binding.qrLL.visibility = View.VISIBLE
            binding.animationRL.visibility = View.GONE
            binding.discardSaleLL.setBackgroundResource(R.drawable.bg_greencolor_filled)
            binding.discardSaleLL.isEnabled = true
        }, 1000)

        binding.discardSaleLL.setOnClickListener {
            disCardSaleDialog()
        }
    }

    private fun disCardSaleDialog() {
        val discardSaleDialog = DiscardSaleDialog(requireContext())
        discardSaleDialog.show()
        discardSaleDialog.setOnDialogClickListener(object : OnDialogClickListener {
            override fun onDialogClicked(action: String?, value: Any?) {
                if (action == Utils.DISCARD) {
                    disCardSale()
                }
            }
        })
    }

    private fun disCardSale() {
        val discardSaleRequest = DiscardSaleRequest()
        discardSaleRequest.requestToken =
            myApplication?.mCurrentUserData?.validateResponseData?.token
        discardSaleRequest.uniqueId =
            myApplication?.mCurrentUserData?.generateQrResponseData?.uniqueId
        generateQrViewModel.discardSaleRequest(discardSaleRequest)
    }

    private fun inItObservers() {
        generateQrViewModel.discardSaleResponse.observe(requireActivity()) { discardSaleResponse ->
            try {
                if (discardSaleResponse != null) {
                    if (discardSaleResponse.status == Utils.SUCCESS) {
                        myApplication?.mCurrentUserData?.generateQrResponseData?.uniqueId == null
                        val intent = Intent(requireContext(), DashboardActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {
                        Utils.displayAlert(
                            discardSaleResponse.error?.errorDescription.toString(),
                            requireContext(),
                            ""
                        )
                    }
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
    }

//    override fun onBackPressed() {
//        disCardSaleDialog()
//    }

    private fun getValues() {
        if (arguments == null) {
            return
        }
        if (requireArguments()[Utils.SCREEN] != null) {
            screen = java.lang.String.valueOf(requireArguments()[Utils.SCREEN])
        }
        if (requireArguments()[Utils.VALUE] != null) {
            amount = java.lang.String.valueOf(requireArguments()[Utils.VALUE])
        }
    }

    private class EchoWebSocketListener : WebSocketListener() {
        private val interval = 2000
        private val handler = Handler(Looper.getMainLooper())
        private var runnable: Runnable? = null
        private var CLOSE_STATUS = 1000
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.e("onOpen", "onOpen")
            try {
                Log.e("authorization: ", Utils.strAuth.toString())
                val jsonObject = JSONObject()
                jsonObject.put("authorization", Utils.strAuth.toString())
//                jsonObject.put("code", Utils.qrUniqueCode)
                jsonObject.put("checkoutCode", Utils.qrUniqueCode)
                Log.e("jsonObject", jsonObject.toString())
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
                            jsonObject.put("checkoutCode", Utils.qrUniqueCode)

                            webSocket.send(jsonObject.toString())
                            handler.postDelayed(runnable!!, interval.toLong())
                        } catch (ex: java.lang.Exception) {
                            ex.printStackTrace()
                        }
                    }
                    handler.postDelayed(runnable!!, 0)
                } else if (obj.getString("eventType") == "POS_TXN_STATUS"
                ) {
                    if (obj.getString("txnStatus").equals("Failed", true)) {

                    } else if (obj.getString("txnStatus").equals("Success", true)) {

                    }
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