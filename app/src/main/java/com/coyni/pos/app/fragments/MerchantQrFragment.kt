package com.coyni.pos.app.fragments

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidmads.library.qrgenearator.QRGContents
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
import com.coyni.pos.app.view.MposDashboardActivity
import com.coyni.pos.app.viewmodel.GenerateQrViewModel
import org.json.JSONObject

class MerchantQrFragment : BaseFragment() {
    lateinit var binding: MerchantQrBinding
    var fontSize: Float = 0.0f;
    private var isPayClickable: Boolean = false
    private lateinit var screen: String
    private lateinit var amount: String
    private lateinit var strWallet: String
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
        myApplication = requireActivity().application as MyApplication
        generateQrViewModel =
            ViewModelProvider(requireActivity()).get(GenerateQrViewModel::class.java)
        getValues()
        strWallet = myApplication!!.mCurrentUserData?.generateQrResponseData?.walletId.toString()
//        generateQRCode(strWallet)
        val jsonObject = JSONObject()
        jsonObject.put("cynAmount", amount.toString())
        jsonObject.put("referenceID", strWallet)
        generateQRCode(myApplication!!.mCurrentUserData?.generateQrResponseData?.image)
        binding.amountTV.text = amount.toString()
//        binding.lottieAnimV.loop(false)

        Handler().postDelayed({
            rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
            binding.lottieAnimV.startAnimation(rotate)

            binding.qrLL.visibility = View.VISIBLE
            binding.animationRL.visibility = View.GONE

            Handler().postDelayed({
//                rotate = AnimationUtils.loadAnimation(context, R.anim.rotate)
//                binding.lottieAnimV.startAnimation(rotate)
//                binding.qrLL.visibility = View.GONE
//                binding.animationRL.visibility = View.VISIBLE
//                binding.waitingText.visibility = View.VISIBLE

            }, 3000)
        }, 3000)


        binding.discardSaleLL.setOnClickListener {
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
                if (discardSaleResponse!!.data != null) {
                    if (discardSaleResponse.status == Utils.SUCCESS) {
                        val intent = Intent(requireContext(), MposDashboardActivity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    } else {

                    }
                } else {

                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

    }

    private fun generateQRCode(wallet: String?) {
        try {
            val windowManager =
                requireContext().getSystemService(Context.WINDOW_SERVICE) as WindowManager
            // initializing a variable for default display.
            val display = windowManager!!.defaultDisplay

            // creating a variable for point which
            // is to be displayed in QR Code.
            val point = Point()
            display!!.getSize(point)

            // getting width and
            // height of a point
            val width = point.x
            val height = point.y

            // generating dimension from width and height.
            var dimen = if (width < height) width else height
            dimen = dimen * 3 / 4

            // setting this dimensions inside our qr code
            // encoder to generate our qr code.
            qrgEncoder = QRGEncoder(wallet, null, QRGContents.Type.TEXT, 600)
            bitmap = Bitmap.createBitmap(qrgEncoder.encodeAsBitmap(), 50, 50, 500, 500)
            //            bitmap  = Utils.trimLeave5Percent(bitmap, R.color.white);

            // getting our qrcode in the form of bitmap.
//            bitmap = qrgEncoder.encodeAsBitmap();
            // the bitmap is set inside our image
            // view using .setimagebitmap method.
            try {
                binding.idIVQrcode.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

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
}