package com.xmx.tango.module.net;

import java.util.List;

/**
 * 用于包含对象列表的返回结果
 *
 * @param <T> 对象类型
 */
public class ListResult<T> extends Result {
    private int status;
    private String prompt;
    private List<T> list;

    public int getStatus() {
        return status;
    }

    @Override
    public ListResult<T> setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getPrompt() {
        return prompt;
    }

    @Override
    public ListResult<T> setPrompt(String prompt) {
        this.prompt = prompt;
        return this;
    }

    public List<T> getList() {
        return list;
    }

    public ListResult<T> setList(List<T> list) {
        this.list = list;
        return this;
    }
}
