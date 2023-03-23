package com.coyni.pos.app.utils

import android.app.Activity
import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.Base64
import android.util.Log
import android.util.Patterns
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.coyni.pos.app.R
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*

class Utils {
    companion object {
        const val ACCESS_TOKEN_EXPIRED = "Access token expired"
        const val INVALID_TOKEN = "Invalid Token"
        const val TIME_EXCEEDED = "User inactive time exceeded"
        const val TRANSACTION_TOKEN = "Transaction_token"
        var strAuth: String? = null
        var qrUniqueCode: String? = null
        const val ACTION_TYPE = "action_type"
        const val txnType = "txnType"
        const val SENT = "Sent"
        const val txnId = "txnId"
        const val txnSubType = "txnSubType"
        const val SALE_ORDER = "Sale Order"
        const val Refund = "Refund"
        const val Refund_String = "refund"
        const val eCommerce = "eCommerce"
        const val FULL = "full"
        const val Partial = "partial"
        const val Retail = "Retail / Mobile"
        const val refundType = "9"
        const val saleorderType = "10"
        const val sentType = "8"
        const val receivedType = "4"
        const val eCommerceSubType = "12"
        const val retailMobileSubType = "13"
        const val SWIPE = "swipe"
        const val GBX_ID = "gbx id"
        const val gbxTxnId = "gbxTxnId"
        const val SALE_ORDER_AMOUNT = "sale order amount"
        const val transPending = "pending"
        const val partialrefund = "partial refund"
        const val refunded = "refunded"
        const val PARTIAL_REFUND = "Partial Refund"
        const val transInprogress = "inprogress"
        const val transCompleted = "completed"
        const val COMPLETED = "Completed"
        const val transSuccessful = "Successful"
        const val transFailed = "failed"
        const val datePicker = "DatePicker"
        var displayAlertDialog: Dialog? = null
        const val slidePercentage = 0.3f
        const val pending = 1
        const val completed = 2
        const val cancelled = 5
        const val inProgress = 0
        const val failed = 3
        const val paid = 5
        const val partialRefund = 11 // need to ask BE
        const val refund = 9
        const val payoutInProgress = 4
        const val payoutFailed = 9
        const val filter_Sent = 8
        const val filter_Received = 4
        const val filter_Buytoken = 2
        const val filter_Withdraw = 3
        const val filter_saleorder = 10
        const val filter_TranferToken = 23
        const val filter_Refund = 9
        const val filter_Batch_release = 19
        const val filter_Reserve_release = 16
        const val filter_Monthly_Service_fee = 17
        const val filter_bankAccount = 0
        const val filter_eCommerce = 12
        const val sent = 8
        const val filter_Retail = 13
        const val filter_full = 22 // need confirmation
        const val filter_partial = 23 // need confirmation
        const val filter_failed_Withdaw = 24 // need confirmation
        const val filter_failed_Withdraw_Fee = 25 // need confirmation
        const val bankAccount = 0
        const val creditCard = 2
        const val debitCard = 3
        const val received = 26 // need confirmation
        const val signet = 7
        const val instantPay = 1
        const val giftCard = 6
        const val pageSize = "25"
        const val pageNo = "0"

        const val CUSTOMER = 1
        const val walletCategory = "1"
        const val PHONE = "PHONE"
        const val EMAIL = "EMAIL"
        const val PHONE_OTP = "PHONE OTP"
        const val EMAIL_OTP = "EMAIL OTP"
        const val FULL_NAME = "FULL NAME"
        const val PASSWORD = "PASSWORD"
        const val LEGAL = "LEGAL"
        const val FORGOTPINOTP = "FORGOTPINOTP"
        const val SCREEN = "screen"
        const val EMPROLE = "Employee"
        const val STATUS = "status"
        const val REFUNDED_AMOUNT = "refunded Amount"
        const val REFUND = "refund"
        const val REFUND_ERROR_CODE = "refund error code"
        const val START_NEW_SALE = "start new sale"
        const val VIEW_BATCH = "view today batch"
        const val VALUE = "value"
        const val HEADER = "Header"
        const val DESCRIPTION = "Description"
        const val FORGOT_PIN = "forgot pin"
        const val FORGOT_PASSWORD = "Forgot Password"
        const val RETRIEVE_EMAIL = "Retrieve Email"
        const val BUSSINESS = "business"
        const val PIN = "pin"
        const val ERROR = "error"
        const val SUCCESS = "SUCCESS"
        const val FAILED = "failed"
        const val IN_PROGRESS = "in progress"
        const val CUSTOMER_PROFILE_ACTIVITY =
            "com.coyni.mapp.profile.profile_dashboard.CustomerProfileActivity"
        const val CURRENT_PASSWORD_ACTIVITY =
            "com.coyni.mapp.profile.profile_dashboard.CustomerProfileActivity"
        const val EDIT_EMAIL_ACTIVITY = "com.coyni.mapp.profile.EditEmailActivity"
        const val EDIT_PHONE_ACTIVITY = "com.coyni.mapp.profile.EditPhoneActivity"
        const val PIN_ACTIVITY = "com.coyni.common.pin.ForGotPinActivity"
        const val applyFilter = "apply"
        const val resetFilter = "resetFilter"
        var isKeyboardVisible = false
        const val SignUP = "SIGNUP"
        const val AddCard = "ADD CARD"
        const val BUTTON_CLICK = "KeyboardClick"
        const val SCREEN_NAME_ADD_CARD = "addcard"
        const val buyBankEnable = "token account.buy tokens.external bank account"
        const val buyDebitEnable = "token account.buy tokens.debit card"
        const val buyCreditEnable = "token account.buy token.credit card"
        const val buyCogentEnable = "token account.buy token.cogent account"
        const val buySignetEnable = "token account.buy token.signet account"
        const val withBankEnable = "token account.withdrawals.external bank account"
        const val withInstantEnable = "token account.withdrawals.instant pay"
        const val withGiftEnable = "token account.withdrawals.gift card"
        const val withCogentEnable = "token account.withdrawals.cogent account"
        const val withSignetEnable = "token account.withdrawals.signet account"
        const val saleOrderEnable = "token account.sale orders.token"
        const val payEnable = "token account.pay/request.pay"
        const val requestEnable = "token account.pay/request.request"
        const val payBankEnable = "token account.payment methods.external bank account"
        const val payDebitEnable = "token account.payment methods.debit card"
        const val payCreditEnable = "token account.payment methods.credit card"
        const val payCogentEnable = "token account.payment methods.cogent account"
        const val paySignetEnable = "token account.payment methods.signet account"
        const val allControlsEnable = "token account.all controls"
        const val MERCHANT_QR = "merchant qr"
        const val GENERATE_QR = "generate qr"
        const val DISCARD = "discard"
        const val DONE = "done"
        const val DEACTIVATED = "DeActivated"
        const val CANCELED = "Canceled"
        const val Completed = "Completed"
        const val lastClickDelay = 2000

        lateinit var errorState: Array<IntArray>
        lateinit var state: Array<IntArray>
        lateinit var errorColor: IntArray
        lateinit var color: IntArray
        lateinit var errorColorState: ColorStateList
        lateinit var colorState: ColorStateList


        fun isValidEmail(target: String?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun shwForcedKeypad(context: Context, edittext: EditText) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(edittext, 0)
        }

        fun hideKeypad(context: Context) {
            try {
                val activity = context as Activity
                activity.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                val imm =
                    context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }


        fun convertDate(date: String?): String {
            var strDate = ""
            try {
                var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                val newDate = spf.parse(date)
                spf = SimpleDateFormat("MM/dd/yyyy")
                strDate = spf.format(newDate)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strDate
        }


        fun monthsBetweenDates(startDate: Date?, endDate: Date?): Int {
            var monthsBetween = 0
            try {
                val start = Calendar.getInstance()
                start.time = startDate
                val end = Calendar.getInstance()
                end.time = endDate
                var dateDiff = end[Calendar.DAY_OF_MONTH] - start[Calendar.DAY_OF_MONTH]
                if (dateDiff < 0) {
                    val borrrow = end.getActualMaximum(Calendar.DAY_OF_MONTH)
                    dateDiff = end[Calendar.DAY_OF_MONTH] + borrrow - start[Calendar.DAY_OF_MONTH]
                    monthsBetween--
                    if (dateDiff > 0) {
                        monthsBetween++
                    }
                } else {
                    monthsBetween++
                }
                monthsBetween += end[Calendar.MONTH] - start[Calendar.MONTH]
                monthsBetween += (end[Calendar.YEAR] - start[Calendar.YEAR]) * 12
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return monthsBetween
        }

        fun getDate(date: String?): Date? {
            var dtExpiry: Date? = null
            try {
                val spf = SimpleDateFormat("dd/MM/yyyy")
                dtExpiry = spf.parse(date)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return dtExpiry
        }

                fun doubleParsing(value: String): Double {
            return value.replace(",".toRegex(), "").replace("$".toRegex(), "")
                .replace("USD".toRegex(), "").toDouble()
        }
//        open fun doubleParsing(value: String): Double? {
//            return java.lang.Double.parseDouble(
//                value.replace(",".toRegex(), "").replace("$".toRegex(), "")
//                    .replace("USD".toRegex(), "")
//            )
//        }

        fun USNumberFormat(number: Double): String {
            var strFormat = ""
            try {
                val format = NumberFormat.getCurrencyInstance(Locale.US)
                strFormat = format.format(number).replace("$", "")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strFormat
        }

        fun convertBigDecimalUSDC(amount: String): String {
            var strValue = ""
            try {
                val amt = doubleParsing(amount.replace(",".toRegex(), ""))
                val format = DecimalFormat.getCurrencyInstance(Locale.US)
                format.maximumFractionDigits = 2
                strValue = format.format(amt).replace("$", "")
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strValue
        }

        fun convertZoneDateTime(
            date: String?, format: String?, requiredFormat: String?, zoneId: String?
        ): String {
            var strDate = ""
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    val dtf = DateTimeFormatterBuilder().appendPattern(format)
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0).toFormatter()
                        .withZone(ZoneOffset.UTC)
                    var zonedTime = ZonedDateTime.parse(date, dtf)
                    val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(requiredFormat)
                    zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS))
                    strDate = zonedTime.format(DATE_TIME_FORMATTER)
                } else {
                    var spf = SimpleDateFormat(format)
                    spf.timeZone = TimeZone.getTimeZone("UTC")
                    val newDate = spf.parse(date)
                    spf = SimpleDateFormat(requiredFormat)
                    spf.timeZone = TimeZone.getTimeZone(zoneId)
                    strDate = spf.format(newDate)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strDate
        }

        fun convertPreferenceZoneToUtcDateTime(
            date: String?, format: String?, requiredFormat: String?, zoneId: String?
        ): String {
            var strDate = ""
            try {
                val dtf = DateTimeFormatterBuilder().appendPattern(format)
                    .parseDefaulting(ChronoField.OFFSET_SECONDS, 0).toFormatter()
                    .withZone(ZoneId.of(zoneId, ZoneId.SHORT_IDS))
                var zonedTime = ZonedDateTime.parse(date, dtf)
                val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(requiredFormat)
                zonedTime = zonedTime.withZoneSameInstant(ZoneOffset.UTC)
                strDate = zonedTime.format(DATE_TIME_FORMATTER)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strDate
        }


        fun exportDate(date: String, zoneId: String?): String {
            var date = date
            if (date.length == 22) {
                date = date + "0"
            }
            var strDate = ""
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    val dtf = DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss.SSS")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0).toFormatter()
                        .withZone(ZoneId.of(zoneId, ZoneId.SHORT_IDS))
                    Log.e("getStrPreference", zoneId!!)
                    var zonedTime = ZonedDateTime.parse(date, dtf)
                    val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")
                    zonedTime = zonedTime.withZoneSameInstant(ZoneOffset.UTC)
                    strDate = zonedTime.format(DATE_TIME_FORMATTER)
                } else {
                    var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    spf.timeZone = TimeZone.getTimeZone("UTC")
                    val newDate = spf.parse(date)
                    spf = SimpleDateFormat("MM/dd/yyyy hh:mm aa")
                    spf.timeZone = TimeZone.getTimeZone(zoneId)
                    strDate = spf.format(newDate)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strDate
        }

        fun getFilterType(type: Int): String {
            var txnType = ""
            when (type) {
                filter_Sent -> txnType = "Sent"
                filter_Buytoken -> txnType = "Buy Token"
                filter_Withdraw -> txnType = "Withdraw"
                received -> txnType = "Received"
                filter_TranferToken -> txnType = "Transfer Token"
                refund -> txnType = "Refund"
                filter_saleorder -> txnType = "Sale Order"
            }
            return txnType
        }

        fun getCurrentDate(): String? {
            return try {
                val spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                spf.format(Date())
            } catch (ex: java.lang.Exception) {
                null
            }
        }

        fun getFiltersSubType(subType: Int): String {
            var txnSubType = ""
            when (subType) {
                bankAccount -> txnSubType = "Bank Account"
                creditCard -> txnSubType = "Credit Card"
                debitCard -> txnSubType = "Debit Card"
                instantPay -> txnSubType = "Instant Pay"
                giftCard -> txnSubType = "Gift Card"
                filter_eCommerce -> txnSubType = "eCommerce"
                filter_Retail -> txnSubType = "Retail/Mobile"
            }
            return txnSubType
        }

        fun pixelsToSp(context: Context, px: Float): Float {
            val scaledDensity = context.resources.displayMetrics.scaledDensity
            return px / scaledDensity
        }


        fun convertTwoDecimal(strAmount: String): String {
            var strValue = ""
            val strAmt = ""
            try {
                strValue = if (strAmount.contains(" ")) {
                    val split = strAmount.split(" ".toRegex()).toTypedArray()
                    //strAmt = Utils.convertBigDecimalUSDC(strAmount.split(" ")[0]);
                    convertBigDecimalUSDC(
                        split[0]
                    ) + " " + split[1]
                } else {
                    convertBigDecimalUSDC(strAmount)
                    //                strValue = Utils.USNumberFormat(Utils.doubleParsing(strAmt)) + " " + mContext.getString(R.string.currency);
//                strValue = Utils.USNumberFormat(Utils.doubleParsing(strAmt));
                }
                Log.e("str", strValue)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strValue
        }

        fun convertToWithoutDecimal(amount: String): String? {
            var strValue = ""
            try {
                val amt = amount.toInt()
                val format = NumberFormat.getNumberInstance(Locale.US)
                strValue = format.format(amt.toLong())
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
            return strValue
        }

        fun convertBigDecimal(amount: String?): String {
            var strValue = ""
            val value: BigDecimal
            try {
                value = BigDecimal(amount)
                strValue = value.setScale(6, BigDecimal.ROUND_HALF_EVEN).toString()
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
            return strValue
        }

        fun isValidJson(jsonString: String?): Boolean {
            if (jsonString == null || jsonString.trim { it <= ' ' } == "") {
                return false
            }
            try {
                JSONObject(jsonString)
            } catch (ex: JSONException) {
                try {
                    JSONArray(jsonString)
                } catch (ex1: JSONException) {
                    return false
                }
            }
            return true
        }

        fun getErrorColorState(context: Context?): ColorStateList {
            errorState = arrayOf(
                intArrayOf(-android.R.attr.state_focused), intArrayOf(android.R.attr.state_focused)
            )
            errorColor = intArrayOf(
                ContextCompat.getColor(context!!, R.color.error_red),
                ContextCompat.getColor(context, R.color.error_red)
            )
            errorColorState = ColorStateList(errorState, errorColor)
            return errorColorState
        }

        fun getNormalColorState(context: Context?): ColorStateList {
            state = arrayOf(
                intArrayOf(-android.R.attr.state_focused), intArrayOf(android.R.attr.state_focused)
            )
            color = intArrayOf(
                ContextCompat.getColor(context!!, R.color.light_gray),
                ContextCompat.getColor(context, R.color.light_gray)
            )
            colorState = ColorStateList(state, color)
            return colorState
        }

        fun getFocusedColorState(context: Context?): ColorStateList {
            state = arrayOf(
                intArrayOf(-android.R.attr.state_focused), intArrayOf(android.R.attr.state_focused)
            )
            color = intArrayOf(
                ContextCompat.getColor(context!!, R.color.primary_green),
                ContextCompat.getColor(context, R.color.primary_green)
            )
            colorState = ColorStateList(state, color)
            return colorState
        }

        fun upperHintColor(
            textInputLayout: TextInputLayout,
            context: Context,
            @ColorRes colorIdRes: Int
        ) {
            textInputLayout.defaultHintTextColor =
                ColorStateList.valueOf(ContextCompat.getColor(context, colorIdRes))
        }

        fun checkInternet(context: Context): Boolean {
            var value = false
            try {
                val ConnectionManager =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val networkInfo = ConnectionManager.activeNetworkInfo
                value = if (networkInfo != null && networkInfo.isConnected == true) {
                    true
                } else {
                    false
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
            return value
        }

        fun setInitialListViewHeight(listView: ExpandableListView) {
            val listAdapter = listView.expandableListAdapter
            var totalHeight = 0
            val desiredWidth = View.MeasureSpec.makeMeasureSpec(
                listView.width,
                View.MeasureSpec.EXACTLY
            )
            for (i in 0 until listAdapter.groupCount) {
                val groupItem = listAdapter.getGroupView(i, false, null, listView)
                groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                totalHeight += groupItem.measuredHeight
            }
            val params = listView.layoutParams
            var height = (totalHeight
                    + listView.dividerHeight * (listAdapter.groupCount - 1))
            if (height < 10) height = 200
            //        params.height = (int) (height * 0.35);
            params.height = height
            listView.layoutParams = params
            listView.requestLayout()
        }

        fun setListViewHeight(listView: ExpandableListView, group: Int) {
            val listAdapter = listView.expandableListAdapter
            var totalHeight = 0
            val desiredWidth = View.MeasureSpec.makeMeasureSpec(
                listView.width,
                View.MeasureSpec.EXACTLY
            )
            for (i in 0 until listAdapter.groupCount) {
                val groupItem = listAdapter.getGroupView(i, false, null, listView)
                groupItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                totalHeight += groupItem.measuredHeight
                if (listView.isGroupExpanded(i) && i != group
                    || !listView.isGroupExpanded(i) && i == group
                ) {
                    for (j in 0 until listAdapter.getChildrenCount(i)) {
                        val listItem = listAdapter.getChildView(
                            i, j, false, null,
                            listView
                        )
                        listItem.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED)
                        totalHeight += listItem.measuredHeight
                    }
                }
            }
            val params = listView.layoutParams
            var height = (totalHeight
                    + listView.dividerHeight * (listAdapter.groupCount - 1))
            if (height < 10) height = 200
            params.height = height
            listView.layoutParams = params
            listView.requestLayout()
        }

        fun displayAlert(msg: String, context: Context, headerText: String) {
            // custom dialog
            displayAlertDialog = Dialog(context)
            displayAlertDialog!!.getWindow()!!.requestFeature(Window.FEATURE_NO_TITLE)
            displayAlertDialog!!.setContentView(R.layout.bottom_sheet_alert_dialog)
            displayAlertDialog!!.getWindow()!!
                .setBackgroundDrawableResource(android.R.color.transparent)
            val mertics = context.resources.displayMetrics
            val width = mertics.widthPixels
            val header: TextView = displayAlertDialog!!.findViewById<TextView>(R.id.tvHead)
            val message: TextView = displayAlertDialog!!.findViewById<TextView>(R.id.tvMessage)
            val actionCV: CardView = displayAlertDialog!!.findViewById<CardView>(R.id.cvAction)
            val actionText: TextView = displayAlertDialog!!.findViewById<TextView>(R.id.tvAction)
            if (headerText != "") {
                header.visibility = View.VISIBLE
                header.text = headerText
            }
            actionCV.setOnClickListener {
                displayAlertDialog!!.dismiss()
            }

            if (msg == "") {
                message.text = context.getString(R.string.please_try_after)
            } else message.text = msg
            val window: Window = displayAlertDialog!!.getWindow()!!
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window.attributes = wlp
            displayAlertDialog!!.getWindow()!!.getAttributes().windowAnimations =
                R.style.DialogAnimation
            displayAlertDialog!!.setCanceledOnTouchOutside(true)
            displayAlertDialog!!.show()
        }

        fun convertZoneLatestTxn(date: String?, zoneId: String?): String? {
            var date = date
            var strDate = ""
            if (date != null && date.contains(".")) {
                date = date.split("\\.").toTypedArray()[0]
            }
            try {
                if (Build.VERSION.SDK_INT >= 26) {
                    val dtf = DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm:ss")
                        .parseDefaulting(ChronoField.OFFSET_SECONDS, 0)
                        .toFormatter()
                        .withZone(ZoneOffset.UTC)
                    var zonedTime = ZonedDateTime.parse(date, dtf)
                    val DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss")
                    zonedTime = zonedTime.withZoneSameInstant(ZoneId.of(zoneId, ZoneId.SHORT_IDS))
                    strDate = zonedTime.format(DATE_TIME_FORMATTER)
                } else {
                    var spf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    spf.timeZone = TimeZone.getTimeZone("UTC")
                    val newDate = spf.parse(date)
                    spf = SimpleDateFormat("MM/dd/yyyy HH:mm:ss")
                    spf.timeZone = TimeZone.getTimeZone(zoneId)
                    strDate = spf.format(newDate)
                }
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
            return strDate
        }

        fun displayAlertNew(msg: String, context: Context, headerText: String) {
            // custom dialog
            displayAlertDialog = Dialog(context)
            displayAlertDialog!!.getWindow()!!.requestFeature(Window.FEATURE_NO_TITLE)
            displayAlertDialog!!.setContentView(R.layout.bottom_sheet_alert_dialog)
            displayAlertDialog!!.getWindow()!!
                .setBackgroundDrawableResource(android.R.color.transparent)
            val mertics = context.resources.displayMetrics
            val width = mertics.widthPixels
            val header: TextView = displayAlertDialog!!.findViewById<TextView>(R.id.tvHead)
            val message: TextView = displayAlertDialog!!.findViewById<TextView>(R.id.tvMessage)
            val actionCV: CardView = displayAlertDialog!!.findViewById<CardView>(R.id.cvAction)
            val actionText: TextView = displayAlertDialog!!.findViewById<TextView>(R.id.tvAction)
            if (headerText != "") {
                header.visibility = View.VISIBLE
                header.text = headerText
            }
            actionCV.setOnClickListener {
                if (msg.equals("Requires Access to Your Storage.", ignoreCase = true)) {
                    displayAlertDialog!!.dismiss()
                    context.startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", context.packageName, null)
                        )
                    )
                } else displayAlertDialog!!.dismiss()
            }
            if (msg == "") {
                message.text = context.getString(R.string.please_try_after)
            } else message.text = msg
            val window: Window = displayAlertDialog!!.getWindow()!!
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            val wlp = window.attributes
            wlp.gravity = Gravity.BOTTOM
            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window.attributes = wlp
            displayAlertDialog!!.getWindow()!!.getAttributes().windowAnimations =
                R.style.DialogAnimation
            displayAlertDialog!!.setCanceledOnTouchOutside(true)
            displayAlertDialog!!.show()
        }

        fun exitSaleModeDialog(context: Context?): Dialog? {
            val dialog = Dialog(context!!)
            dialog.window!!.requestFeature(Window.FEATURE_NO_TITLE)
            dialog.setContentView(R.layout.exit_sale_mode_layout)
            dialog.window!!.setBackgroundDrawableResource(android.R.color.white)
            val window = dialog.window
            window!!.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            val wlp = window.attributes
            wlp.gravity = Gravity.CENTER
//            wlp.flags = wlp.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND
            window.attributes = wlp
            dialog.setCanceledOnTouchOutside(true)
            dialog.show()
            return dialog
        }

        //        fun setCursorColor(edittext: EditText, context: Context) {
//            try {
//                // Get the cursor drawable resource ID
//                val cursorDrawableRes = TextView::class.java.getDeclaredField("mCursorDrawableRes")
//                cursorDrawableRes.isAccessible = true
//                val drawableResId = cursorDrawableRes.getInt(edittext)
//
//                // Get the cursor drawable
//                val cursorDrawable = ContextCompat.getDrawable(this, drawableResId)
//
//                // Set the cursor color
//                cursorDrawable?.setColorFilter(
//                    ContextCompat.getColor(
//                        context,
//                        R.color.primary_green
//                    ), PorterDuff.Mode.SRC_IN
//                )
//
//                // Set the cursor drawable with the new color
//                val cursorDrawables = arrayOf(cursorDrawable, cursorDrawable)
//                val cursorDrawableField = TextView::class.java.getDeclaredField("mCursorDrawable")
//                cursorDrawableField.isAccessible = true
//                cursorDrawableField.set(editText, cursorDrawables)
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }

        fun copyText(strText: String?, context: Context) {
            try {
                val clipboard =
                    context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("Wallet Address", strText)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Copied", Toast.LENGTH_SHORT).show()
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }

        fun convertBase64ToBitmap(base64String: String): Bitmap {
            val decodedString: ByteArray = Base64.decode(base64String, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }

}