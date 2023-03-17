package com.coyni.pos.app.model.ActivityLogs

import java.util.Properties

data class ActivityLogsResponseData(
    var createdAt : String? = null,
    var updatedAt : String? = null,
    var msgDisplayUser : String? = null,
    var refId : Int? = null,
    var message : String? = null,
    var txnType : Int? = null,
    var txnSubType : Int? = null,
    var linkText : String? = null,
    var customProperties : PropertiesData? = null
)
