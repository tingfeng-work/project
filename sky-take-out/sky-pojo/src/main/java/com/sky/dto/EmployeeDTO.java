package com.sky.dto;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;


// 前端提交的数据和实体类中对应的属性差别较大时，使用 DTO 来封装
@Data
@ApiModel(description = "新增员工时传递的参数")
public class EmployeeDTO implements Serializable {
    @ApiModelProperty("员工id")
    private Long id;

    @ApiModelProperty(value = "用户名",required = true)
    private String username;

    @ApiModelProperty(value = "员工姓名",required = true)
    private String name;

    @ApiModelProperty(value = "员工手机号",required = true)
    private String phone;

    @ApiModelProperty(value = "员工性别",required = true)
    private String sex;

    @ApiModelProperty(value = "员工身份证号码",required = true)
    private String idNumber;

}
