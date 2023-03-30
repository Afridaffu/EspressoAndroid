package com.coyni.pos.app.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.postDelayed
import androidx.lifecycle.ViewModelProvider
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.BaseActivity
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.MerchantQrBinding
import com.coyni.pos.app.dialog.DiscardSaleDialog
import com.coyni.pos.app.dialog.OnDialogClickListener
import com.coyni.pos.app.model.discard.DiscardSaleRequest
import com.coyni.pos.app.utils.MyApplication
import com.coyni.pos.app.utils.Utils
import com.coyni.pos.app.view.DashboardActivity
import com.coyni.pos.app.view.StatusFailedActivity
import com.coyni.pos.app.view.SucessFlowActivity
import com.coyni.pos.app.viewmodel.GenerateQrViewModel
import com.microsoft.appcenter.utils.HandlerUtils.runOnUiThread
import okhttp3.*
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MerchantQrFragment : BaseFragment() {
    lateinit var binding: MerchantQrBinding
    private lateinit var screen: String
    private lateinit var amount: String
    private lateinit var strWallet: String
    private var webSocketUrl: String = ""
    var myApplication: MyApplication? = null
    var lastClickTime = 0L
    lateinit var generateQrViewModel: GenerateQrViewModel
    var webSocketGlobal: WebSocket? = null
    var activity: BaseActivity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = MerchantQrBinding.inflate(layoutInflater, container, false)
        inItFields()
        inItObservers()
        return binding.root
    }


    private fun inItFields() {
        binding.discardSaleLL.isEnabled = false
        myApplication = requireActivity().application as MyApplication
        activity = requireActivity() as BaseActivity
        generateQrViewModel =
            ViewModelProvider(requireActivity()).get(GenerateQrViewModel::class.java)
        getValues()
        strWallet = myApplication!!.mCurrentUserData.generateQrResponseData?.walletId.toString()
        binding.idIVQrcode.setImageBitmap(Utils.convertBase64ToBitmap(myApplication!!.mCurrentUserData.generateQrResponseData?.image.toString()))
        webSocketUrl =
            myApplication!!.mCurrentUserData.generateQrResponseData?.mposWebsocket.toString()
        Utils.qrUniqueCode = myApplication!!.mCurrentUserData.generateQrResponseData?.uniqueId

        Handler().postDelayed({
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
                    if (SystemClock.elapsedRealtime() - lastClickTime < Utils.lastClickDelay)
                        return
                    lastClickTime = SystemClock.elapsedRealtime()
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
        activity?.showProgressDialog()
        generateQrViewModel.discardSaleRequest(discardSaleRequest)
    }

    private fun inItObservers() {
//        generateQrViewModel.discardSaleResponse.observe(requireActivity()) { discardSaleResponse ->
//            try {
//                if (discardSaleResponse != null) {
//                    if (discardSaleResponse.status == Utils.SUCCESS) {
//                        myApplication?.mCurrentUserData?.generateQrResponseData?.uniqueId = null
//                        val intent = Intent(requireContext(), DashboardActivity::class.java)
//                        intent.setFlags(
//                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//                        )
//                        startActivity(intent)
//                        requireActivity().finish()
//                    } else {
//                        Utils.displayAlert(
//                            discardSaleResponse.error?.errorDescription.toString(),
//                            requireContext(),
//                            ""
//                        )
//                    }
//                }
//            } catch (ex: Exception) {
//                ex.printStackTrace()
//            }
//        }
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

    inner class EchoWebSocketListener : WebSocketListener() {
        private val interval = 2000
        private val handler = Handler(Looper.getMainLooper())
        private var runnable: Runnable? = null
        private var CLOSE_STATUS = 1000
        override fun onOpen(webSocket: WebSocket, response: Response) {
            webSocketGlobal = webSocket
            Log.e("onOpen", "onOpen")
            try {
                Log.e("authorization: ", Utils.strAuth.toString())
                val jsonObject = JSONObject()
                jsonObject.put("authorization", Utils.strAuth.toString())
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
                webSocketGlobal = webSocket
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
                } else if (obj.getString("eventType") == "SCAN_QR_CODE") {
                    runOnUiThread(Runnable {
                        binding.qrLL.visibility = View.GONE
                        binding.animationRL.visibility = View.VISIBLE
                        binding.waitingText.visibility = View.VISIBLE
                        binding.discardSaleLL.setBackgroundResource(R.drawable.bg_inactive_color_filled_cv)
                        binding.discardSaleLL.isEnabled = false
                    })
                } else if (obj.getString("eventType") == "POS_CANCELLED") {
                    startActivity(
                        Intent(
                            requireContext(), StatusFailedActivity::class.java
                        ).putExtra(Utils.STATUS, obj.getString("txnStatus"))
                    )
                } else if (obj.getString("eventType") == "POS_TXN_STATUS") {

                    myApplication!!.mCurrentUserData.webSocketObject = obj

                    runnable = Runnable {
                        if (obj.getString("txnStatus").equals(Utils.FAILED, true)
                            || obj.getString("txnStatus").equals(Utils.CANCELED, true)
                        ) {
                            startActivity(
                                Intent(
                                    requireContext(), StatusFailedActivity::class.java
                                ).putExtra(Utils.STATUS, obj.getString("txnStatus"))
                            )
                        } else if (obj.getString("txnStatus").equals(Utils.COMPLETED, true)) {
                            startActivity(Intent(requireContext(), SucessFlowActivity::class.java))
                        }

                        handler.postDelayed(Runnable {
                            runOnUiThread(Runnable {
                                binding.qrLL.visibility = View.VISIBLE
                                binding.animationRL.visibility = View.GONE
                                binding.waitingText.visibility = View.GONE
                                binding.discardSaleLL.setBackgroundResource(R.drawable.bg_greencolor_filled)
                                binding.discardSaleLL.isEnabled = true
                            })
                        }, 1500)


                    }
                    handler.postDelayed(runnable!!, 500)

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
            webSocketGlobal = webSocket
            Log.d("Receive Bytes : ", bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            webSocketGlobal = webSocket
            webSocket.close(CLOSE_STATUS, null)
            handler.removeCallbacks(runnable!!)
            Log.d("Closing Socket : ", "$code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
            try {
                webSocketGlobal = webSocket
                if (throwable.message != null) Log.d(
                    "Error : ", throwable.message!!
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

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            webSocketGlobal!!.close(1000, null)
        } catch (e: Exception) {
        }
    }

    override fun onResume() {
        super.onResume()
        try {
            if (webSocketUrl != "") startWebSocket(webSocketUrl)
        } catch (e: Exception) {
        }
    }
}