package com.superkele.demo.service;

import com.superkele.demo.domain.entity.Product;
import com.superkele.demo.domain.vo.ProductVO;

public interface ProductService {

    Product getById(Integer id);

    ProductVO getDetailById(Integer id);
}
