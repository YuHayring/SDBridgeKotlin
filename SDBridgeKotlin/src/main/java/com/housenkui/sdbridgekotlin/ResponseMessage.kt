package com.housenkui.sdbridgekotlin

/**
 * @date 2022/10/15
 * @author Hayring
 * @description response message or js-call-native message after deserialize by gson
 */
data class ResponseMessage(
    val responseId: String?,
    val responseData: Any?,
    val callbackId: String?,
    val handlerName: String?,
    val data: Any?
)
