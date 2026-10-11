package com.guyi.service;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guyi.entity.Category;
import com.guyi.mapper.CategoryMapper;
import com.guyi.vo.CategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryMapper categoryMapper;

    /** 查询启用中的分类树（本期两级，结构上兼容多级）。 */
    public List<CategoryVO> tree() {
        List<Category> categories = categoryMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getStatus, 1)
                .orderByAsc(Category::getSort));

        Map<Integer, List<Category>> childrenMap = categories.stream()
                .filter(category -> category.getParentId() != null && category.getParentId() != 0)
                .collect(Collectors.groupingBy(Category::getParentId));

        return categories.stream()
                .filter(category -> category.getParentId() != null && category.getParentId() == 0)
                .map(category -> toVO(category, childrenMap))
                .toList();
    }

    private CategoryVO toVO(Category category, Map<Integer, List<Category>> childrenMap) {
        CategoryVO vo = BeanUtil.copyProperties(category, CategoryVO.class);
        vo.setChildren(childrenMap.getOrDefault(category.getId(), List.of()).stream()
                .map(child -> BeanUtil.copyProperties(child, CategoryVO.class))
                .toList());
        return vo;
    }
}
