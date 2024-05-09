package com.superkele.demo.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.superkele.demo.domain.entity.Product;
import com.superkele.demo.domain.vo.ProductVO;
import com.superkele.demo.service.ProductService;
import org.springframework.stereotype.Service;


@Service
public class ProductServiceImpl implements ProductService {
    @Override
    public Product getById(Integer id) {
        Product product = new Product();
        product.setProductId(id);
        product.setProductName("productName:" + id);
        product.setCatId(RandomUtil.randomInt());
        product.setCreateBy(RandomUtil.randomInt());
        return product;
    }

    @Override
    public ProductVO getDetailById(Integer id) {
        return null;
    }
}
