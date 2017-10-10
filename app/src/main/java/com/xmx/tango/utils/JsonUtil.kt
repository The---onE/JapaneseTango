package com.xmx.tango.utils

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by The_onE on 2017/5/27.
 * 处理JSON数据，将对象转化为Map，将数组转化为List
 */
object JsonUtil {
    val RESPONSE_STATUS = "status"
    val STATUS_ERROR = "0"
    val STATUS_EXECUTE_SUCCESS = "1"
    val STATUS_QUERY_SUCCESS = "2"
    val RESPONSE_PROMPT = "prompt"
    val RESPONSE_ENTITIES = "entities"

    /**
     * 将JSON字符串解析为Map
     *
     * @param json 最外层为对象的JSON字符串
     * @return 解析出的Map对象
     * @throws Exception 解析异常
     */
    @Throws(Exception::class)
    fun parseObject(json: String): Map<String, Any> = parseObject(JSONObject(json))

    /**
     * 将JSONObject解析为Map
     *
     * @param obj JSONObject对象
     * @return 解析出的Map对象
     * @throws Exception 解析异常
     */
    @Throws(Exception::class)
    private fun parseObject(obj: JSONObject): Map<String, Any> {
        val map = HashMap<String, Any>()
        val it = obj.keys()
        // 遍历JSON对象
        while (it.hasNext()) {
            val key = it.next()
            try {
                // 若为数组
                val array = obj.getJSONArray(key)
                val res = parseArray(array)
                map.put(key, res)
            } catch (e: JSONException) {
                try {
                    // 若为对象
                    val o = obj.getJSONObject(key)
                    val res = parseObject(o)
                    map.put(key, res)
                } catch (ex: JSONException) {
                    // 若为值
                    val value = obj.getString(key)
                    map.put(key, value)
                }

            }

        }
        return map
    }

    /**
     * 将JSON字符串解析为List
     *
     * @param json 最外层为数组的JSON字符串
     * @return 解析出的List数组
     * @throws Exception 解析异常
     */
    @Throws(Exception::class)
    fun parseArray(json: String): List<Any> = parseArray(JSONArray(json))

    /**
     * 将JSONArray解析为List
     *
     * @param array JSONArray数组
     * @return 解析出的List数组
     * @throws Exception 解析异常
     */
    @Throws(Exception::class)
    private fun parseArray(array: JSONArray): List<Any> {
        val list = ArrayList<Any>()
        val length = array.length()
        for (i in 0 until length) {
            try {
                // 若为数组
                val arr = array.getJSONArray(i)
                val res = parseArray(arr)
                list.add(res)
            } catch (e: JSONException) {
                try {
                    // 若为对象
                    val obj = array.getJSONObject(i)
                    val res = parseObject(obj)
                    list.add(res)
                } catch (ex: JSONException) {
                    // 若为值
                    val value = array.getString(i)
                    list.add(value)
                }

            }

        }
        return list
    }

    /**
     * 格式化Map对象
     *
     * @param map 由JSON解析出的Map对象
     * @param sep 键值分隔符
     * @param tab 层次推进符
     * @return 格式化后的字符串
     */
    fun formatJSONObject(map: Map<String, Any>, sep: String, tab: String): String {
        var sb = StringBuilder()
        sb = JsonUtil.appendJSONObject(map, sb, 0, sep, tab)
        return sb.toString()
    }

    /**
     * 格式化List数组
     *
     * @param list 由JSON解析出的List数组
     * @param sep  键值分隔符
     * @param tab  层次推进符
     * @return 格式化后的字符串
     */
    fun formatJSONArray(list: List<Any>, sep: String, tab: String): String {
        var sb = StringBuilder()
        sb = JsonUtil.appendJSONArray(list, sb, 0, sep, tab)
        return sb.toString()
    }

    /**
     * 向字符串中追加Map对象
     *
     * @param map    由JSON解析出的Map对象
     * @param source 原字符串
     * @param level  所在层次
     * @param sep    键值分隔符
     * @param tab    层次推进符
     * @return 追加后的字符串
     */
    private fun appendJSONObject(map: Map<String, Any>,
                                 source: StringBuilder,
                                 level: Int, sep: String, tab: String): StringBuilder {
        var src = source
        val t = StringBuilder()
        for (i in 0 until level) {
            t.append(tab)
        }
        for ((key, value) in map) {
            src.append(t).append(key).append(sep)
            when (value) {
                is Map<*, *> -> {
                    src.append("\n")
                    src = appendJSONObject(value as Map<String, Any>, src, level + 1, sep, tab)
                }
                is List<*> -> {
                    src.append("\n")
                    src = appendJSONArray(value as List<Any>, src, level + 1, sep, tab)
                }
                else -> {
                    src.append(value.toString())
                    src.append("\n")
                }
            }
        }
        return src
    }

    /**
     * 向字符串中追加List数组
     *
     * @param list   由JSON解析出的List数组
     * @param source 原字符串
     * @param level  所在层次
     * @param sep    键值分隔符
     * @param tab    层次推进符
     * @return 追加后的字符串
     */
    private fun appendJSONArray(list: List<Any>,
                                source: StringBuilder,
                                level: Int, sep: String, tab: String): StringBuilder {
        var src = source
        val t = StringBuilder()
        for (i in 0 until level) {
            t.append(tab)
        }
        var i = 0
        for (item in list) {
            src.append(t).append("[").append(i).append("]").append(sep)
            if (item is Map<*, *>) {
                src.append("\n")
                src = appendJSONObject(item as Map<String, Any>, src, level + 1, sep, tab)
            } else if (item is List<*>) {
                src.append("\n")
                src = appendJSONArray(item as List<Any>, src, level + 1, sep, tab)
            } else {
                src.append(item.toString())
                src.append("\n")
            }
            i++
        }
        return src
    }
}
