package com.linrushao.businessserver.entity.userEntity;

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
public class UserRegister {
    private String username;
    private String password;
}
