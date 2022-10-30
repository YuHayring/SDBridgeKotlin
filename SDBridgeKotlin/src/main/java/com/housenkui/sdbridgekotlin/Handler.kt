package com.housenkui.sdbridgekotlin

import com.google.gson.internal.LinkedTreeMap
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * js call native handler interface
 */
interface Handler<P,R> {

    fun handle(p: P): R

    /**
     *
     */
    fun invoke(parameter: Any?) = handle(parameter as P)

    /**
     * get the type of the handler parameter
     */
    val parameterType: Type
        get() = (javaClass.genericInterfaces[
                javaClass.interfaces.indexOf(Handler::class.java)
        ] as ParameterizedType)
            .actualTypeArguments[0]

    /**
     * get the type of the handler returning
     */
    val returnType: Type
        get() =  (javaClass.genericInterfaces[
                javaClass.interfaces.indexOf(Handler::class.java)
        ] as ParameterizedType)
            .actualTypeArguments[1]

}