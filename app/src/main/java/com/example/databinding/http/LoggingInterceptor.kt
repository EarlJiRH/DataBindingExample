package com.example.databinding.http

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.ResponseBody
import okio.Buffer

/**
 * ================================================
 * 类名：com.example.databinding.http
 * 时间：2021/8/12 15:48
 * 描述： 自定义日志打印拦截器
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
private const val TAG = "LoggingInterceptor"

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val timeStart = System.nanoTime()
        val request = chain.request()
        val response = chain.proceed(request)

        val buffer = Buffer()
        request.body?.writeTo(buffer)
        val requestBodyStr = buffer.readUtf8()

        Log.e(TAG, String.format("Sending request %s with params %s", request.url, requestBodyStr))

        //仅可以读取一次
        val businessData = response.body?.string() ?: "response body null"

        val mediaType = response.body?.contentType()
        val newBody = ResponseBody.create(mediaType, businessData)
        val newResponse = response.newBuilder().body(newBody).build()


        val timeEnd = System.nanoTime()
        Log.e(TAG, String.format("Received response for %s in %.1fms >>> %s", request.url,(timeEnd-timeStart)/1e6, businessData) )
        return newResponse
    }
}