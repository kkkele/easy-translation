package com.superkele.demo.service.impl;


import com.superkele.demo.domain.entity.ProductCategory;
import com.superkele.demo.service.ProductCategoryService;
import com.superkele.translation.annotation.Translation;
import com.superkele.translation.boot.annotation.Translator;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

    @Override
    public ProductCategory getById(Integer id) {
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCatId(id);
        productCategory.setCatName("商品分类" + id);
        return productCategory;
    }

    @Translator("getCatName")
    public String getNameById(Integer id) {
        return getById(id).getCatName();
    }
}
