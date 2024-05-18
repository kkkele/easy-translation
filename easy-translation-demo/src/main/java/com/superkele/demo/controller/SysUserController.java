package com.superkele.demo.controller;


import com.superkele.demo.domain.R;
import com.superkele.demo.domain.vo.TestVo;
import com.superkele.translation.annotation.TranslationExecute;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sysUser")
@RequiredArgsConstructor
public class SysUserController {

    @GetMapping("/{id}")
    @TranslationExecute(field = "data")
    public R<TestVo> getById(@PathVariable Integer id){
        TestVo testVo = new TestVo();
        testVo.setId(id);
        return R.ok(testVo);
    }
}
