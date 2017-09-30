package com.xmx.tango.module.verb

import android.app.FragmentManager
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.xmx.tango.R
import com.xmx.tango.base.dialog.BaseDialog
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.module.tango.TangoConstants

import java.util.ArrayList
import java.util.HashMap

/**
 * Created by The_onE on 2016/9/21.
 * 动词变形对话框
 */
class VerbDialog : BaseDialog() {

    private var verb: String? = null // 动词原型，辞书形
    private var type: Int = 0 // 1：五段动词 2：一段动词 3：特殊动词
    private val textViews = ArrayList<TextView>()

    /**
     * 初始化对话框，调用show()之前调用
     */
    fun initDialog(context: Context, verb: String, type: Int) {
        super.initDialog(context)
        this.verb = convertVerb(verb, type)
        this.type = type
    }

    companion object {
        private val TEXT_SIZE = 28f // 字体大小

        // 五段词尾
        private val aStatus = charArrayOf('わ', 'か', 'が', 'さ', 'た', 'な', 'ば', 'ま', 'ら')
        private val iStatus = charArrayOf('い', 'き', 'ぎ', 'し', 'ち', 'に', 'び', 'み', 'り')
        private val uStatus = charArrayOf('う', 'く', 'ぐ', 'す', 'つ', 'ぬ', 'ぶ', 'む', 'る')
        private val eStatus = charArrayOf('え', 'け', 'げ', 'せ', 'て', 'ね', 'べ', 'め', 'れ')
        private val oStatus = charArrayOf('お', 'こ', 'ご', 'そ', 'と', 'の', 'ぼ', 'も', 'ろ')

        private val teOnbin: MutableMap<String, CharArray> = HashMap() // て形词尾映射
        private val taOnbin: MutableMap<String, CharArray> = HashMap() // た形词尾映射

        init {
            teOnbin.put("いて", charArrayOf('く'))
            taOnbin.put("いた", charArrayOf('く'))

            teOnbin.put("いて", charArrayOf('く'))
            taOnbin.put("いた", charArrayOf('く'))

            teOnbin.put("いで", charArrayOf('ぐ'))
            taOnbin.put("いだ", charArrayOf('ぐ'))

            teOnbin.put("って", charArrayOf('う', 'つ', 'る'))
            taOnbin.put("った", charArrayOf('う', 'つ', 'る'))

            teOnbin.put("んで", charArrayOf('ぶ', 'ぬ', 'む'))
            taOnbin.put("んだ", charArrayOf('ぶ', 'ぬ', 'む'))

            teOnbin.put("して", charArrayOf('す'))
            taOnbin.put("した", charArrayOf('す'))
        }
    }

    /**
     * ます形转为辞书形
     * @param verb 动词ます形
     * @param type 动词分类
     * @return 动词辞书形
     */
    private fun convertVerb(verb: String, type: Int): String {
        val i = verb.lastIndexOf("ます")
        if (i > 0) {
            var temp = verb.substring(0, i) //去掉ます
            when (type) {
                1 -> {
                    val tail = temp[i - 1]
                    //五段动词将い段词尾变う段
                    for (j in iStatus.indices) {
                        if (tail == iStatus[j]) {
                            temp = temp.substring(0, i - 1) + ("" + uStatus[j])
                            break
                        }
                    }
                }
                2 ->
                    //一段动词直接加る
                    temp += "る"
                3 ->
                    if (temp[i - 1] == 'し') {
                        //サ变动词，し变する
                        temp = temp.substring(0, i - 1)
                        temp += "する"
                    } else if (temp[i - 1] == 'き' || temp[i - 1] == '来') {
                        //カ变动词，来(き) 变 来(く)る
                        temp = temp.substring(0, i - 1)
                        temp += "くる"
                    }
            }
            return temp
        } else {
            //不是ます形则直接返回
            return verb
        }
    }

