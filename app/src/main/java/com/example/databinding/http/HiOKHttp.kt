package com.example.databinding.http

import android.content.Context
import android.os.Environment
import android.util.Log
import android.widget.Toast
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.logging.HttpLoggingInterceptor
import java.io.File
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * ================================================
 * 类名：com.example.databinding.http
 * 时间：2021/8/12 14:15
 * 描述：
 * 修改人：
 * 修改时间：
 * 修改备注：
 * ================================================
 * @author Admin
 */
object HiOKHttp {
    private const val TAG = "HiOKHttp"
    private const val BASE_URL = "http://123.56.232.18:8080/serverdemo"
    private val client: OkHttpClient

    init {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        client = OkHttpClient.Builder()    //builder构造者设计模式
            .connectTimeout(10, TimeUnit.SECONDS) //连接超时时间
            .readTimeout(10, TimeUnit.SECONDS)    //读取超时
            .writeTimeout(10, TimeUnit.SECONDS)  //写超时，也就是请求超时
            //.addInterceptor(LoggingInterceptor())// 添加Log日志拦截器
            .addInterceptor(httpLoggingInterceptor)// 添加Log日志拦截器
            .build()
    }


    fun get(url: String) {
        Thread {
            val request: Request = Request.Builder()
                .url(BASE_URL + url)
                .build()

            val call = client.newCall(request)
            val response = call.execute()//同步get请求 子线程网络请求
            val body = response.body?.string()
            Log.e(TAG, "get response :${body}")
        }.start()

    }


    fun getAsync(url: String) {
        val request: Request = Request.Builder()
            .url(BASE_URL + url)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                //返回结果 子线程
                Log.e(TAG, "onResponse: ${response.body?.string()}")
            }

            override fun onFailure(call: Call, e: IOException) {
                //请求失败 子线程
                Log.e(TAG, "onFailure: ${e.message}")
            }
        })//异步get请求 可以主线程网络请求
    }


    fun post(url: String) {
        Thread {
            //创建Form表单对象
            val formBody = FormBody.Builder()
                .add("userId", "1600932269")
                .add("tagId", "71")
                .build()

            val request = Request.Builder()
                .url(BASE_URL + url)
                .post(formBody)
                .build()
            val call = client.newCall(request)
            val response = call.execute()
            val body = response.body?.string()
            Log.e(TAG, "post response: $body")
        }.start()
    }

    fun postAsync(url: String) {
        //创建Form表单对象
        val formBody = FormBody.Builder()
            .add("userId", "1600932269")
            .add("tagId", "71")
            .build()

        val request = Request.Builder()
            .url(BASE_URL + url)
            .post(formBody)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //请求失败 子线程
                Log.e(TAG, "onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                //返回结果 子线程
                Log.e(TAG, "onResponse: ${response.body?.string()}")
            }
        })

    }

    /**
     * POST异步请求【多表单文件上传】 在Android 6.0+以后,读取外部存储卡的文件都是需要动态申请权限
     * Android Q(10) 文件权限再次变更 不允许通过 new File(src) 获取真实文件 仅可以通过 Uri转换成File
     */
    fun postAsyncMultipart(context: Context) {
        val file = File(Environment.getExternalStorageDirectory(), "1.png")
        if (!file.exists()) {
            Toast.makeText(context, "文件不存在", Toast.LENGTH_SHORT).show()
            return
        }

        val multipartBody: RequestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)//一定要设置这句
            .addFormDataPart("username", "admin") //
            .addFormDataPart("password", "admin") //
            .addFormDataPart(
                "file",
                "1.png",
                RequestBody.create("application/octet-stream".toMediaType(), file)
            )
            .build()

        val request: Request = Request.Builder()
            .url("接口需要支持文件上传才可以")
            .post(multipartBody)
            .build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //请求失败 子线程
                Log.e(TAG, "onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                //返回结果 子线程
                Log.e(TAG, "onResponse: ${response.body?.string()}")
            }
        })
    }


    fun postString() {
//        val jsonObj = JSONObject()
//        jsonObj.put("key1", "value1")
//        jsonObj.put("key2", 200)

//        val mediaType = "application/json;charset=utf-8".toMediaType()
        val textPlain = "text/plain;charset=utf-8".toMediaType()
        val textObj = "{username:admin, password:admin}"

//        val body = RequestBody.create(mediaType, jsonObj.toString())
        val body = RequestBody.create(textPlain, textObj)

        val request = Request.Builder()
            .url("$BASE_URL/welcome")
            .post(body)
            .build()

        val call = client.newCall(request)
        call.enqueue(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                //请求失败 子线程
                Log.e(TAG, "postString onFailure: ${e.message}")
            }

            override fun onResponse(call: Call, response: Response) {
                //返回结果 子线程
                Log.e(TAG, "postString onResponse: ${response.body?.string()}")
            }
        })

    }


}