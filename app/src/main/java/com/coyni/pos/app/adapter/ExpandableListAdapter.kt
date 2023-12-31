package com.coyni.pos.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.coyni.pos.app.R
import com.coyni.pos.app.baseclass.OnItemClickListener
import com.coyni.pos.app.interfaces.TxnTypesListener
import com.coyni.pos.app.model.TransactionFilter.TransactionsSubTypeData
import com.coyni.pos.app.model.TransactionFilter.TransactionsTypeData
import java.util.*

class ExpandableListAdapter() : BaseExpandableListAdapter() {

    private var listener: TxnTypesListener? = null
    private lateinit var transactionTypeData: java.util.HashMap<Int, TransactionsTypeData>
    private lateinit var transactionSubTypeData: java.util.HashMap<Int, List<TransactionsSubTypeData>>
    private var groups: ArrayList<Int?> = ArrayList()
    private lateinit var mContext: Context

    constructor(
        context: Context,
        transactionTypeData: HashMap<Int, TransactionsTypeData>,
        transactionSubTypeData: HashMap<Int, List<TransactionsSubTypeData>>
    ) : this() {
        this.mContext = context
        this.transactionTypeData = transactionTypeData
        this.transactionSubTypeData = transactionSubTypeData
        groups = ArrayList<Int?>(transactionTypeData.keys)
    }

    fun setTypeClickListener(listener: TxnTypesListener) {
        this.listener = listener
    }

    override fun getGroupCount(): Int {
        return groups.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        val groupId = groups[groupPosition]
        return if (!transactionSubTypeData.containsKey(groupId)) {
            0
        } else transactionSubTypeData[groupId]!!.size
    }

    override fun getGroup(groupPosition: Int): Int? {
        return groups[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): TransactionsSubTypeData {
        return transactionSubTypeData[groups[groupPosition]]!![childPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    val groupData: HashMap<Int, TransactionsTypeData>
        get() = transactionTypeData
    val childData: HashMap<Int, List<TransactionsSubTypeData>>
        get() = transactionSubTypeData

    override fun getGroupView(
        groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?
    ): View {
        var view = convertView
        val groupId = groups[groupPosition]
        val group: TransactionsTypeData? = transactionTypeData[groupId]
        if (convertView == null) {
            val layoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.parent_item, null)
        }
        val parentLL = view!!.findViewById<LinearLayout>(R.id.withdrawViewLL)
        val parentTextTV = view.findViewById<TextView>(R.id.parentTextTV)
        val plusImg = view.findViewById<ImageView>(R.id.plusImage)
        val checkCB = view.findViewById<CheckBox>(R.id.checkCB)
        val viewV = view.findViewById<View>(R.id.viewV)
        parentTextTV.setText(group?.groupItem)
        parentLL.isEnabled = true
        group?.isSelected?.let { checkCB.setChecked(it) }
        if (transactionSubTypeData.containsKey(groupId) && transactionSubTypeData[groupId]!!.size > 0) {
            plusImg.visibility = View.VISIBLE
        } else {
            plusImg.visibility = View.GONE
        }
        checkCB.setOnCheckedChangeListener { compoundButton, check ->
            group?.isSelected = (check)
            if (!check) {
                transactionTypeData.get(groups.get(groupPosition))?.isFromTypes = true
            }
            if (transactionTypeData.get(groups.get(groupPosition))?.isFromTypes!!) {

                try {
                    if (transactionSubTypeData[groups[groupPosition]] != null) {
                        var i = 0
                        while (transactionSubTypeData[groups[groupPosition]]!!.size > i) {
                            if (check) {
                                transactionSubTypeData[groups[groupPosition]]!![i].isSelected = check
                            } else {
                                transactionSubTypeData[groups[groupPosition]]!![i].isSelected = check
                            }
                            i++
                        }
                    }
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }
            listener!!.onCheckBoxClick(transactionSubTypeData)
//            Log.e("transactionTypeData", transactionTypeData.toString())
        }
        return view;

    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        var view = convertView
        if (view == null) {
            val layoutInflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = layoutInflater.inflate(R.layout.filter_childitems, null)
        }
        val childLL = view!!.findViewById<LinearLayout>(R.id.withdrawChildItemLL)
        childLL.isEnabled = true
        val childItem: TransactionsSubTypeData = getChild(groupPosition, childPosition)
        val childTv = view.findViewById<TextView>(R.id.childTextTV)
        val chechBoxCB = view.findViewById<CheckBox>(R.id.chechBoxCB)
        childTv.setText(childItem.groupItem)
        childLL.isEnabled = true
        chechBoxCB.isChecked = childItem.isSelected!!
        chechBoxCB.setOnCheckedChangeListener { compoundButton, b ->
            childItem.isSelected = (b)
            if (transactionSubTypeData != null && transactionSubTypeData.size > 0) {
                if (b) {
                    for (type in transactionSubTypeData.keys) {
                        for (i in 0 until transactionSubTypeData[type]!!.size) {
                            if (transactionSubTypeData[type]!![i].isSelected == true) {
                                transactionTypeData[type]?.isSelected = (true)
                                transactionTypeData[type]?.isFromTypes = (false)
                            }
                        }
                    }

                }
                notifyDataSetChanged();
            }
            listener!!.onCheckBoxClick(transactionSubTypeData)
//            Log.e("transactionSubTypeData", transactionSubTypeData.toString())
        }
        return view;
    }


    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    interface OnSelectListner {
        fun selectedItem(id: Int)
    }

    interface OnItemClickListener {
        fun onGroupClicked(position: Int, childItems: String?)
        fun onChildClicked(s: String?)
    }
}