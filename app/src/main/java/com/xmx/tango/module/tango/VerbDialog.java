package com.xmx.tango.module.tango;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.xmx.tango.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by The_onE on 2016/9/21.
 */

public class VerbDialog extends Dialog {

    private String verb;
    private int type;

    static final char[] aStatus = new char[]{'わ', 'か', 'が', 'さ', 'た', 'な', 'ば', 'ま', 'ら'};
    static final char[] iStatus = new char[]{'い', 'き', 'ぎ', 'し', 'ち', 'に', 'び', 'み', 'り'};
    static final char[] uStatus = new char[]{'う', 'く', 'ぐ', 'す', 'つ', 'ぬ', 'ぶ', 'む', 'る'};
    static final char[] eStatus = new char[]{'え', 'け', 'げ', 'せ', 'て', 'ね', 'べ', 'め', 'れ'};
    static final char[] oStatus = new char[]{'お', 'こ', 'ご', 'そ', 'と', 'の', 'ぼ', 'も', 'ろ'};

    static final Map<String, char[]> teOnbin = new HashMap<>();
    static final Map<String, char[]> taOnbin = new HashMap<>();
    static {
        teOnbin.put("いて", new char[]{'く'});
        taOnbin.put("いた", new char[]{'く'});

        teOnbin.put("いて", new char[]{'く'});
        taOnbin.put("いた", new char[]{'く'});

        teOnbin.put("いで", new char[]{'ぐ'});
        taOnbin.put("いだ", new char[]{'ぐ'});

        teOnbin.put("って", new char[]{'う', 'つ', 'る'});
        taOnbin.put("った", new char[]{'う', 'つ', 'る'});

        teOnbin.put("んで", new char[]{'ぶ', 'ぬ', 'む'});
        taOnbin.put("んだ", new char[]{'ぶ', 'ぬ', 'む'});

        teOnbin.put("して", new char[]{'す'});
        taOnbin.put("した", new char[]{'す'});
    }

    public VerbDialog(Context context, String verb, int type) {
        super(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar);
        this.verb = convertVerb(verb, type);
        this.type = type;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_verb);

        String cishu = verb;
        String lianyong = verb;
        String te = verb;
        String ta = verb;
        String weiran = verb;
        String yizhi = verb;
        String mingling = verb;
        String jiading = verb;
        String keneng = verb;
        String shiyi = verb;
        String beidong = verb;
        String zifa = verb;
        String shiyibeidong = verb;

