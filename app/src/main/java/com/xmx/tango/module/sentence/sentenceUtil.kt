package com.xmx.tango.module.sentence

import com.atilika.kuromoji.ipadic.Token
import com.atilika.kuromoji.ipadic.Tokenizer

/**
 * Created by The_onE on 2017/9/12.
 * 句子分词工具
 */

object sentenceUtil {
    // 分词工具
    private var tokenizer: Tokenizer? = null

    // 平假名表
    private val hiragana = "ぁあぃいぅうぇえぉおかがきぎくぐけげこご" +
            "さざしじすずせぜそぞただちぢっつづてでとどなにぬねの" +
            "はばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんヴヵヶ"
    // 片假名表
    private val katakana = "ァアィイゥウェエォオカガキギクグケゲコゴ" +
            "サザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノ" +
            "ハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヴヵヶ"

    /**
     * 初始化分词工具
     */
    fun init() {
        if (tokenizer == null) {
            tokenizer = Tokenizer()
        }
    }

    /**
     * 分析句子
     * @param sentence 待分析的句子
     * @return 句子成分列表
     */
    fun analyzeSentence(sentence: String): List<Token> {
        init()
        return tokenizer!!.tokenize(sentence)
    }

    /**
     * 将片假名转换为平假名
     * @param tango 片假名字符串
     * @return 平假名字符串
     */
    fun convertKana(tango: String): String {
        val sb = StringBuilder(tango)
        for (i in 0..sb.length - 1) {
            val c = sb[i]
            val index = katakana.indexOf(c)
            if (index > 0) {
                sb.replace(i, i + 1, "" + hiragana[index])
            }
        }
        return sb.toString()
    }
}
