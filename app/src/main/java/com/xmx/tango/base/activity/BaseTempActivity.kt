package com.xmx.tango.base.activity

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent

import kotlinx.android.synthetic.main.tool_bar.*

/**
 * Created by The_onE on 2016/1/1.
 * 临时Activity基类，增加右滑关闭功能，工具栏带有返回按钮
 * 布局文件中需添加 <include layout="@layout/tool_bar" />
 * AndroidManifest.xml中对应的Activity需添加 android:theme="@style/AppTheme.NoActionBar"
 */
abstract class BaseTempActivity : BaseActivity() {
    // 手势识别，在Activity创建后创建
    private val mGestureDetector: GestureDetector by lazy {
        GestureDetector(this,
                // 添加右滑关闭功能
                object : GestureDetector.SimpleOnGestureListener() {
                    override fun onFling(e1: MotionEvent, e2: MotionEvent,
                                         velocityX: Float, velocityY: Float): Boolean {
                        if (velocityX > Math.abs(velocityY)) {
                            onBackPressed()
                        }
                        return super.onFling(e1, e2, velocityX, velocityY)
                    }
                })
    }

    /**
     * 触摸时添加对手势的识别
     * @param[event] 触摸事件
     * @return true:事件已处理完成，不向下传递 false:事件未处理完成，向下传递
     */
    override fun onTouchEvent(event: MotionEvent): Boolean = mGestureDetector.onTouchEvent(event)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化工具栏
        // 布局中必须要有 <include layout="@layout/tool_bar" />
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // 工具栏添加返回按钮
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}