        int len = verb.length();
        String s = verb.substring(0, len - 1); //去掉词尾
        switch (type) {
            case 1:
                //词尾所在行
                int row = -1;
                char tail = verb.charAt(verb.length() - 1);
                for (int i = 0; i < uStatus.length; ++i) {
                    if (tail == uStatus[i]) {
                        row = i;
                        break;
                    }
                }
                //连用形
                lianyong = s.concat("" + iStatus[row]);
                //未然形
                weiran = s.concat("" + aStatus[row]);
                //意志形
                yizhi = s.concat("" + oStatus[row] + "う");
                //命令形
                mingling = s.concat("" + eStatus[row]);
                //假定形
                jiading = s.concat("" + eStatus[row] + "ば");
                //可能态
                keneng = s.concat("" + eStatus[row] + "る");
                //使役态
                shiyi = s.concat("" + aStatus[row] + "せる");
                //被动态
                beidong = s.concat("" + aStatus[row] + "れる");
                //自发态
                zifa = s.concat("" + aStatus[row] + "れる");
                //使役被动
                shiyibeidong = s.concat("" + aStatus[row] + "される");

                //て形
                for (Map.Entry<String, char[]> entry : teOnbin.entrySet()) {
                    boolean flag = false;
                    for (char t : entry.getValue()) {
                        if (tail == t) {
                            te = s.concat(entry.getKey());
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                if (cishu.equals("行く")) {
                    te = "行って";
                }
                if (cishu.equals("問う")) {
                    te = "問うて";
                }
                if (cishu.equals("乞う")) {
                    te = "乞うて";
                }
                //た形
                for (Map.Entry<String, char[]> entry : taOnbin.entrySet()) {
                    boolean flag = false;
                    for (char t : entry.getValue()) {
                        if (tail == t) {
                            ta = s.concat(entry.getKey());
                            flag = true;
                            break;
                        }
                    }
                    if (flag) {
                        break;
                    }
                }
                if (cishu.equals("行く")) {
                    ta = "行った";
                }
                if (cishu.equals("問う")) {
                    ta = "問うた";
                }
                if (cishu.equals("乞う")) {
                    ta = "乞うた";
                }
                break;
            case 2:
                //连用形
                lianyong = s.concat("");
                //未然形
                weiran = s.concat("");
                //意志形
                yizhi = s.concat("よう");
                //命令形
                mingling = s.concat("ろ");
                //假定形
                jiading = s.concat("れば");
                //可能态
                keneng = s.concat("られる");
                //使役态
                shiyi = s.concat("させる");
                //被动态
                beidong = s.concat("られる");
                //自发态
                zifa = s.concat("られる");
                //使役被动
                shiyibeidong = s.concat("させられる");
                //て形
                te = s.concat("て");
                //た形
                ta = s.concat("た");
                break;
            case 3:
                if (verb.lastIndexOf("する") == verb.length() - 2) {
                    //サ变动词
                    s = verb.substring(0, len - 2); //去掉する
                    //连用形
                    lianyong = s.concat("し");
                    //未然形
                    weiran = s.concat("し");
                    //意志形
                    yizhi = s.concat("しよう");
                    //命令形
                    mingling = s.concat("しろ");
                    //假定形
                    jiading = s.concat("すれば");
                    //可能态
                    keneng = s.concat("できる");
                    //使役态
                    shiyi = s.concat("させる");
                    //被动态
                    beidong = s.concat("される");
                    //自发态
                    zifa = s.concat("される");
                    //使役被动
                    shiyibeidong = s.concat("させられる");
                    //て形
                    te = s.concat("して");
                    //た形
                    ta = s.concat("した");
                } else if (verb.lastIndexOf("くる") >= 0) {
                    //カ变动词
                    s = verb.substring(0, len - 2); //去掉くる
                    //连用形
                    lianyong = s.concat("き");
                    //未然形
                    weiran = s.concat("こ");
                    //意志形
                    yizhi = s.concat("こよう");
                    //命令形
                    mingling = s.concat("こい");
                    //假定形
                    jiading = s.concat("くれば");
                    //可能态
                    keneng = s.concat("こられる");
                    //使役态
                    shiyi = s.concat("こさせる");
                    //被动态
                    beidong = s.concat("こられる");
                    //自发态
                    zifa = s.concat("こられる");
                    //使役被动
                    shiyibeidong = s.concat("こさせられる");
                    //て形
                    te = s.concat("きて");
                    //た形
                    ta = s.concat("きた");
                }
                break;
            default:
                return;
        }

        TextView cishuView = (TextView) findViewById(R.id.tv_verb_cishu);
        cishuView.setText(cishu);

        TextView lianyongView = (TextView) findViewById(R.id.tv_verb_lianyong);
        lianyongView.setText(lianyong);

        TextView teView = (TextView) findViewById(R.id.tv_verb_te);
        teView.setText(te);

        TextView taView = (TextView) findViewById(R.id.tv_verb_ta);
        taView.setText(ta);

        TextView weiranView = (TextView) findViewById(R.id.tv_verb_weiran);
        weiranView.setText(weiran);

        TextView yizhiView = (TextView) findViewById(R.id.tv_verb_yizhi);
        yizhiView.setText(yizhi);

        TextView minglingView = (TextView) findViewById(R.id.tv_verb_mingling);
        minglingView.setText(mingling);

        TextView jiadingView = (TextView) findViewById(R.id.tv_verb_jiading);
        jiadingView.setText(jiading);

        TextView kenengView = (TextView) findViewById(R.id.tv_verb_keneng);
        kenengView.setText(keneng);

        TextView shiyiView = (TextView) findViewById(R.id.tv_verb_shiyi);
        shiyiView.setText(shiyi);

        TextView beidongView = (TextView) findViewById(R.id.tv_verb_beidong);
        beidongView.setText(beidong);

        TextView zifaView = (TextView) findViewById(R.id.tv_verb_zifa);
        zifaView.setText(zifa);

        TextView shiyibeidongView = (TextView) findViewById(R.id.tv_verb_shiyibeidong);
        shiyibeidongView.setText(shiyibeidong);
    }

    //ます形转为辞书形
    private String convertVerb(String verb, int type) {
        int i = verb.lastIndexOf("ます");
        if (i > 0) {
            String temp = verb.substring(0, i); //去掉ます
            switch (type) {
                case 1:
                    char tail = temp.charAt(i - 1);
                    //五段动词将い段词尾变う段
                    for (int j = 0; j < iStatus.length; ++j) {
                        if (tail == iStatus[j]) {
                            temp = temp.substring(0, i - 1).concat("" + uStatus[j]);
                            break;
                        }
                    }
                    break;
                case 2:
                    //一段动词直接加る
                    temp = temp.concat("る");
                    break;
                case 3:
                    if (temp.charAt(i - 1) == 'し') {
                        //サ变动词，し变する
                        temp = temp.substring(0, i - 1);
                        temp = temp.concat("する");
                    } else if (temp.charAt(i - 1) == 'き' || temp.charAt(i - 1) == '来') {
                        //カ变动词，来(き) 变 来(く)る
                        temp = temp.substring(0, i - 1);
                        temp = temp.concat("くる");
                    }
                    break;
            }
            return temp;
        } else {
            //不是ます形则直接返回
            return verb;
        }
    }

    @Override
    public void show() {
        if (type > 0) {
            super.show();
        }
    }
}
