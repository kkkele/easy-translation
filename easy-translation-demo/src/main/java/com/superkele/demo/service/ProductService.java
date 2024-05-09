package com.superkele.demo.service;

import com.superkele.demo.domain.entity.Product;
import com.superkele.demo.domain.vo.ProductVo;

public interface ProductService {

    Product getById(Integer id);

    ProductVo getDetailById(Integer id);
}
