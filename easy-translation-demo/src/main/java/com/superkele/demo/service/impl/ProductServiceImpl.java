package com.superkele.demo.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.superkele.demo.domain.entity.Product;
import com.superkele.demo.domain.vo.ProductVo;
import com.superkele.demo.domain.vo.ProductVoV2;
import com.superkele.demo.service.ProductService;
import com.superkele.translation.annotation.TranslationExecute;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getById(Integer id) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName("productName:" + id);
        product.setCatId(RandomUtil.randomInt());
        product.setCreateBy(1);
        return product;
    }

    @Override
    public ProductVo getDetailById(Integer id) {
        ProductVo productVO = new ProductVo();
        Product byId = getById(id);
        BeanUtil.copyProperties(byId, productVO);
        return productVO;
    }

    @Override
    @TranslationExecute
    public ProductVoV2 getDetailByIdV2(Integer id) {
        Product byId = getById(id);
        ProductVoV2 productVO = new ProductVoV2();
        productVO.setProduct(byId);
        return productVO;
    }
}
