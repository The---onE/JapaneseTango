package com.xmx.tango.Tango;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.xmx.tango.R;

/**
 * Created by The_onE on 2016/9/21.
 */

public class VerbDialog extends Dialog {

    private String verb;
    private int type;

    final char[] aStatus = new char[]{'あ', 'か', 'が', 'さ', 'た', 'な', 'ば', 'ま', 'ら'};
    final char[] iStatus = new char[]{'い', 'き', 'ぎ', 'し', 'ち', 'に', 'び', 'み', 'り'};
    final char[] uStatus = new char[]{'う', 'く', 'ぐ', 'す', 'つ', 'ぬ', 'ぶ', 'む', 'る'};
    final char[] eStatus = new char[]{'え', 'け', 'げ', 'せ', 'て', 'ね', 'べ', 'め', 'れ'};
    final char[] oStatus = new char[]{'お', 'こ', 'ご', 'そ', 'と', 'の', 'ぼ', 'も', 'ろ'};

    final char[] iteTail = new char[]{'く'};
    final char[] ideTail = new char[]{'ぐ'};
    final char[] tteTail = new char[]{'う', 'つ', 'る'};
    final char[] ndeTail = new char[]{'ぶ', 'ぬ', 'む'};

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
                for (char t : iteTail) {
                    if (tail == t) {
                        te = s.concat("いて");
                        break;
                    }
                }
                for (char t : ideTail) {
                    if (tail == t) {
                        te = s.concat("いで");
                        break;
                    }
                }
                for (char t : tteTail) {
                    if (tail == t) {
                        te = s.concat("って");
                        break;
                    }
                }
                for (char t : ndeTail) {
                    if (tail == t) {
                        te = s.concat("んで");
                        break;
                    }
                }
                //た形
                for (char t : iteTail) {
                    if (tail == t) {
                        ta = s.concat("いた");
                        break;
                    }
                }
                for (char t : ideTail) {
                    if (tail == t) {
                        ta = s.concat("いだ");
                        break;
                    }
                }
                for (char t : tteTail) {
                    if (tail == t) {
                        ta = s.concat("った");
                        break;
                    }
                }
                for (char t : ndeTail) {
                    if (tail == t) {
                        ta = s.concat("んだ");
                        break;
                    }
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
                    if (i > 0) {
                        char tail = temp.charAt(i - 1);
                        for (int j = 0; j < iStatus.length; ++j) {
                            if (tail == iStatus[j]) {
                                temp = temp.substring(0, i - 1).concat("" + uStatus[j]);
                                break;
                            }
                        }
                    }
                    break;
                case 2:
                    temp = temp.concat("る");
                    break;
                case 3:
                    if (temp.charAt(i - 1) == 'し') {
                        temp = temp.substring(0, i - 1);
                        temp = temp.concat("する");
                    }
                    break;
            }
            return temp;
        } else {
            return verb;
        }
    }
}
