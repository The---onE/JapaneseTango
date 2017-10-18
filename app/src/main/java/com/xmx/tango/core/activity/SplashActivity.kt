package com.xmx.tango.core.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.view.View

import com.xmx.tango.core.CoreConstants
import com.xmx.tango.R
import com.xmx.tango.module.calendar.DateData
import com.xmx.tango.module.calendar.DateDataEntityManager
import com.xmx.tango.module.sentence.SentenceUtil
import com.xmx.tango.module.tango.TangoConstants
import com.xmx.tango.module.tango.TangoEntityManager
import com.xmx.tango.module.crud.TangoListChangeEvent
import com.xmx.tango.module.tango.TangoManager
import com.xmx.tango.base.activity.BaseSplashActivity
import com.xmx.tango.common.data.DataManager
import com.xmx.tango.utils.ExceptionUtil
import com.xmx.tango.utils.Timer
import kotlinx.android.synthetic.main.activity_splash.*

import org.greenrobot.eventbus.EventBus
import java.util.*

class SplashActivity : BaseSplashActivity() {
    // 跳转至主Activity定时器
    private val timer = Timer {
        timeFlag = true
        skip()
    }
    private var readyFlag = false // 数据库是否已更新完毕
    private var timeFlag = false // 是否已过自动跳转时间
    private var skipFlag = false // 是否已跳转

    private val writeSdRequest = 1 // 申请读写SD数据权限

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_splash)
    }

    override fun setListener() {
        // 点击跳过按钮直接跳到主页
        btnSkip.setOnClickListener { skip() }
        btnPermission.setOnClickListener { checkPermission() }
    }

    override fun processLogic(savedInstanceState: Bundle?) {
        checkPermission()
    }

    private fun checkPermission() {
        // 检验是否有读写SD数据权限
        if (checkLocalPhonePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, writeSdRequest)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                if (checkOpsPermission(AppOpsManager.OPSTR_WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE, writeSdRequest)) {
                    // 具有权限则进行初始化
                    init()
                }
            } else {
                // 具有权限则进行初始化
                init()
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    private fun init() {
        // 开启跳转定时器
        timer.start(CoreConstants.SPLASH_TIME, true)
        // 设置列表筛选条件
        TangoManager.writing = DataManager.getSearchValue("writing")
        TangoManager.pronunciation = DataManager.getSearchValue("pronunciation")
        TangoManager.meaning = DataManager.getSearchValue("meaning")
        TangoManager.partOfSpeech = DataManager.getSearchValue("partOfSpeech")
        TangoManager.type = DataManager.getSearchValue("type")
        // 查询单语列表
        TangoManager.updateTangoList()

        // 根据上次进行遗忘减分的时间来处理
        val last = Date(DataManager.forgetLastTime)
        val lastCalendar = Calendar.getInstance()
        lastCalendar.time = last
        val now = Date()
        val nowCalendar = Calendar.getInstance()
        if (!CoreConstants.isSameDate(now, last)) {
            // 若不在同一天
            if (last.time > 0) {
                // 更新上次签到日数据
                var dateData = DateDataEntityManager
                        .selectFirst("addTime", false,
                                "Year=" + (lastCalendar.get(Calendar.YEAR)),
                                "Month=" + (lastCalendar.get(Calendar.MONTH) + 1),
                                "Date=" + lastCalendar.get(Calendar.DATE))
                if (dateData != null) {
                    // 更新完成任务数
                    DateDataEntityManager.updateData(dateData.id,
                            "Mission=" + DataManager.todayMission)
                } else {
                    // 添加上次签到数据
                    dateData = DateData()
                    dateData.year = lastCalendar.get(Calendar.YEAR)
                    dateData.month = lastCalendar.get(Calendar.MONTH) + 1
                    dateData.date = lastCalendar.get(Calendar.DATE)
                    dateData.checkIn = 1
                    dateData.mission = DataManager.todayMission
                    dateData.addTime = now
                    DateDataEntityManager.insertData(dateData)
                }
            }
            // 今天打卡签到
            var todayData = DateDataEntityManager
                    .selectFirst("addTime", false,
                            "Year=" + (nowCalendar.get(Calendar.YEAR)),
                            "Month=" + (nowCalendar.get(Calendar.MONTH) + 1),
                            "Date=" + nowCalendar.get(Calendar.DATE))
            if (todayData == null) {
                // 添加今天打开签到数据
                todayData = DateData()
                todayData.year = nowCalendar.get(Calendar.YEAR)
                todayData.month = nowCalendar.get(Calendar.MONTH) + 1
                todayData.date = nowCalendar.get(Calendar.DATE)
                todayData.checkIn = 1
                todayData.addTime = now
                DateDataEntityManager.insertData(todayData)
            }
            // 重置上次数据
            DataManager.forgetLastTime = now.time
            DataManager.todayMission = 0

            // 进行单语遗忘减分
            object : AsyncTask<Void, Void, Void>() {
                override fun doInBackground(vararg voids: Void?): Void? {
                    try {
                        val tangoList = TangoManager.tangoList
                        // 将所有分数大于0的单语进行遗忘减分
                        tangoList.filter { it.score > 0 }.forEach {
                            var newScore = TangoConstants.forgottenScore(it.score)
                            if (newScore <= 0) {
                                // 学习过的单语最低分为1
                                newScore = 1
                            }
                            // 在数据库中更新分数
                            TangoEntityManager.updateData(it.id,
                                    "Score=" + newScore)
                        }
                    } catch (e: Exception) {
                        ExceptionUtil.normalException(e, this@SplashActivity)
                    }
                    return null
                }

                override fun onPostExecute(aVoid: Void?) {
                    super.onPostExecute(aVoid)
                    // 当所有单语处理完后可以跳转至主页
                    ready()
                    if (timeFlag) {
                        skip()
                    }
                    EventBus.getDefault().post(TangoListChangeEvent())
                }
            }.execute()
        } else {
            // 若今天运行过，则可以直接跳到主页
            ready()
        }

        // 初始化分词工具
        Thread(Runnable { SentenceUtil.init() }).start()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            writeSdRequest -> if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // 用户拒绝了读写SD数据权限
                showToast("您拒绝了读写手机存储的权限，某些功能会导致程序出错，请手动允许该权限！")
                btnPermission.visibility = View.VISIBLE
            } else {
                // 用户同意了读写SD数据权限
                init()
                btnPermission.visibility = View.GONE
            }
        }
    }

    /**
     * 若已准备就绪则跳转至主页
     */
    private fun skip() {
        if (!skipFlag && readyFlag) {
            startMainActivity()
            skipFlag = true
        }
    }

    /**
     * 设置状态已准备就绪，显示跳过按钮
     */
    private fun ready() {
        readyFlag = true
        btnSkip.visibility = View.VISIBLE
    }
}