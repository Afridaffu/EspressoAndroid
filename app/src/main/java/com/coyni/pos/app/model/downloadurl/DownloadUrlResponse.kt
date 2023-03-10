package com.coyni.pos.app.model.downloadurl

import com.coyni.pos.app.model.BaseResponse

data class DownloadUrlResponse(var data: ArrayList<DownloadUrlData>? = null) : BaseResponse()