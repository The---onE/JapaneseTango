package com.xmx.tango.module.net;

import com.alibaba.fastjson.JSON;

/**
 * 用于解析或生成返回结果的模版
 */
public abstract class Result {

    /**
     * 设置状态码
     *
     * @param status 状态码
     * @return 当前对象用于链式调用
     */
    public abstract Result setStatus(int status);

    /**
     * 设置提示信息
     *
     * @param prompt 提示信息
     * @return 当前对象用于链式调用
     */
    public abstract Result setPrompt(String prompt);

    /**
     * 将自身转化为JSON对象字符串
     *
     * @return 转化后的JSON字符串
     */
    public String toJson() {
        return JSON.toJSONString(this);
    }
}
