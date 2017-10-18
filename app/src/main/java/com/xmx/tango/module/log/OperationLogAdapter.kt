package com.xmx.tango.module.log

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.xmx.tango.R
import com.xmx.tango.common.data.BaseEntityAdapter
import com.xmx.tango.common.log.OperationLog

import java.text.SimpleDateFormat

/**
 * Created by The_onE on 2016/3/27.
 * 操作日志列表适配器
 */
class OperationLogAdapter(context: Context, data: List<OperationLog>) : BaseEntityAdapter<OperationLog>(context, data) {

    internal class ViewHolder {
        var operation: TextView? = null
        var time: TextView? = null
    }

    @SuppressLint("SimpleDateFormat")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val cv: View
        val holder: ViewHolder
        if (convertView == null) {
            // 生成新View
            cv = LayoutInflater.from(mContext).inflate(R.layout.item_operation_log, parent, false)
            // 生成ViewHolder
            holder = ViewHolder()
            holder.operation = cv.findViewById(R.id.item_operation)
            holder.time = cv.findViewById(R.id.itemTime)
            cv.tag = holder
        } else {
            cv = convertView
            holder = cv.tag as ViewHolder
        }
        // 处理数据
        if (position < mData.size) {
            val log = mData[position]
            // 日志内容
            holder.operation?.text = log.mOperation
            // 日志时间
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val timeString = df.format(log.mTime)
            holder.time?.text = timeString
        } else {
            holder.operation?.text = "加载失败"
        }

        return cv
    }
}