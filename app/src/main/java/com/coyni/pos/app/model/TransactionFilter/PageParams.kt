package com.coyni.pos.app.model.TransactionFilter

import com.coyni.pos.app.utils.Utils
import okio.Utf8

data class PageParams(
    var pageNo: String? = Utils.pageNo,
    var pageSize: String? = Utils.pageSize,
)
