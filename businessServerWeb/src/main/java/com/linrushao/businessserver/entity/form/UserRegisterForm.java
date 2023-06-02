package com.linrushao.businessserver.entity.form;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author LRS
 * @Date 2022/9/21 9:04
 * Desc
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterForm {
    private String username;
    private String password;
}
