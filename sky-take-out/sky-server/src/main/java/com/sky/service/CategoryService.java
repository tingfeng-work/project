package com.sky.service;

import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;

import java.util.List;

public interface CategoryService {
    void save(CategoryDTO categoryDTO);

    PageResult pageQuery(CategoryPageQueryDTO pageQueryDTO);

    void delete(Long id);

    void edit(CategoryDTO categoryDTO);

    void editStatus(Integer status, Long id);

    List<Category> getByType(Integer type);
}
