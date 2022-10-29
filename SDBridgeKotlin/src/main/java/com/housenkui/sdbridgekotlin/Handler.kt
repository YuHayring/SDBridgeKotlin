package com.housenkui.sdbridgekotlin

import com.google.gson.internal.LinkedTreeMap
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface Handler<P,R> {
    fun handle(p: P): R


    fun invoke(parameter: Any?) = handle(parameter as P)

    /**
     * get the type of the handler parameter
     */
    val parameterType: Type
        get() =  (javaClass.genericInterfaces[0] as ParameterizedType)
            .actualTypeArguments[0]

    /**
     * get the type of the handler returning
     */
    val returnType: Type
        get() =  (javaClass.genericInterfaces[0] as ParameterizedType)
            .actualTypeArguments[1]

}