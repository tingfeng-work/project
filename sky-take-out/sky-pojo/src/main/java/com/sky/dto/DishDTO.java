package com.sky.dto;

import com.sky.entity.DishFlavor;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@ApiModel(description = "新增/修改菜品传递的参数")
public class DishDTO implements Serializable {

    private Long id;
    //菜品名称
    @ApiModelProperty(value = "菜品名称",required = true)
    private String name;
    //菜品分类id
    @ApiModelProperty(value = "菜品分类id",required = true)
    private Long categoryId;
    //菜品价格
    @ApiModelProperty(value = "菜品价格",required = true)
    private BigDecimal price;
    //图片
    @ApiModelProperty(value = "菜品图片路径",required = true)
    private String image;
    //描述信息
    @ApiModelProperty(value = "菜品描述",required = false)
    private String description;
    //0 停售 1 起售
    @ApiModelProperty(value = "菜品状态",required = false)
    private Integer status;
    //口味
    @ApiModelProperty(value = "菜品口味",required = false)
    private List<DishFlavor> flavors = new ArrayList<>();

}
