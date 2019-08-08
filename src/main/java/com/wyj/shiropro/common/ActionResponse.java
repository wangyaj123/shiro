package com.wyj.shiropro.common;

import com.github.pagehelper.PageInfo;

import java.util.*;

/**
 * @author wangyajing
 * @date 2019-08-08
 */
public class ActionResponse<T> {
/**
 * {
 * 	"head": {
 * 		"code": "200",
 * 		"result": "操作成功"
 *        },
 * 	"body": "操作成功"
 * }
 */
    /**
     * 返回的头部信息
     */
    private Head head = new Head();
    /**
     * 返回的主体信息
     */
    private Body body = new Body();

    public ActionResponse(RespBasicCode respBasicCode, T data) {
        super();
        this.head.setCode(respBasicCode.getCode());
        this.head.setResult(respBasicCode.getDesc());
        this.body.setData(data);
    }

    public ActionResponse(RespBasicCode respBasicCode) {
        super();
        this.head.setCode(respBasicCode.getCode());
        this.head.setResult(respBasicCode.getDesc());
    }

    public ActionResponse(String code, String msg, T result) {
        this.head.code = code;
        this.head.result = msg;
        this.body.data = result;
    }

    /**
     * 返回成功
     *
     * @param data 获取的数据
     */
    public static ActionResponse success(Object data) {
        return new ActionResponse(RespBasicCode.SUCCESS, data);
    }
    /**
     * 普通成功返回
     *
     */
    public static ActionResponse success() {
        return new ActionResponse(RespBasicCode.SUCCESS);
    }
    /**
     * 普通失败提示信息
     */
    public static ActionResponse failed() {
        return new ActionResponse(RespBasicCode.BUSINESS_EXCEPTION);
    }
    /**
     * 其他错误提示信息
     */
    public static ActionResponse failed(RespBasicCode respBasicCode) {
        return new ActionResponse(respBasicCode);
    }

    /**
     * 返回分页成功数据
     */
    public ActionResponse pageSuccess(List data) {
        PageInfo pageInfo = new PageInfo(data);
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("pageSize", pageInfo.getPageSize());
        result.put("totalPage", pageInfo.getPages());
        result.put("total", pageInfo.getTotal());
        result.put("pageNum", pageInfo.getPageNum());
        result.put("list", pageInfo.getList());
        return this;
    }
    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    /**
     * 返回的头部信息
     */
    private class Head{
        private String code;
        private String result;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getResult() {
            return result;
        }

        public void setResult(String result) {
            this.result = result;
        }
    }
    /**
     * 返回的主体信息
     */
    private class Body {
        private T data;
        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }
}

