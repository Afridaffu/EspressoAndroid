package com.coyni.pos.app.dialog

import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.SystemClock
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import com.coyni.pos.app.R
import com.coyni.pos.app.adapter.ExpandableListAdapter
import com.coyni.pos.app.baseclass.BaseDialog
import com.coyni.pos.app.databinding.TransactionFilterDialogBinding
import com.coyni.pos.app.model.RangeDates
import com.coyni.pos.app.model.TransactionFilter.TransactionFilterRequest
import com.coyni.pos.app.model.TransactionFilter.TransactionsSubTypeData
import com.coyni.pos.app.model.TransactionFilter.TransactionsTypeData
import com.coyni.pos.app.utils.Utils
import java.text.SimpleDateFormat
import java.util.*

class TransactionFilterDialog(context: Context) : BaseDialog(context) {

    private lateinit var binding: TransactionFilterDialogBinding
    override fun getLayoutId() = R.layout.transaction_filter_dialog

    private var mContext: Context? = null

    private var request: TransactionFilterRequest? = null
    private var isFilters = false
    private var txnStatus = ArrayList<Int>()
    private var transactionType: Int? = null
    var transactionSubType = java.util.ArrayList<Int>()
    var strStartAmount = ""
    var strEndAmount: String? = ""
    var strFromDate: String? = ""
    var strToDate: String? = ""
    var strSelectedDate: String? = ""
    var tempStrSelectedDate: String? = ""
    var strupdated: String? = ""
    var strended: String? = ""
    var strF: String? = ""
    var strT: String? = ""
    var startDateD: Date? = null
    var endDateD: Date? = null
    private var displayFormatter: SimpleDateFormat? = null
    private val displayFormat = "MM-dd-yyyy"
    private var mLastClickTimeFilters = 0L
    var startDateLong = 0L
    var endDateLong: Long = 0

    //    private var adapter: TransactionFilterAdapter? = null
    private var adapter: com.coyni.pos.app.adapter.ExpandableListAdapter? = null
    var transactionTypeData: HashMap<Int, TransactionsTypeData> =
        HashMap<Int, TransactionsTypeData>()
    var transactionSubTypeData: HashMap<Int, List<TransactionsSubTypeData>> =
        HashMap<Int, List<TransactionsSubTypeData>>()

    var rangeDates = RangeDates()


//    private val myApplication: MyApplication? = context as MyApplication?

    override fun initViews() {
        binding = TransactionFilterDialogBinding.bind(findViewById(R.id.filterLL))

        initFields()
        filterActions()

    }

    private fun setAdapter() {
//        adapter = TransactionFilterAdapter(context, transactionTypeData, transactionSubTypeData)
//        binding.custRecyclerView.setAdapter(adapter)
//        Handler().postDelayed(
//            { Utils.setInitialListViewHeight(binding.custRecyclerView) },
//            100
//        )

        adapter = ExpandableListAdapter(context, transactionTypeData, transactionSubTypeData)
        binding.custRecyclerView.setAdapter(adapter)
        Handler().postDelayed(
            { Utils.setInitialListViewHeight(binding.custRecyclerView) },
            100
        )
    }

