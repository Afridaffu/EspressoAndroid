package com.coyni.pos.app.model.ActivityLogs

import com.coyni.pos.app.model.BaseResponse

data class ActivityLogsResponse(

    var data : List<ActivityLogsResponseData>? = null

): BaseResponse()