    override fun show(manager: FragmentManager, tag: String) {
        if (type > 0) {
            super.show(manager, tag)
        }
    }

    override fun getContentView(inflater: LayoutInflater, container: ViewGroup?): View =
            inflater.inflate(R.layout.dialog_verb, container)

    override fun initView(view: View, savedInstanceState: Bundle?) {
        verb?.apply {
            val cishu = this // 辞书形
            var lianyong: String = this // 连用形
            var te: String = this // て形
            var ta: String = this // た形
            var weiran: String = this // 未然形
            var yizhi: String = this // 意志形
            var mingling: String = this // 命令形
            var jiading: String = this // 假定形
            var keneng: String = this // 可能态
            var shiyi: String = this // 使役态
            var beidong: String = this // 被动态
            var zifa: String = this // 自发态
            var shiyibeidong: String = this // 使役被动态

            val len = this.length
            if (len > 0) {
                var s = this.substring(0, len - 1) //去掉词尾
                when (type) {
                // 五段动词
                    1 -> {
                        //词尾所在行
                        val tail = this[len - 1]
                        val row = uStatus.indices.firstOrNull { tail == uStatus[it] } ?: -1
                        if (row >= 0) {
                            //连用形
                            lianyong = s + ("" + iStatus[row])
                            //未然形
                            weiran = s + ("" + aStatus[row])
                            //意志形
                            yizhi = s + ("" + oStatus[row] + "う")
                            //命令形
                            mingling = s + ("" + eStatus[row])
                            //假定形
                            jiading = s + ("" + eStatus[row] + "ば")
                            //可能态
                            keneng = s + ("" + eStatus[row] + "る")
                            //使役态
                            shiyi = s + ("" + aStatus[row] + "せる")
                            //被动态
                            beidong = s + ("" + aStatus[row] + "れる")
                            //自发态
                            zifa = s + ("" + aStatus[row] + "れる")
                            //使役被动
                            shiyibeidong = s + ("" + aStatus[row] + "される")

                            //て形
                            for ((key, value) in teOnbin) {
                                var flag = false
                                for (t in value) {
                                    if (tail == t) {
                                        te = s + key
                                        flag = true
                                        break
                                    }
                                }
                                if (flag) {
                                    break
                                }
                            }
                            // 特殊情况
                            when (cishu) {
                                "行く" -> te = "行って"
                                "問う" -> te = "問うて"
                                "乞う" -> te = "乞うて"
                            }

                            //た形
                            for ((key, value) in taOnbin) {
                                var flag = false
                                for (t in value) {
                                    if (tail == t) {
                                        ta = s + key
                                        flag = true
                                        break
                                    }
                                }
                                if (flag) {
                                    break
                                }
                            }
                            // 特殊情况
                            when (cishu) {
                                "行く" -> ta = "行った"
                                "問う" -> ta = "問うた"
                                "乞う" -> ta = "乞うた"
                            }
                        }
                    }
                    2 -> {
                        //连用形
                        lianyong = s + ""
                        //未然形
                        weiran = s + ""
                        //意志形
                        yizhi = s + "よう"
                        //命令形
                        mingling = s + "ろ"
                        //假定形
                        jiading = s + "れば"
                        //可能态
                        keneng = s + "られる"
                        //使役态
                        shiyi = s + "させる"
                        //被动态
                        beidong = s + "られる"
                        //自发态
                        zifa = s + "られる"
                        //使役被动
                        shiyibeidong = s + "させられる"
                        //て形
                        te = s + "て"
                        //た形
                        ta = s + "た"
                    }
                    3 ->
                        if (this.lastIndexOf("する") == len - 2) {
                            //サ变动词
                            s = this.substring(0, len - 2) //去掉する
                            //连用形
                            lianyong = s + "し"
                            //未然形
                            weiran = s + "し"
                            //意志形
                            yizhi = s + "しよう"
                            //命令形
                            mingling = s + "しろ"
                            //假定形
                            jiading = s + "すれば"
                            //可能态
                            keneng = s + "できる"
                            //使役态
                            shiyi = s + "させる"
                            //被动态
                            beidong = s + "される"
                            //自发态
                            zifa = s + "される"
                            //使役被动
                            shiyibeidong = s + "させられる"
                            //て形
                            te = s + "して"
                            //た形
                            ta = s + "した"
                        } else if (this.lastIndexOf("くる") >= 0) {
                            //カ变动词
                            s = this.substring(0, len - 2) //去掉くる
                            //连用形
                            lianyong = s + "き"
                            //未然形
                            weiran = s + "こ"
                            //意志形
                            yizhi = s + "こよう"
                            //命令形
                            mingling = s + "こい"
                            //假定形
                            jiading = s + "くれば"
                            //可能态
                            keneng = s + "こられる"
                            //使役态
                            shiyi = s + "こさせる"
                            //被动态
                            beidong = s + "こられる"
                            //自发态
                            zifa = s + "こられる"
                            //使役被动
                            shiyibeidong = s + "こさせられる"
                            //て形
                            te = s + "きて"
                            //た形
                            ta = s + "きた"
                        }
                    else -> return
                }
                val cishuView = view.findViewById(R.id.tv_verb_cishu) as TextView
                cishuView.text = cishu
                textViews.add(cishuView)

                val lianyongView = view.findViewById(R.id.tv_verb_lianyong) as TextView
                lianyongView.text = lianyong
                textViews.add(lianyongView)

                val teView = view.findViewById(R.id.tv_verb_te) as TextView
                teView.text = te
                textViews.add(teView)

                val taView = view.findViewById(R.id.tv_verb_ta) as TextView
                taView.text = ta
                textViews.add(taView)

                val weiranView = view.findViewById(R.id.tv_verb_weiran) as TextView
                weiranView.text = weiran
                textViews.add(weiranView)

                val yizhiView = view.findViewById(R.id.tv_verb_yizhi) as TextView
                yizhiView.text = yizhi
                textViews.add(yizhiView)

                val minglingView = view.findViewById(R.id.tv_verb_mingling) as TextView
                minglingView.text = mingling
                textViews.add(minglingView)

                val jiadingView = view.findViewById(R.id.tv_verb_jiading) as TextView
                jiadingView.text = jiading
                textViews.add(jiadingView)

                val kenengView = view.findViewById(R.id.tv_verb_keneng) as TextView
                kenengView.text = keneng
                textViews.add(kenengView)

                val shiyiView = view.findViewById(R.id.tv_verb_shiyi) as TextView
                shiyiView.text = shiyi
                textViews.add(shiyiView)

                val beidongView = view.findViewById(R.id.tv_verb_beidong) as TextView
                beidongView.text = beidong
                textViews.add(beidongView)

                val zifaView = view.findViewById(R.id.tv_verb_zifa) as TextView
                zifaView.text = zifa
                textViews.add(zifaView)

                val shiyibeidongView = view.findViewById(R.id.tv_verb_shiyibeidong) as TextView
                shiyibeidongView.text = shiyibeidong
                textViews.add(shiyibeidongView)

                // 处理每一个文本框
                for (tv in textViews) {
                    tv.textSize = TEXT_SIZE

                    // 设置字体
                    val mgr = mContext.assets
                    val title = DataManager.getInstance().japaneseFontTitle
                    var font: String? = null
                    if (title != null) {
                        font = TangoConstants.JAPANESE_FONT_MAP[title]
                    }
                    var tf = Typeface.DEFAULT
                    if (font != null) {
                        tf = Typeface.createFromAsset(mgr, font)
                    }
                    tv.typeface = tf
                }
            }
        }
    }

    override fun setListener(view: View) {

    }

    override fun processLogic(view: View, savedInstanceState: Bundle?) {

    }
}
