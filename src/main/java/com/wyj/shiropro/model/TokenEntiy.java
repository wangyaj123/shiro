package com.wyj.shiropro.model;

public class TokenEntiy {
    /**

     * 用户id

     */

    private int userId;

    /**

     * 随机生成的uuid

     */

    private String token;

    public TokenEntiy(String token, int userId) {

        this.token = token;

        this.userId=userId;

    }

    public int getUserId() {

        return userId;

    }

    public void setUserId(int userId) {

        this.userId = userId;

    }

    public String getToken() {

        return token;

    }

    public void setToken(String token) {

        this.token = token;

    }
}
