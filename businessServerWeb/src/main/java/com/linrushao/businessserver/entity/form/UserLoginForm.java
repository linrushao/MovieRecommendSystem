package com.linrushao.businessserver.entity.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author LRS
 * @Date 2022/9/21 14:25
 * Desc
 */
@Data
public class UserLoginForm {
    private String username;
    private int uid;
    private String password;

    public String getUsername() {
        return username;
    }
    public UserLoginForm(){}

    public UserLoginForm(String username, int uid, String password) {
        this.username = username;
        this.uid = uid;
        this.password = password;
    }

    public UserLoginForm(String username, String password) {
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
