package com.xmx.tango.core.activity

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem

import com.xmx.tango.base.activity.BaseActivity
import com.xmx.tango.core.fragments.HomeFragment
import com.xmx.tango.core.fragments.SentenceFragment
import com.xmx.tango.core.fragments.TangoListFragment
import com.xmx.tango.module.calendar.CalendarActivity
import com.xmx.tango.module.log.OperationLogActivity
import com.xmx.tango.R
import com.xmx.tango.module.mission.MissionActivity
import com.xmx.tango.core.HomePagerAdapter
import com.xmx.tango.core.CoreConstants
import com.xmx.tango.core.MyApplication
import com.xmx.tango.module.crud.ChooseTangoEvent
import com.xmx.tango.module.service.TangoService
import com.xmx.tango.module.test.TestActivity
import com.xmx.tango.module.test.TypewriterActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.tool_bar.*

import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

import java.util.ArrayList

/**
 * Created by The_onE on 2017/10/16.
 * 主Activity，通过ViewPager中的Fragment展示内容，设置侧边栏
 */
class MainActivity : BaseActivity() {
    private var mExitTime: Long = 0 // 第一次尝试退出操作的时间

    override fun initView(savedInstanceState: Bundle?) {
        setContentView(R.layout.activity_main)
        // 初始化侧边栏
        initDrawerNavigation()
        // 设置Fragment
        val fragments = ArrayList<Fragment>()
        fragments.add(HomeFragment())
        fragments.add(TangoListFragment())
        fragments.add(SentenceFragment())
        // 设置Fragment对应的标题
        val titles = ArrayList<String>()
        titles.add("首页")
        titles.add("单词")
        titles.add("句子")
        // 设置适配器
        viewPager.adapter = HomePagerAdapter(supportFragmentManager, fragments, titles)
        // 设置标签页底部选项卡
        tabLayout.setupWithViewPager(viewPager)

        EventBus.getDefault().register(this)
    }

    override fun setListener() {

    }

    override fun processLogic(savedInstanceState: Bundle?) {}

    override fun onBackPressed() {
        // 点击返回键
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            // 若侧边栏打开则关闭
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // 在一定时间内连续点击两次则退出程序
            if (System.currentTimeMillis() - mExitTime > CoreConstants.LONGEST_EXIT_TIME) {
                showToast(R.string.confirm_exit)
                mExitTime = System.currentTimeMillis()
            } else {
                super.onBackPressed()
            }
        }
    }

    /**
     * 初始化侧边栏
     */
    private fun initDrawerNavigation() {
        // 设置工具栏
        setSupportActionBar(toolbar)
        // 监听侧边栏打开状态
        val toggle = ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        // 设置侧边栏选项点击监听
        navView.setNavigationItemSelectedListener { item ->
            // 根据选取的选项进行操作
            when (item.itemId) {
            // 切换到首页
                R.id.nav_home -> viewPager.currentItem = 0
            // 切换到列表页
                R.id.nav_tango_list -> viewPager.currentItem = 1
            // 切换到句子页
                R.id.nav_sentence -> viewPager.currentItem = 2
            // 打开设置Activity
                R.id.nav_setting -> startActivity(SettingActivity::class.java)
            // 打开日志Activity
                R.id.nav_log -> startActivity(OperationLogActivity::class.java)
            }
            // 操作后关闭侧边栏
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // 设置主菜单
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            // 点击主菜单选项时的操作
            when (item.itemId) {
            // 打开设置Activity
                R.id.action_settings -> {
                    startActivity(SettingActivity::class.java)
                    true
                }
            // 打开或关闭背词Service
                R.id.action_switch_service -> {
                    switchService()
                    true
                }
            // 打开写字板Activity
                R.id.action_typewriter -> {
                    startActivity(TypewriterActivity::class.java)
                    true
                }
            // 打开任务模式Activity
                R.id.action_mission -> {
                    startActivity(MissionActivity::class.java)
                    true
                }
            // 打开测试模式Activity
                R.id.action_test -> {
                    startActivity(TestActivity::class.java)
                    true
                }
            // 打开打卡签到Activity
                R.id.action_check_in -> {
                    startActivity(CalendarActivity::class.java)
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    /**
     * 打开/关闭背词服务
     */
    private fun switchService() {
        var flag = false
        // 获取所有运行中的服务
        val services = MyApplication.getInstance().serviceList
        // 结束所有背词服务
        services.filter { it is TangoService }.forEach {
            it.stopSelf()
            flag = true
        }
        // 若服务未运行，则打开服务
        if (!flag) {
            startService(Intent(this, TangoService::class.java))
            showToast("已开启服务")
        } else {
            showToast("已关闭服务")
        }
    }

    /**
     * 当在首页中选取单词后，切换到列表页
     */
    @Subscribe
    fun onEvent(event: ChooseTangoEvent) {
        viewPager.currentItem = 1
    }
}