    private fun initFields() {
        /* Class Initializing */

        binding.custRecyclerView.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, v, groupPosition, id ->
            parent.expandGroup(groupPosition)
            val checkBox = v.findViewById<CheckBox>(R.id.checkBoxCB)
            checkBox.isChecked
            false
        })

        binding.custRecyclerView.setOnGroupClickListener(ExpandableListView.OnGroupClickListener { parent, view, groupPosition, l ->
            val plusImg = view.findViewById<ImageView>(R.id.plusImage)
            if (parent != null && parent.isGroupExpanded(groupPosition)) {
                plusImg.setImageResource(R.drawable.ic_plus)
            } else {
                plusImg.setImageResource(R.drawable.ic_minus)
            }
            Utils.setListViewHeight(parent, groupPosition)
            false
        })


        if (request != null) {
            isFilters = request!!.isFilters
            if (request!!.data?.txnType != null) {
                transactionType = request!!.data?.txnType!!
            }

            if (request!!.data?.txnSubTypes != null) {
                transactionSubType.addAll(request!!.data?.txnSubTypes!!)
            }
            if (transactionSubType == null) {
                transactionSubType = ArrayList<Int>()
            }
            if (request!!.status != null) {
//                txnStatus.addAll(request!!.status!!)
            }
            if (txnStatus == null) {
                txnStatus = ArrayList<Int>()
            }
            if (txnStatus.size > 0) {
                for (i in txnStatus.indices) {
                    when (txnStatus.get(i)) {
                        Utils.completed -> {
                            binding.transStatusCompleted.setChecked(true)
                            binding.transStatusCompleted.setChipStrokeColor(
                                ColorStateList.valueOf(
                                    context.resources.getColor(R.color.primary_green)
                                )
                            )
                        }
                        Utils.partialRefund -> {
                            binding.transStatusPartialRefund.setChecked(true)
                            binding.transStatusPartialRefund.setChipStrokeColor(
                                ColorStateList.valueOf(
                                    context.resources.getColor(R.color.primary_green)
                                )
                            )
                        }
                        Utils.refund -> {
                            binding.transStatusRefunded.setChecked(true)
                            binding.transStatusRefunded.setChipStrokeColor(
                                ColorStateList.valueOf(
                                    context.resources.getColor(R.color.primary_green)
                                )
                            )
                        }
                    }
                }
            }
            strStartAmount = request!!.fromAmount.toString()
            if (strStartAmount != null && strStartAmount.trim { it <= ' ' } != "") {
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] =
                    InputFilter.LengthFilter(
                        java.lang.String.valueOf(R.string.maxlendecimal).toInt()
                    )
                binding.transAmountStartET.setFilters(FilterArray)
                binding.transAmountStartET.setText(strStartAmount)
            }
            strEndAmount = request!!.toAmount.toString()
            if (strEndAmount != null && strEndAmount!!.trim { it <= ' ' } != "") {
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] =
                    InputFilter.LengthFilter(
                        java.lang.String.valueOf(R.string.maxlendecimal).toInt()
                    )
                binding.transAmountEndET.setFilters(FilterArray)
                binding.transAmountEndET.setText(strEndAmount)
            }
            if (request!!.fromDate != null && !request!!.fromDate.equals("")) {
                strF = request!!.fromDate
                if (strF!!.contains(".")) {
                    strF = strF!!.substring(0, strF!!.lastIndexOf("."))
                }
//                strF = myApplication.convertZoneDateTime(strF, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy")
            }
            if (request!!.toDate != null && !request!!.toDate.equals("")) {
                strT = request!!.toDate
                if (strT?.contains(".") == true) {
                    strT = strT?.substring(0, strT?.lastIndexOf(".")!!)
                }
//                strT = myApplication.convertZoneDateTime(strT, "yyyy-MM-dd HH:mm:ss", "MM-dd-yyyy")
            }
            val formatToDisplay = "MMM dd, yyyy"
            val simpleDateFormat = SimpleDateFormat(formatToDisplay)
            try {
                if (strF != null && strF != "" && strT != null && strT != "") {
                    startDateD = displayFormatter?.parse(strF)
                    endDateD = displayFormatter?.parse(strT)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (startDateD != null && endDateD != null) {
                strupdated = simpleDateFormat.format(startDateD)
                strended = simpleDateFormat.format(endDateD)
            }
            if (strupdated != null && strupdated != "" && strended != null && strended != "") {
                strSelectedDate = strupdated + " - " + strended
            }
            if (strSelectedDate != null && strSelectedDate != "") {
                binding.datePickET.setText(strSelectedDate)
            } else {
                binding.datePickET.setText("")
            }
        } else {
            if (transactionType != null) {
                transactionType = 0
            }
            if (transactionSubType != null) {
                transactionSubType.clear()
            }
            if (txnStatus != null) {
                txnStatus.clear()
            }
            strFromDate = ""
            strToDate = ""
            strStartAmount = ""
            strEndAmount = ""
            tempStrSelectedDate = ""
        }
        prepareListData()
        setAdapter()
    }

    private fun filterActions() {
        if (isFilters) {
            if (transactionType == 0) {

            }
            if (transactionSubType.size > 0) {
                for (i in transactionSubType.indices) {
                }
            }
            if (txnStatus.size > 0) {
                for (i in txnStatus.indices) {
                    when (txnStatus[i]) {
                        Utils.completed -> {
                            binding.transStatusCompleted.setChecked(true)
                            binding.transStatusCompleted.setChipStrokeColor(
                                ColorStateList.valueOf(
                                    context.resources.getColor(R.color.primary_green)
                                )
                            )
                        }
                        Utils.partialRefund -> {
                            binding.transStatusPartialRefund.setChecked(true)
                            binding.transStatusPartialRefund.setChipStrokeColor(
                                ColorStateList.valueOf(
                                    context.resources.getColor(R.color.primary_green)
                                )
                            )
                        }
                        Utils.refund -> {
                            binding.transStatusRefunded.setChecked(true)
                            binding.transStatusRefunded.setChipStrokeColor(
                                ColorStateList.valueOf(
                                    context.resources.getColor(R.color.primary_green)
                                )
                            )
                        }
                    }
                }
            }

            if (strStartAmount != null && strStartAmount.trim { it <= ' ' } != "") {
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] = InputFilter.LengthFilter(13)
                binding.transAmountStartET.setFilters(FilterArray)
                binding.transAmountStartET.setText(strStartAmount)
            }

            if (strEndAmount != null && strEndAmount!!.trim { it <= ' ' } != "") {
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] = InputFilter.LengthFilter(13)
                binding.transAmountEndET.setFilters(FilterArray)
                binding.transAmountEndET.setText(strEndAmount)
            }

            if (strSelectedDate != null && strSelectedDate != "") {
                binding.datePickET.setText(strSelectedDate)
            }
        } else {
//            transactionType.clear()
            transactionSubType.clear()
            txnStatus.clear()
            strFromDate = ""
            strToDate = ""
            strStartAmount = ""
            strEndAmount = ""
            startDateD = null
            endDateD = null
            startDateLong = 0L
            endDateLong = 0L
            isFilters = false
            strSelectedDate = ""
        }

        binding.resetFiltersTV.setOnClickListener { view ->
            if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < Utils.lastClickDelay) {
                return@setOnClickListener
            }
            mLastClickTimeFilters = SystemClock.elapsedRealtime()
            resetListData()
            txnStatus.clear()
            strFromDate = ""
            strToDate = ""
            strStartAmount = ""
            strEndAmount = ""
            startDateD = null
            endDateD = null
            startDateLong = 0L
            endDateLong = 0L
            isFilters = false
            strSelectedDate = ""
            binding.transAmountStartET.setText("")
            binding.transAmountEndET.setText("")
            binding.datePickET.setText("")
            rangeDates.updatedFromDate = ("")
            rangeDates.updatedToDate = ("")
            binding.transAmountStartET.clearFocus()
            binding.transAmountEndET.clearFocus()
            binding.datePickET.clearFocus()

            binding.transStatusCompleted.setChecked(false)
            binding.transStatusCompleted.setChipStrokeColor(
                ColorStateList.valueOf(
                    context.resources.getColor(R.color.viewcolor)
                )
            )
            binding.transStatusRefunded.setChecked(false)
            binding.transStatusRefunded.setChipStrokeColor(
                ColorStateList.valueOf(
                    context.resources.getColor(R.color.viewcolor)
                )
            )
            binding.transStatusPartialRefund.setChecked(false)
            binding.transStatusPartialRefund.setChipStrokeColor(
                ColorStateList.valueOf(
                    context.resources.getColor(R.color.viewcolor)
                )
            )

            getOnDialogClickListener()!!.onDialogClicked(Utils.resetFilter, false)
        }

        binding.transStatusCompleted.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                txnStatus.add(Utils.completed)
                binding.transStatusCompleted.setChipStrokeColor(
                    ColorStateList.valueOf(
                        context.resources.getColor(
                            R.color.primary_green
                        )
                    )
                )
            } else {
                if (txnStatus.contains(Utils.completed)) {
                    txnStatus.remove(Utils.completed)
                    binding.transStatusCompleted.setChipStrokeColor(
                        ColorStateList.valueOf(
                            context.resources.getColor(
                                R.color.viewcolor
                            )
                        )
                    )
                }
            }
        })

        binding.transStatusRefunded.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                txnStatus.add(Utils.refund)
                binding.transStatusRefunded.setChipStrokeColor(
                    ColorStateList.valueOf(
                        context.resources.getColor(
                            R.color.primary_green
                        )
                    )
                )
            } else {
                if (txnStatus.contains(Utils.refund)) {
                    txnStatus.remove(Utils.refund)
                    binding.transStatusRefunded.setChipStrokeColor(
                        ColorStateList.valueOf(
                            context.resources.getColor(
                                R.color.viewcolor
                            )
                        )
                    )
                }
            }
        })

        binding.transStatusPartialRefund.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { compoundButton, b ->
            if (b) {
                txnStatus.add(Utils.partialRefund)
                binding.transStatusPartialRefund.setChipStrokeColor(
                    ColorStateList.valueOf(
                        context.resources.getColor(
                            R.color.primary_green
                        )
                    )
                )
            } else {
                if (txnStatus.contains(Utils.partialRefund)) {
                    txnStatus.remove(Utils.partialRefund)
                    binding.transStatusPartialRefund.setChipStrokeColor(
                        ColorStateList.valueOf(
                            context.resources.getColor(
                                R.color.viewcolor
                            )
                        )
                    )
                }
            }
        })

        binding.transAmountStartET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString() == ".") {
                    binding.transAmountStartET.setText("")
                }
            }
        })

        binding.transAmountEndET.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(s: Editable) {
                if (s.toString() == ".") {
                    binding.transAmountEndET.setText("")
                }
            }
        })

        binding.transAmountStartET.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (!hasFocus) {
                binding.transAmountStartET.setFilters(
                    arrayOf<InputFilter>(
                        InputFilter.LengthFilter(
                            13
                        )
                    )
                )
//                USFormat(binding.transAmountStartET, "START")
                try {
                    if (!binding.transAmountStartET.getText().toString()
                            .equals("") && !binding.transAmountStartET.getText().toString()
                            .equals("")
                    ) {
                        val startAmount = Utils.doubleParsing(
                            binding.transAmountStartET.getText().toString().replace(",", "")
                                .trim()
                        )
                        val endAmount = Utils.doubleParsing(
                            binding.transAmountEndET.getText().toString().replace(",", "")
                                .trim()
                        )
                        if (endAmount < startAmount) {
                            Utils.displayAlertNew(
                                "'From Amount' should not be greater than 'To Amount'",
                                context, "coyni"
                            )
                            binding.transAmountStartET.setText("")
                            strStartAmount = ""
                            binding.transAmountEndET.setText("")
                            strEndAmount = ""
                        }
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            } else {
                binding.transAmountStartET.setFilters(
                    arrayOf<InputFilter>(
                        InputFilter.LengthFilter(
                            8
                        )
                    )
                )
            }
        })

        binding.transAmountEndET.setOnFocusChangeListener(View.OnFocusChangeListener { v, hasFocus ->
            if (binding.transAmountStartET.getText().toString().equals("")) {
                binding.transAmountStartET.setText("0.00")
            }
            if (!hasFocus) {
                binding.transAmountEndET.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(13)))
//                USFormat(binding.transAmountEndET, "END")
                try {
                    if (!binding.transAmountEndET.getText().toString()
                            .equals("") && !binding.transAmountEndET.getText().toString()
                            .equals("")
                    ) {
                        val startAmount = Utils.doubleParsing(
                            binding.transAmountStartET.getText().toString().replace(",", "")
                                .trim()
                        )
                        val endAmount = Utils.doubleParsing(
                            binding.transAmountEndET.getText().toString().replace(",", "")
                                .trim()
                        )
                        if (endAmount < startAmount) {
                            Utils.displayAlertNew(
                                "'From Amount' should not be greater than 'To Amount'",
                                context, "coyni"
                            )
                            binding.transAmountStartET.setText("")
                            strStartAmount = ""
                            binding.transAmountEndET.setText("")
                            strEndAmount = ""
                        }
                    }
                    if (binding.transAmountStartET.getText().toString().equals("")) {
                        binding.transAmountStartET.setText("0.00")
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                }
            } else {
                binding.transAmountEndET.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(8)))
            }
        })

        binding.transAmountStartET.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.transAmountStartET.setFilters(
                    arrayOf<InputFilter>(
                        InputFilter.LengthFilter(
                            13
                        )
                    )
                )
