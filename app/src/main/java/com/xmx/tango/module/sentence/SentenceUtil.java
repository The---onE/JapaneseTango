package com.xmx.tango.module.sentence;

import com.atilika.kuromoji.ipadic.Token;
import com.atilika.kuromoji.ipadic.Tokenizer;

import java.util.List;

/**
 * Created by The_onE on 2017/9/12.
 */

public class SentenceUtil {
    private static Tokenizer tokenizer;

    private static String hiragana = "ぁあぃいぅうぇえぉおかがきぎくぐけげこご" +
            "さざしじすずせぜそぞただちぢっつづてでとどなにぬねの" +
            "はばぱひびぴふぶぷへべぺほぼぽまみむめもゃやゅゆょよらりるれろゎわゐゑをんヴヵヶ";
    private static String katakana = "ァアィイゥウェエォオカガキギクグケゲコゴ" +
            "サザシジスズセゼソゾタダチヂッツヅテデトドナニヌネノ" +
            "ハバパヒビピフブプヘベペホボポマミムメモャヤュユョヨラリルレロヮワヰヱヲンヴヵヶ";

    public static void init() {
        if (tokenizer == null) {
            tokenizer = new Tokenizer();
        }
    }

    static List<Token> analyzeSentence(String sentence) {
        init();
        return tokenizer.tokenize(sentence);
    }

    static String convertKana(String tango) {
        StringBuilder sb = new StringBuilder(tango);
        for (int i = 0; i < sb.length(); ++i) {
            char c = sb.charAt(i);
            int index = katakana.indexOf(c);
            if (index > 0) {
                sb.replace(i, i + 1, "" + hiragana.charAt(index));
            }
        }
        return sb.toString();
    }
}
