package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "分页查询请求参数")
public class CategoryPageQueryDTO implements Serializable {

    //页码
    @ApiModelProperty(value = "页码",required = true)
    private int page;

    //每页记录数
    @ApiModelProperty(value = "每页记录数",required = true)
    private int pageSize;

    //分类名称
    @ApiModelProperty(value = "分类名称")
    private String name;

    //分类类型 1菜品分类  2套餐分类
    @ApiModelProperty(value = "分类类型")
    private Integer type;

}
