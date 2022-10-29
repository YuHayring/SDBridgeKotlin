package com.housenkui.sdbridgekotlin

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

interface Callback<P> {

    fun invoke(parameter: Any?) {
        call(parameter as P)
    }

    fun call(p: P)

    /**
     * get the type of the callback parameter
     */
    val parameterType: Type
       get() =  (javaClass.genericInterfaces[0] as ParameterizedType)
            .actualTypeArguments[0]

}
