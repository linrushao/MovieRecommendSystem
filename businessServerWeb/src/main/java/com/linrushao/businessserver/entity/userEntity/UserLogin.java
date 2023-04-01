package com.linrushao.businessserver.entity.userEntity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author LRS
 * @Date 2022/9/21 14:25
 * Desc
 */
@Data
public class UserLogin {
    private String username;
    private int uid;
    private String password;

    public String getUsername() {
        return username;
    }
    public UserLogin(){}

    public UserLogin(String username, int uid, String password) {
        this.username = username;
        this.uid = uid;
        this.password = password;
    }

    public UserLogin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
