package com.quality.common.vo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqRegisterVO {
    @NotBlank(message = "用户名不能为空")
    private String username;
    @NotBlank(message = "真实姓名不能为空")
    private String name;
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String password;
    @NotBlank(message = "所属区域不能为空")
    private String region;
}
