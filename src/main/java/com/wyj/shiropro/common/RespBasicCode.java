package com.wyj.shiropro.common;

/**
 * @author wangyajing
 * @date 2019-08-08
 */
public enum RespBasicCode {
    SUCCESS("200","成功"),
    ERROR("500","异常错误"),
    LOGIN_FAIL("403","用户名或密码错误"),
    BUSINESS_EXCEPTION("415","业务异常"),

    //权限验证返回状态
    ACCOUNT_NOT_LOGIN("420","未登录"),
    ACCOUNT_NOT_AUTH("421","未授权");

    /**
     * 返回的状态码
     */
    private String code;
    /**
     * 返回的状态码描述
     */
    private String desc;

    RespBasicCode(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 通过code 获取异常对象
     *
     * @param code
     * @return
     */
//    public static RespBasicCode getByCode(String code) {
//        if (code == null || "".equals(code)) {
//            return null;
//        }
//        for (RespBasicCode respBasicCode : RespBasicCode.values()) {
//            if (respBasicCode.getCode().equals(code)) {
//                return respBasicCode;
//            }
//        }
//        return RespBasicCode.SUCCESS;
//    }

}
