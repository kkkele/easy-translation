package com.superkele.demo.service;

import com.superkele.demo.domain.entity.Product;
import com.superkele.demo.domain.vo.ProductVo;
import com.superkele.demo.domain.vo.ProductVoV2;

public interface ProductService {

    Product getById(Integer id);

    ProductVo getDetailById(Integer id);

    ProductVoV2 getDetailByIdV2(Integer id);
}
