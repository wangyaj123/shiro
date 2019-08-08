package com.wyj.shiropro.pojo.request;

/**
 * @author wangyajing
 */
public class UserRequest {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return "UserRequest{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
