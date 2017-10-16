package com.xmx.tango.common.net

import android.annotation.SuppressLint
import android.os.AsyncTask
import com.alibaba.fastjson.JSON

import java.util.ArrayList

import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody

/**
 * Created by The_onE on 2017/5/26.
 * 用于管理HTTP请求，使用OkHttp插件
 */
// TODO
@SuppressLint("StaticFieldLeak")
object HttpManager {

    private val JSON_TYPE = MediaType.parse("application/json; charset=utf-8")

    // OkHttp客户端
    private val client = OkHttpClient()

    /**
     * 生成Get请求Url
     * @param url 请求地址
     * @param params 请求参数
     * @return 拼接参数后的Url
     */
    fun makeGetRequest(url: String, params: Map<String, String>): String {
        val list = ArrayList<String>()
        for ((key, value) in params) {
            list.add(key + "=" + value)
        }
        return url + "?" + list.joinToString("&")
    }

    /**
     * 发送Get请求
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 请求回调
     */
    fun sendGet(url: String, params: Map<String, String>?,
                callback: HttpGetCallback) {
        // 开启新线程
        object : AsyncTask<String, Exception, String>() {
            override fun doInBackground(vararg strings: String): String? {
                return try {
                    // 拼接参数
                    val re = if (params != null) {
                        makeGetRequest(url, params)
                    } else {
                        url
                    }
                    // 生成Get请求
                    val request = Request.Builder()
                            .url(re)
                            .build()
                    // 获取Get响应
                    val response = client.newCall(request).execute()
                    // 返回响应结果
                    response.body()?.string()
                } catch (e: Exception) {
                    publishProgress(e)
                    null
                }

            }

            override fun onProgressUpdate(vararg values: Exception) {
                super.onProgressUpdate(*values)
                // 请求过程出现异常，请求失败
                callback.fail(values[0])
            }

            override fun onPostExecute(s: String?) {
                super.onPostExecute(s)
                if (s != null) {
                    // 请求成功，返回响应结果
                    callback.success(s)
                } else {
                    callback.success("")
                }
            }
        }.execute()
    }

    /**
     * @param url 请求地址
     * @param params 请求参数
     * @param callback 请求回调
     */
    fun sendPost(url: String, params: Map<String, String>?,
                 callback: HttpPostCallback) {
        sendPost(url, JSON.toJSONString(params), callback)
    }

    /**
     * 发送Post请求
     * @param url 请求地址
     * @param json 发送Json数据
     * @param callback 请求回调
     */
    private fun sendPost(url: String, json: String, callback: HttpPostCallback) {
        // 开启新线程
        object : AsyncTask<String, Exception, String>() {
            override fun doInBackground(vararg strings: String): String? {
                return try {
                    // 生成Post请求
                    val body = RequestBody.create(JSON_TYPE, json)
                    val request = Request.Builder()
                            .url(url)
                            .post(body)
                            .build()
                    // 获取Post响应
                    val response = client.newCall(request).execute()
                    // 返回响应结果
                    response.body()!!.string()
                } catch (e: Exception) {
                    publishProgress(e)
                    null
                }

            }

            override fun onProgressUpdate(vararg values: Exception) {
                super.onProgressUpdate(*values)
                // 请求过程出现异常，请求失败
                callback.fail(values[0])
            }

            override fun onPostExecute(s: String?) {
                super.onPostExecute(s)
                if (s != null) {
                    // 请求成功，返回响应结果
                    callback.success(s)
                }
            }
        }.execute()
    }
}
