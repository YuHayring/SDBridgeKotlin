package com.housenkui.sdbridgekotlin

import com.google.gson.*
import java.lang.reflect.Type

/**
 * @date 2022/10/29
 * @author Hayring
 * @description
 */
class ResponseMessageDeserializer(
    val responseCallbacks: Map<String, Callback<*>>,
    val messageHandlers: Map<String, Handler<*,*>>
): JsonDeserializer<ResponseMessage>, JsonSerializer<ResponseMessage> {



    companion object {

        /**
         * json key responseId
         */
        private const val RESPONSE_ID = "responseId"

        /**
         * json key responseData
         */
        private const val RESPONSE_DATA = "responseData"

        /**
         * json key callbackId
         */
        private const val CALLBACK_ID = "callbackId"

        /**
         * json key handlerName
         */
        private const val HANDLER_NAME = "handlerName"

        /**
         * json key data
         */
        private const val DATA = "data"

    }


    /**
     * native callback after js handle
     */
    override fun deserialize(
        json: JsonElement,
        typeOfT: Type?,
        context: JsonDeserializationContext
    ): ResponseMessage {
        val jsonObj = json.asJsonObject
        //native call json back
        jsonObj[RESPONSE_ID]?.let {
            val responseId: String? = context.deserialize(it, String::class.java)
            val responseDataElement = jsonObj[RESPONSE_DATA]
            val type = responseCallbacks[responseId]!!.parameterType
            return@deserialize ResponseMessage(
                responseId,
                context.deserialize(responseDataElement, type),
                null,
                null,
                null
            )
        }
        //json call native
        val handlerName: String = context.deserialize(jsonObj[HANDLER_NAME], String::class.java)
        return ResponseMessage(
            null,
            null,
            jsonObj[CALLBACK_ID]?.let { context.deserialize(it, String::class.java) },
            handlerName,
            jsonObj[DATA]?.let { context.deserialize(it, messageHandlers[handlerName]!!.parameterType)}
        )
    }

    /**
     * js call native, back to js after handle
     */
    override fun serialize(
        src: ResponseMessage?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ) = JsonObject().also { json ->
        src?.responseId?.let {
            json.add(RESPONSE_ID, context?.serialize(it))
        }
        src?.responseData?.let {
            json.add(RESPONSE_DATA, context?.serialize(it))
        }
        src?.callbackId?.let {
            json.add(CALLBACK_ID, context?.serialize(it))
        }
        src?.handlerName?.let {
            json.add(HANDLER_NAME, context?.serialize(it))
        }
        src?.data?.let {
            json.add(DATA, context?.serialize(it))
        }
    }


}