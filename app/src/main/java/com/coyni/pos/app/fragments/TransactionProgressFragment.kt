package com.coyni.pos.app.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.coyni.pos.app.adapter.TxnStepperAdapter
import com.coyni.pos.app.baseclass.BaseFragment
import com.coyni.pos.app.databinding.TransactionInprogressBinding
import com.coyni.pos.app.model.web_socket.StepperItem
import com.coyni.pos.app.utils.Utils
import com.google.android.material.internal.ContextUtils.getActivity
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONException
import org.json.JSONObject
import java.util.*


class TransactionProgressFragment : BaseFragment() {
    private lateinit var binding: TransactionInprogressBinding
    lateinit var screen: String
    var txnStepperAdapter: TxnStepperAdapter? = null
    lateinit var stepperData: ArrayList<StepperItem>
    private val serverUrl = ""
    private var strScreen = ""
    private var strStatus = ""


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // inflate the layout and bind to the _binding
        binding = TransactionInprogressBinding.inflate(layoutInflater, container, false)
        inItFields()
        return binding.root
    }

    private fun inItFields() {

        startWebSocket()
        stepperData = ArrayList()
        (stepperData as ArrayList<StepperItem>).add(StepperItem())
        (stepperData as ArrayList<StepperItem>).get((stepperData as ArrayList<StepperItem>).size - 1).header =
            "Verifying card information..."

        loadStepperRV()
        if (requireActivity().intent.getStringExtra(Utils.SCREEN) != null && requireActivity().intent.getStringExtra(Utils.SCREEN) != "") {
            strScreen = requireActivity().intent.getStringExtra(Utils.SCREEN)!!
        }
        if (requireActivity().intent.getStringExtra(Utils.STATUS) != null && requireActivity().intent.getStringExtra(Utils.STATUS) != "") {
            strStatus = requireActivity().intent.getStringExtra(Utils.STATUS)!!
        }

        if (strStatus == Utils.SUCCESS) {
            Handler().postDelayed({
                (stepperData).get(stepperData.size - 1)
                    .header = "Card verification successful."
                stepperData.get(stepperData.size - 1).status = Utils.transCompleted
                stepperData.get(stepperData.size - 1).isCompleted = true
                val stepperItem = StepperItem()
                stepperItem.header = "Charging your card..."
                stepperData.add(stepperItem)
                loadStepperRV()
            }, 3000)
        } else {
            when (strScreen) {

            }
        }
    }

    private fun startWebSocket() {

    }

    private fun loadStepperRV() {
        if (stepperData != null && stepperData!!.size > 0) {
            if (txnStepperAdapter == null) {
                val layoutManager = LinearLayoutManager(context)
                txnStepperAdapter = TxnStepperAdapter(requireContext(), stepperData!!)
                binding.stepperRV.layoutManager = layoutManager
                binding.stepperRV.adapter = txnStepperAdapter
            } else {
                txnStepperAdapter!!.updateList(stepperData!!)
            }
        }
    }

    private class EchoWebSocketListener : WebSocketListener() {
        private val interval = 2000
        private val handler = Handler(Looper.getMainLooper())
        private var runnable: Runnable? = null
        override fun onOpen(webSocket: WebSocket, response: Response) {
            try {
                val jsonObject = JSONObject()
                jsonObject.put("authorization", Utils.strAuth)
                webSocket.send(jsonObject.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun onMessage(webSocket: WebSocket, message: String) {
            Log.e("Receive Message: ", message)
            try {
                val obj = JSONObject(message)
                var strValue = ""
                if (obj.getString("eventType") == "SERVER_CONNECTION") {
                    runnable = Runnable {
                        try {
                            val jsonObject = JSONObject()
                            jsonObject.put("authorization", Utils.strAuth)
                            jsonObject.put("eventType", "ping")
                            webSocket.send(jsonObject.toString())
                            handler.postDelayed(runnable!!, interval.toLong())
                        } catch (ex: Exception) {
                            ex.printStackTrace()
                        }
                    }
                    handler.postDelayed(runnable!!, 0)
                } else if (obj.has("msg") && obj.getString("eventType") == "TXN_STATUS") {
                    strValue = obj.getString("msg")

                } else if (obj.getString("eventType") == "TXN_STATUS" && obj.getString("txnStatus")
                        .lowercase(
                            Locale.getDefault()
                        ) == "completed"
                ) {

                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.e("Receive Bytes : ", bytes.hex())
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
//            webSocket.close(CLOSE_STATUS, null);
//            Log.e("Closing Socket : ", code + " / " + reason);
            webSocket.close(CLOSE_STATUS, null)
            handler.removeCallbacks(runnable!!)
            Log.d("Closing Socket : ", "$code / $reason")
        }

        override fun onFailure(webSocket: WebSocket, throwable: Throwable, response: Response?) {
            Log.e("Error : ", throwable.message!!)
            try {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            stepperData.get(stepperData.size() - 1).setHeader("Card charged successfully.");
//                            stepperData.get(stepperData.size() - 1).setStatus(Utils.transCompleted);
//                            stepperData.get(stepperData.size() - 1).setCompleted(true);
//                            StepperItem stepperItem = new StepperItem();
//                            stepperItem.setHeader("Issuing tokens");
//                            stepperData.add(stepperItem);
//                            loadStepperRV();
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                });
                try {
                    //webSocket.close(CLOSE_STATUS, null);
                    if (throwable.message != null) Log.d(
                        "Error : ",
                        throwable.message!!
                    ) else Log.d("Error : ", throwable.toString())
                    handler.removeCallbacks(runnable!!)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        companion object {
            private const val CLOSE_STATUS = 1000
        }
    }

}