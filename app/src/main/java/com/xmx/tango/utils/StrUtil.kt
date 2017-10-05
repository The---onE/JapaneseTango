package com.xmx.tango.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.util.Log
import android.widget.Toast

import java.util.ArrayList

/**
 * 字符串工具类
 *
 * @author xiaoleilu
 */
object StrUtil {

    val DOT = "."
    val SLASH = "/"
    val EMPTY = ""

    /**
     * 字符串是否为空白 空白的定义如下： <br></br>
     * 1、为null <br></br>
     * 2、为不可见字符（如空格）<br></br>
     * 3、""<br></br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    fun isBlank(str: String?): Boolean {
        return str == null || str.trim { it <= ' ' }.length == 0
    }

    /**
     * 字符串是否为空，空的定义如下
     * 1、为null <br></br>
     * 2、为""<br></br>
     *
     * @param str 被检测的字符串
     * @return 是否为空
     */
    fun isEmpty(str: String?): Boolean {
        return str == null || str.length == 0
    }

    /**
     * 获得set或get方法对应的标准属性名<br></br>
     * 例如：setName 返回 name
     *
     * @param getOrSetMethodName
     * @return 如果是set或get方法名，返回field， 否则null
     */
    fun getGeneralField(getOrSetMethodName: String): String? {
        return if (getOrSetMethodName.startsWith("get") || getOrSetMethodName.startsWith("set")) {
            cutPreAndLowerFirst(getOrSetMethodName, 3)
        } else null
    }

    /**
     * 生成set方法名<br></br>
     * 例如：name 返回 setName
     *
     * @param fieldName 属性名
     * @return setXxx
     */
    fun genSetter(fieldName: String): String? {
        return upperFirstAndAddPre(fieldName, "set")
    }

    /**
     * 生成get方法名
     *
     * @param fieldName 属性名
     * @return getXxx
     */
    fun genGetter(fieldName: String): String? {
        return upperFirstAndAddPre(fieldName, "get")
    }

    /**
     * 去掉首部指定长度的字符串并将剩余字符串首字母小写<br></br>
     * 例如：str=setName, preLength=3 -> return name
     *
     * @param str       被处理的字符串
     * @param preLength 去掉的长度
     * @return 处理后的字符串，不符合规范返回null
     */
    fun cutPreAndLowerFirst(str: String?, preLength: Int): String? {
        if (str == null) {
            return null
        }
        if (str.length > preLength) {
            val first = Character.toLowerCase(str[preLength])
            return if (str.length > preLength + 1) {
                first + str.substring(preLength + 1)
            } else first.toString()
        }
        return null
    }

    /**
     * 原字符串首字母大写并在其首部添加指定字符串
     * 例如：str=name, preString=get -> return getName
     *
     * @param str       被处理的字符串
     * @param preString 添加的首部
     * @return 处理后的字符串
     */
    fun upperFirstAndAddPre(str: String?, preString: String?): String? {
        return if (str == null || preString == null) {
            null
        } else preString + upperFirst(str)
    }

    /**
     * 大写首字母<br></br>
     * 例如：str = name, return Name
     *
     * @param str 字符串
     * @return
     */
    fun upperFirst(str: String): String {
        return Character.toUpperCase(str[0]) + str.substring(1)
    }

    /**
     * 小写首字母<br></br>
     * 例如：str = Name, return name
     *
     * @param str 字符串
     * @return
     */
    fun lowerFirst(str: String): String {
        return Character.toLowerCase(str[0]) + str.substring(1)
    }

    /**
     * 去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 preffix， 返回原字符串
     */
    fun removePrefix(str: String?, prefix: String): String? {
        return if (str != null && str.startsWith(prefix)) {
            str.substring(prefix.length)
        } else str
    }

    /**
     * 忽略大小写去掉指定前缀
     *
     * @param str    字符串
     * @param prefix 前缀
     * @return 切掉后的字符串，若前缀不是 prefix， 返回原字符串
     */
    fun removePrefixIgnoreCase(str: String?, prefix: String): String? {
        return if (str != null && str.toLowerCase().startsWith(prefix.toLowerCase())) {
            str.substring(prefix.length)
        } else str
    }

