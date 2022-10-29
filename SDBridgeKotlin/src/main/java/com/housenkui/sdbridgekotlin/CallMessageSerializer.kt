package com.housenkui.sdbridgekotlin

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

/**
 * @date 2022/10/29
 * @author Hayring
 * @description CallMessage custom gson Serializer
 */
object CallMessageSerializer: JsonSerializer<CallMessage> {


    /**
     * json key handlerName
     */
    private const val HANDLER_NAME = "handlerName"


    /**
     * json key data
     */
    private const val DATA = "data"

    /**
     * json key callbackId
     */
    private const val CALLBACK_ID = "callbackId"


    /**
     * if field is null, don serialize it
     */
    override fun serialize(
        src: CallMessage,
        typeOfSrc: Type?,
        context: JsonSerializationContext
    ): JsonElement {
        val json = JsonObject()
        json.add(HANDLER_NAME, context.serialize(src.handlerName))
        src.data?.let { json.add(DATA, context.serialize(it)) }
        src.callbackId?.let { json.add(CALLBACK_ID, context.serialize(it)) }
        return json
    }


}