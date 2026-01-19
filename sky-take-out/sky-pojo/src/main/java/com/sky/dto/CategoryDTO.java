package com.sky.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel("新增分类")
public class CategoryDTO implements Serializable {

    //主键
    @ApiModelProperty
    private Long id;

    //类型 1 菜品分类 2 套餐分类
    @ApiModelProperty(value = "分类类型",required = true)
    private Integer type;

    //分类名称
    @ApiModelProperty(value = "分类名称",required = true)
    private String name;

    //排序
    @ApiModelProperty(value = "排序，按照升序排序",required = true)
    private Integer sort;

}