//                USFormat(binding.transAmountStartET, "START")
                binding.transAmountStartET.clearFocus()
            }
            false
        })

        binding.transAmountEndET.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                binding.transAmountEndET.setFilters(arrayOf<InputFilter>(InputFilter.LengthFilter(13)))
//                USFormat(binding.transAmountEndET, "END")
                binding.transAmountEndET.clearFocus()
                if (binding.transAmountStartET.getText().toString().equals("")) {
                    binding.transAmountStartET.setText("0.00")
                }
            }
            false
        })

        binding.applyFilterBtnCV.setOnClickListener(View.OnClickListener {

            if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < Utils.lastClickDelay) {
                return@OnClickListener
            }
            mLastClickTimeFilters = SystemClock.elapsedRealtime()
            request = TransactionFilterRequest()
            isFilters = false
            binding.transAmountStartET.clearFocus()
            binding.transAmountEndET.clearFocus()

            processFilter(request!!)

            if (transactionType!! > 0 || transactionSubType.size > 0 || txnStatus.size > 0) {
                isFilters = true
                if (transactionType!! > 0) {
                    request!!.data?.txnType = (transactionType)
                }
                if (transactionSubType.size > 0) {
                    request!!.data?.txnSubTypes = (transactionSubType)
                }
                if (txnStatus.size > 0) {
                    request!!.status = (txnStatus.toString())
                }
            }
            if (!binding.transAmountStartET.getText().toString().trim().equals("")) {
                isFilters = true
//                request!!.fromAmount =
//                    binding.transAmountStartET.getText().toString().replace(",", "")

            } else {
                strStartAmount = ""
            }
            if (!binding.transAmountEndET.getText().toString().trim().equals("")) {
                isFilters = true
//                request!!.toAmount =
//                    (binding.transAmountEndET.getText().toString().replace(",", ""))
                if (binding.transAmountStartET.getText().toString().trim()
                        .equals("") || binding.transAmountStartET.getText().toString().trim()
                        .equals("0.00")
                ) {
                    request!!.fromAmount = 0
                    strStartAmount = "0.00"
                }
            } else {
                strEndAmount = ""
            }
            if (strFromDate != "") {
                isFilters = true
//                request!!.updatedFromDate =
//                    Utils.convertPreferenceZoneToUtcDateTime(
//                        "$strFromDate 00:00:00",
//                        "MM-dd-yyyy HH:mm:ss",
//                        "yyyy-MM-dd HH:mm:ss",
////                        myApplication!!.mCurrentUserData!!.strPreference
//
//                    )
            }
            if (strToDate != "") {
                isFilters = true
//                request!!.updatedToDate =
//                    Utils.convertPreferenceZoneToUtcDateTime(
//                        "$strToDate 23:59:59",
//                        "MM-dd-yyyy HH:mm:ss",
//                        "yyyy-MM-dd HH:mm:ss",
////                        myApplication!!.mCurrentUserData!!.strPreference
//                    )
            }
            if (!binding.transAmountStartET.getText().toString()
                    .equals("") && !binding.transAmountEndET.getText().toString().equals("")
            ) {
                val startAmount = Utils.doubleParsing(
                    binding.transAmountStartET.getText().toString().replace(",", "").trim()
                )
                val endAmount = Utils.doubleParsing(
                    binding.transAmountEndET.getText().toString().replace(",", "").trim()
                )
                if (endAmount < startAmount) {
                    Utils.displayAlertNew(
                        "'From Amount' should not be greater than 'To Amount'",
                        context, ""
                    )
                    binding.transAmountStartET.setText("")
                    strStartAmount = ""
                    binding.transAmountEndET.setText("")
                    strEndAmount = ""
                }
            }
            request!!.isFilters = (isFilters)
            getOnDialogClickListener()!!.onDialogClicked(Utils.applyFilter, request)
            dismiss()
        })

        binding.dateRangePickerLL.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < Utils.lastClickDelay) {
                return@OnClickListener
            }
            mLastClickTimeFilters = SystemClock.elapsedRealtime()
            showCalendar()
        })

        binding.datePickET.setOnClickListener(View.OnClickListener {
            if (SystemClock.elapsedRealtime() - mLastClickTimeFilters < Utils.lastClickDelay) {
                return@OnClickListener
            }
            mLastClickTimeFilters = SystemClock.elapsedRealtime()
            showCalendar()
        })
    }

    private fun showCalendar() {
        val dateRangePickerDialog = DateRangePickerDialog(context, rangeDates)
        dateRangePickerDialog.show()

        dateRangePickerDialog.setOnDialogClickListener(object : OnDialogClickListener {
            override fun onDialogClicked(action: String?, value: Any?) {
                println("went wrong")
                if (action == Utils.datePicker) {
                    rangeDates = value as RangeDates
                    strFromDate = rangeDates.updatedFromDate
                    strToDate = rangeDates.updatedToDate
                    tempStrSelectedDate = rangeDates.fullDate
                    binding.datePickET.setText(tempStrSelectedDate)
                }
            }
        })
    }


    private fun resetListData() {
        if (adapter != null) {
            transactionTypeData = adapter!!.groupData
            val groups: List<Int?> = ArrayList<Int?>(transactionTypeData!!.keys)
            for (group in groups) {
                val data = transactionTypeData!![group]
                data?.isSelected = (false)
            }
            transactionSubTypeData = adapter!!.childData!!
            val childs: List<Int?> = ArrayList<Int?>(transactionSubTypeData.keys)
            for (child in childs) {
                val groupChilds = transactionSubTypeData[child]
                for (childData in groupChilds!!) {
                    childData.isSelected = (false)
                }
            }
        }
        setAdapter()
    }

    private fun prepareListData() {
        //Types
        transactionTypeData = HashMap()

        val saleOrder = TransactionsTypeData()
        saleOrder.itemId = (Utils.filter_saleorder)
        saleOrder.isSelected = (transactionType?.equals(Utils.filter_saleorder))
        saleOrder.groupItem = Utils.SALE_ORDER
        transactionTypeData!![Utils.filter_saleorder] = saleOrder

        val refund = TransactionsTypeData()
        refund.itemId = (Utils.filter_Refund)
        refund.isSelected = (transactionType?.equals(Utils.filter_Refund))
        refund.groupItem = Utils.Refund_String
        transactionTypeData!![Utils.filter_Refund] = refund

        //Sale Order Sub Types
        val saleOrderSubType: MutableList<TransactionsSubTypeData> = ArrayList()
        val data1 = TransactionsSubTypeData()
        data1.isSelected = (transactionSubType.contains(Utils.filter_eCommerce))
        data1.itemId = (Utils.filter_eCommerce)
        data1.groupItem = Utils.eCommerce
        saleOrderSubType.add(data1)

        val data2 = TransactionsSubTypeData()
        data2.isSelected = (transactionSubType.contains(Utils.filter_Retail))
        data2.itemId = (Utils.filter_Retail)
        data2.groupItem = Utils.Retail
        saleOrderSubType.add(data2)

        transactionSubTypeData[Utils.filter_saleorder] = saleOrderSubType

        //Refund Subtypes
        val refunSubType: MutableList<TransactionsSubTypeData> = ArrayList()
        val data3 = TransactionsSubTypeData()
        data3.isSelected = (transactionSubType.contains(Utils.filter_full))
        data3.itemId = (Utils.filter_full)
        data3.groupItem = Utils.FULL
        refunSubType.add(data3)

        val data4 = TransactionsSubTypeData()
        data4.isSelected = (transactionSubType.contains(Utils.filter_partial))
        data4.itemId = (Utils.filter_Retail)
        data4.groupItem = Utils.Partial
        refunSubType.add(data4)

        transactionSubTypeData[Utils.filter_Refund] = refunSubType
    }

    private fun processFilter(request: TransactionFilterRequest) {
        if (adapter != null) {
            val parent: HashMap<Int, TransactionsTypeData> = adapter!!.groupData
            val groups: List<Int> = ArrayList(parent.keys)
            for (group in groups) {
                val data = parent[group]
//                if (data!!.isSelected!! && !transactionType.contains(data.itemId)) {
//                    transactionType.add(data.itemId)
//                }
//                if (!data.isSelected!! && transactionType.contains(data.itemId)) {
//                    transactionType.remove(data.itemId as Int?)
//                }
            }
            val data: HashMap<Int, List<TransactionsSubTypeData>> = adapter!!.childData
            val childs: List<Int> = ArrayList(data.keys)
            for (child in childs) {
                val groupChilds = data[child]!!
                for (childData in groupChilds) {
                    if (childData.isSelected!! && !transactionSubType.contains(childData.itemId)) {
                        transactionSubType.add(childData.itemId)
                    }
                    if (!childData.isSelected!! && transactionSubType.contains(childData.itemId)) {
                        transactionSubType.remove(childData.itemId as Int?)
                    }
                }
            }
        }
    }
}