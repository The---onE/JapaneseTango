package com.xmx.tango.common.data

import android.content.Context
import android.widget.BaseAdapter

/**
 * Created by The_onE on 2016/7/11.
 * 实体列表适配器基类
 */
abstract class BaseEntityAdapter<Entity>(protected var mContext: Context,
                                         protected var mData: List<Entity>) : BaseAdapter() {

    /**
     * 更新数据
     */
    fun updateList(data: List<Entity>) {
        mData = data
        notifyDataSetChanged()
    }

    override fun getCount(): Int = mData.size

    override fun getItem(i: Int): Any? {
        return if (i < mData.size) {
            mData[i]
        } else {
            null
        }
    }

    override fun getItemId(i: Int): Long = i.toLong()
}
