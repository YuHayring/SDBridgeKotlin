package com.housenkui.sdbridgekotlin

/**
 * @date 2022/10/15
 * @author Hayring
 * @description call message before serialize by gson
 */
data class CallMessage(
    val handlerName: String,
    val data: Any?,
    val callbackId: String?
)