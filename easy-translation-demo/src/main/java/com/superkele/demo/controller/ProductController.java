package com.superkele.demo.controller;


import cn.hutool.core.bean.BeanUtil;
import com.superkele.demo.domain.R;
import com.superkele.demo.domain.entity.Product;
import com.superkele.demo.domain.vo.ProductVo;
import com.superkele.demo.service.ProductService;
import com.superkele.translation.annotation.TranslationExecute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/{id}")
    @TranslationExecute(field = "data")
    public R<ProductVo> getDetailById(@PathVariable Integer id) {
        return R.ok(productService.getDetailById(id));
    }

    @GetMapping("/list")
    @TranslationExecute
    public R<List<ProductVo>> getList(List<Integer> ids) {
        List<Product> collect = ids.stream()
                .map(productService::getById)
                .collect(Collectors.toList());
        List<ProductVo> productVos = BeanUtil.copyToList(collect, ProductVo.class);
        return R.ok(productVos);
    }
}