package com.coyni.pos.app.utils

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.net.ConnectivityManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.inputmethod.InputMethodManager
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
        const val splashDelay = 2000L
        var QA_SKIP_ENCRYPTION = false
        const val ACCESS_TOKEN_EXPIRED = "Access token expired"
        const val INVALID_TOKEN = "Invalid Token"
        const val TIME_EXCEEDED = "User inactive time exceeded"
        const val TRANSACTION_TOKEN = "Transaction_token"
        const val COMMENT_ACTION = "comment_action"
        const val RELOAD = "reload"
        const val selectedCard = "SelectedCardToDisplay"
        const val SCREEN_NAME_REFUND = "refundables"
        const val ONCLICK_CLICK = "OnClickONKeyboard"
        const val BACK_CLICK = "onBackClickAction"
        var strAuth: String? = null
        var strVersionName: String? = null
        var strVersionCode: String? = null
        var isFaceEnabled = true
        var isTouchEnabled = true
        var isBiometric = false
        var deviceID = ""
        var appVersion: String? = null
        const val ACCOUNT_TYPE = "account_type"
        const val PAYMETHODS = "paymethods"
        const val BUYTOKEN = "buytoken"
        const val CARDS_ADD = "cards adding"
        const val WITHDRAW = "withdraw"
        const val TYPE = "type"
        const val TOUCH = "TOUCH"
        const val FACE = "FACE"
        const val AUTHENTICATION_TYPE = "type"
        const val TOUCH_FACE = "touch face"
        const val ACTION_TYPE = "action_type"
        const val RESET_PIN = "reset pin"
        const val NEW_PIN = "new pin"
        const val CONFIRM_NEW_PIN = "confirm pin"
        const val CHANGE_PASSWORD = "change password"
        const val CHOOSE_PIN = "choose"
        const val ENTER_PIN = "enter"
        const val CONFIRM_PIN = "confirm"
        const val CURRENT_PASSWORD = "current password"
        const val OLD_PASSWORD = "old password"
        const val PROFILE_DETAILS = "profile details"
        const val ADDRESS = "address"
        const val EDIT_EMAIL = "edit email"
        const val EDIT_PHONE = "edit phone"
        const val EDIT_ADDRESS = "edit address"
        const val DBA_PROFILE = "dba profile"
        const val MERCHANT_FEE = "merchant fee"
        const val MERCHANT_LIMITS = "merchant limits"
        const val WALLET_SETTINGS = "wallet settings"
        const val WALLET_DETAILS = "wallet details"
        const val WALLET_FEE_LIMITS = "wallet fee and limits"
        const val refundActionType = "REFUND"
        const val txnType = "txnType"
        const val txnSubType = "txnSubType"
        const val PAID_ORDER = "paid order"
        const val buyType = "2"
        const val withdrawType = "3"
        const val refundType = "9"
        const val saleorderType = "10"
        const val bankType = "0"
        const val instantType = "1"
        const val giftcardType = "6"
        const val eCommerceSubType = "12"
        const val retailMobileSubType = "13"
        const val reserveRelease = "16"
        const val batchRelease = "33" // need information
        const val filterMonthlyServiceFee = "34" //
        const val monthlyMinFee = "35" //
        const val complianceFee = "36" //
        const val cancellationFee = "37" //
        const val walletTransferType = "38" //
        const val PAY = "pay"
        const val TIP = "tip"
        const val LOGIN_EXPIRY = "loginExpiry"
        const val LOGIN = "login"
        const val PAY_TO_PERSONAL_WITH_AMOUNT = "pay to personal with amount"
        const val PAY_TO_MERCHANT = "pay to merchant"
        const val PAY_TO_PERSONAL = "pay to personal"
        const val LOAD_TOKEN = "loadtoken"
        const val CHECK_OUT = "checkout"
        const val SWIPE = "swipe"
        const val CVV_SUCCESS = "cvv"
        var displayAlertDialog: Dialog? = null
        const val slidePercentage = 0.3f
        const val slidePercentagehalf = 0.5f
        const val duration = 1000
        const val cPP = 1
        const val cTOS = 0
        const val mPP = 1
        const val mTOS = 0
        const val mAgmt = 2
        const val pending = 1
        const val completed = 2
        const val cancelled = 5
        const val inProgress = 0
        const val failed = 3
        const val paid = 5
        const val partialRefund = 11 // need to ask BE
        const val refund = 9 // need to ask BE
        const val payoutInProgress = 4
        const val payoutFailed = 9
        const val REQUEST_ID_MULTIPLE_PERMISSIONS = 102
        const val filter_Sent = 8
        const val filter_Received = 4
        const val filter_Buytoken = 2
        const val filter_Withdraw = 3
        const val filter_saleorder = 14
        const val filter_TranferToken = 23
        const val filter_Refund = 9
        const val filter_Batch_release = 19
        const val filter_Reserve_release = 16
        const val filter_Monthly_Service_fee = 17
        const val filter_bankAccount = 0
        const val filter_eCommerce = 12
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
        const val pageSize = 25
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
        const val REFUND = "refund"
        const val START_NEW_SALE = "start new sale"
        const val VIEW_BATCH = "view today batch"
        const val VALUE = "value"
        const val FORGOT_PIN = "forgot pin"
        const val FORGOT_PASSWORD = "Forgot Password"
        const val RETRIEVE_EMAIL = "Retrieve Email"
        const val BUSSINESS = "business"
        const val PIN = "pin"
        const val ERROR = "error"
        const val SUCCESS = "success"
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

        lateinit var errorState: Array<IntArray>
        lateinit var state: Array<IntArray>
        lateinit var errorColor: IntArray
        lateinit var color: IntArray
        lateinit var errorColorState: ColorStateList
        lateinit var colorState: ColorStateList


        fun isValidEmail(target: String?): Boolean {
            return !TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()
        }

        fun shwForcedKeypad(context: Context) {
            val inputMethodManager =
                context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
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
                //            strValue = format.format(amt).replaceAll("$", "").replaceAll("USD", "").replace("CYN", "");
                strValue = format.format(amt).replace("$", "").replace("USD", "").replace("CYN", "")
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

        fun setUpperHintColor(til: TextInputLayout, color: Int) {
            try {
                val states = arrayOf(intArrayOf())
                val colors = intArrayOf(color)
                val myList = ColorStateList(states, colors)
                til.defaultHintTextColor = myList
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
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

    }
}