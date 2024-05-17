package com.superkele.demo.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.ListUtil;
import com.superkele.demo.domain.R;
import com.superkele.demo.domain.entity.Product;
import com.superkele.demo.domain.vo.ProductVo;
import com.superkele.demo.domain.vo.ProductVoV2;
import com.superkele.demo.service.ProductService;
import com.superkele.translation.annotation.TranslationExecute;
import com.superkele.translation.annotation.constant.DefaultTranslationTypeHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @GetMapping("/v2/{id}")
    public R<ProductVoV2> getDetailByIdV2(@PathVariable Integer id) {
        return R.ok(productService.getDetailByIdV2(id));
    }

    @GetMapping("/list")
    @TranslationExecute(field = "data")
    public R<List<ProductVo>> getList() {
        return R.ok(mappingToList(ListUtil.of(1, 2, 3, 4, 4)));
    }

    @GetMapping("/array")
    @TranslationExecute(field = "data",listTypeHandler = DefaultTranslationTypeHandler.class)
    public R<ProductVo[]> getArray() {
        return R.ok(mappingToArray(1, 2, 3, 4, 5));
    }

    @GetMapping("/map")
    @TranslationExecute(field = "data")
    public R<Map<Integer, ProductVo>> getMap() {
        return R.ok(mappingToMap(1, 2, 3, 4, 5));
    }

    public Map<Integer, ProductVo> mappingToMap(Integer... ids) {
        return Arrays.stream(ids)
                .map(productService::getById)
                .map(product -> BeanUtil.copyProperties(product, ProductVo.class))
                .collect(Collectors.toMap(ProductVo::getProductId,x -> x));
    }

    public ProductVo[] mappingToArray(Integer... ids) {
        return Arrays.stream(ids)
                .map(productService::getById)
                .map(product -> BeanUtil.copyProperties(product, ProductVo.class))
                .toArray(ProductVo[]::new);
    }

    public List<ProductVo> mappingToList(List<Integer> ids) {
        List<Product> collect = ids.stream()
                .map(productService::getById)
                .collect(Collectors.toList());
        List<ProductVo> productVos = BeanUtil.copyToList(collect, ProductVo.class);
        return productVos;
    }
}
