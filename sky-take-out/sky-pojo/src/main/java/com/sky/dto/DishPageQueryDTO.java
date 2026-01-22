package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "分页查询请求参数")
public class DishPageQueryDTO implements Serializable {

    @ApiModelProperty(value = "页码",required = true)
    private int page;

    @ApiModelProperty(value = "每页记录数",required = true)
    private int pageSize;

    @ApiModelProperty(value = "菜品名称",required = false)
    private String name;

    //分类id
    @ApiModelProperty(value = "分类id")
    private Integer categoryId;

    //状态 0表示禁用 1表示启用
    @ApiModelProperty(value = "分类状态")
    private Integer status;

}
