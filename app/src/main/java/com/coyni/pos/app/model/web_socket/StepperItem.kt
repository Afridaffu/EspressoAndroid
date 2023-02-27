package com.coyni.pos.app.model.web_socket

data class StepperItem(
    var header: String? = "Connecting to server..",
    val content: String = "",
    var status: String = "",
    var isCompleted: Boolean = false,
    val enable: Boolean = true
)