    /**
     * 去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    fun removeSuffix(str: String?, suffix: String): String? {
        return if (str != null && str.endsWith(suffix)) {
            str.substring(0, str.length - suffix.length)
        } else str
    }

    /**
     * 忽略大小写去掉指定后缀
     *
     * @param str    字符串
     * @param suffix 后缀
     * @return 切掉后的字符串，若后缀不是 suffix， 返回原字符串
     */
    fun removeSuffixIgnoreCase(str: String?, suffix: String): String? {
        return if (str != null && str.toLowerCase().endsWith(suffix.toLowerCase())) {
            str.substring(0, str.length - suffix.length)
        } else str
    }

    /**
     * 切分字符串
     *
     * @param str       被切分的字符串
     * @param separator 分隔符字符
     * @param limit     限制分片数
     * @return 切分后的集合
     */
    @JvmOverloads
    fun split(str: String?, separator: Char, limit: Int = 0): List<String>? {
        if (str == null) {
            return null
        }
        val list = ArrayList<String>(if (limit == 0) 16 else limit)
        if (limit == 1) {
            list.add(str)
            return list
        }

        var isNotEnd = true    //未结束切分的标志
        val strLen = str.length
        val sb = StringBuilder(strLen)
        for (i in 0 until strLen) {
            val c = str[i]
            if (isNotEnd && c == separator) {
                list.add(sb.toString())
                //清空StringBuilder
                sb.delete(0, sb.length)

                //当达到切分上限-1的量时，将所剩字符全部作为最后一个串
                if (limit != 0 && list.size == limit - 1) {
                    isNotEnd = false
                }
            } else {
                sb.append(c)
            }
        }
        list.add(sb.toString())
        return list
    }

    /**
     * 重复某个字符
     *
     * @param c     被重复的字符
     * @param count 重复的数目
     * @return 重复字符字符串
     */
    fun repeat(c: Char, count: Int): String {
        val result = CharArray(count)
        for (i in 0 until count) {
            result[i] = c
        }
        return String(result)
    }

    /**
     * 比较两个字符串是否相同，如果为null或者空串则算不同
     *
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 是否非空相同
     */
    fun equalsNotEmpty(str1: String, str2: String): Boolean {
        return if (isEmpty(str1)) {
            false
        } else str1 == str2
    }

    /**
     * 格式化文本
     *
     * @param template 文本模板，被替换的部分用 {} 表示
     * @param values   参数值
     * @return 格式化后的文本
     */
    fun format(template: String, vararg values: Any): String {
        return String.format(template.replace("{}", "%s"), *values)
    }

    /**
     * 连接字符串
     *
     * @param items     待连接的字符串数组
     * @param separator 分隔字符串
     * @return 格式化后的文本
     */
    fun join(items: Array<String>, separator: String): String {
        val sb = StringBuffer()
        sb.append(items[0])
        for (i in 1 until items.size) {
            sb.append(separator)
            sb.append(items[i])
        }
        return String(sb)
    }


    /**
     * 连接字符串
     *
     * @param items     待连接的字符串列表
     * @param separator 分隔字符串
     * @return 格式化后的文本
     */
    fun join(items: List<String>, separator: String): String {
        val sb = StringBuffer()
        sb.append(items[0])
        for (i in 1 until items.size) {
            sb.append(separator)
            sb.append(items[i])
        }
        return String(sb)
    }

    /**
     * 显示提示信息
     * @param context 当前上下文
     * @param str 要显示的字符串信息
     */
    fun showToast(context: Context, str: String) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
    }

    /**
     * 显示提示信息
     * @param context 当前上下文
     * @param resId 要显示的字符串在strings文件中的ID
     */
    fun showToast(context: Context, resId: Int) {
        Toast.makeText(context, resId, Toast.LENGTH_SHORT).show()
    }

    /**
     * 打印日志
     * @param tag 日志标签
     * @param msg 日志信息
     */
    fun showLog(tag: String, msg: String) {
        Log.e(tag, msg)
    }

    /**
     * 打印日志
     * @param tag 日志标签
     * @param i 数字作为日志信息
     */
    fun showLog(tag: String, i: Int) {
        Log.e(tag, "" + i)
    }

    /**
     * 复制到剪贴板
     * @param context 当前上下文
     * @param text 要复制的内容
     */
    fun copyToClipboard(context: Context, text: String) {
        val cmb = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("label", text) //文本型数据 clipData 的构造方法。
        cmb.primaryClip = clipData
    }
}

