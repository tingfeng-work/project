package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public void save(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dish.setStatus(StatusConstant.DISABLE);

        dishMapper.insert(dish);

        Long id = dish.getId();


        List<DishFlavor> dishFlavors = dishDTO.getFlavors();

        if (dishFlavors != null && !dishFlavors.isEmpty()) {
            dishFlavors.forEach(dishFlavor -> dishFlavor.setDishId(id));
            dishFlavorMapper.insertBatch(dishFlavors);
        }


    }

    @Override
    public PageResult page(DishPageQueryDTO dto) {
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        Page<DishVO> page = dishMapper.page(dto);

        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    @Transactional
    public void delete(List<Long> ids) {
        /*
        - 可以一次删除一个菜品，也可以批量删除菜品
        - 起售中的菜品不能删除
        - 被套餐关联的菜品不能删除
        - 删除菜品后，关联的口味数据也需要删除掉
        * */
        for (Long id : ids) {
            // 判断菜品状态
            Dish dish = dishMapper.selectById(id);
            if (dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }

            List<Long> setmealIds = setmealMapper.selectByDishId(id);
            if (setmealIds != null && !setmealIds.isEmpty()) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
            }
        }
        // 批量删除
        for (Long id : ids) {
            dishMapper.deleteById(id);
            dishFlavorMapper.deleteByDish(id);
        }

    }

    @Override
    public DishVO getById(Long id) {
        DishVO dishVO = new DishVO();
        Dish dish = dishMapper.selectById(id);
        BeanUtils.copyProperties(dish, dishVO);

        List<DishFlavor> list = dishFlavorMapper.selectByDishId(id);
        dishVO.setFlavors(list);
        return dishVO;
    }

    @Override
    @Transactional
    public void modifyDish(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        dishFlavorMapper.deleteByDish(dish.getId());
        //重新插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }


}
