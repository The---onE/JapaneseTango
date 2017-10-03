package com.xmx.tango.module.sentence

import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.util.TreeMap
import java.util.regex.Pattern

/**
 * 此类用来解析LRC文件
 * 将解析完整的LRC文件放入一个LrcInfo对象中
 * 并且返回这个LrcInfo对象
 * http://java-mzd.iteye.com/blog/811374
 */
object LrcParser {
    /**
     * 歌词信息
     */
    class LrcInfo {
        var title: String? = null // 歌曲名
        var singer: String? = null // 演唱者
        var album: String? = null // 专辑
        var info: TreeMap<Long, String>? = null // 保存歌词信息和时间的映射
    }

    /**
     * 将输入流中的信息解析，返回一个LrcInfo对象
     *
     * @param inputStream 输入流
     * @return 解析好的LrcInfo对象
     * @throws IOException
     */
    @Throws(IOException::class)
    fun parser(reader: BufferedReader): LrcInfo {
        val lrcInfo = LrcInfo()
        val maps = TreeMap<Long, String>() // 歌词与时间的映射
        // 一行一行的读，每读一行，解析一行
        var line: String? = reader.readLine()
        while (line != null) {
            parserLine(line, lrcInfo, maps)
            line = reader.readLine()
        }
        // 全部解析完后，设置info
        lrcInfo.info = maps
        return lrcInfo
    }

    /**
     * 利用正则表达式解析每行具体语句
     * 并在解析完该语句后，将解析出来的信息设置在LrcInfo对象中
     *
     * @param str
     * @param lrcInfo 已解析的歌曲信息
     * @param maps 已解析的歌词与时间的映射
     */
    private fun parserLine(str: String, lrcInfo: LrcInfo, maps: TreeMap<Long, String>) =// 取得歌曲名信息
            when {
                str.startsWith("[ti:") -> {
                    // 取得歌曲名信息
                    val title = str.substring(4, str.length - 1)
                    lrcInfo.title = title
                }
                str.startsWith("[ar:") -> {
                    // 取得歌手信息
                    val singer = str.substring(4, str.length - 1)
                    lrcInfo.singer = singer
                }
                str.startsWith("[al:") -> {
                    // 取得专辑信息
                    val album = str.substring(4, str.length - 1)
                    lrcInfo.album = album
                }
                else -> {
                    // 通过正则取得每句歌词信息
                    // 设置正则规则
                    val reg = "\\[(\\d{2}:\\d{2}\\.\\d{2})]"
                    // 编译
                    val pattern = Pattern.compile(reg)
                    val matcher = pattern.matcher(str)
                    // 如果存在匹配项，则执行以下操作
                    while (matcher.find()) {
                        var currentTime: Long = 0 // 存放临时时间
                        // 得到这个匹配项中的组数
                        val groupCount = matcher.groupCount()
                        // 得到每个组中内容
                        for (i in 0..groupCount) {
                            val timeStr = matcher.group(i)
                            if (i == 1) {
                                // 将第二组中的内容设置为当前的一个时间点
                                currentTime = strToLong(timeStr)
                            }
                        }
                        // 得到时间点后的内容
                        val content = pattern.split(str)
                        if (content.isNotEmpty()) {
                            // 将内容设置为当前内容
                            val currentContent = content[content.size - 1]
                            // 设置时间点和内容的映射
                            currentContent?.apply { maps.put(currentTime, this) }
                        } else {
                            maps.put(currentTime, "")
                        }
                    }
                }
            }

    /**
     * 将解析得到的表示时间的字符转化为Long型
     * @param timeStr 时间字符串
     * @return Long类型的时间
     */
    private fun strToLong(timeStr: String): Long {
        // 因为给如的字符串的时间格式为XX:XX.XX,返回的long要求是以毫秒为单位
        // 1:使用：分割 2：使用.分割
        val s = timeStr.split(":".toRegex())
        val min = Integer.parseInt(s[0])
        val ss = s[1].split("\\.".toRegex())
        val sec = Integer.parseInt(ss[0])
        val mill = Integer.parseInt(ss[1])
        return (min * 60 * 1000 + sec * 1000 + mill * 10).toLong()
    }
}
