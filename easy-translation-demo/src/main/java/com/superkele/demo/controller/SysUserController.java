package com.superkele.demo.controller;


import com.superkele.demo.domain.R;
import com.superkele.demo.domain.entity.Employee;
import com.superkele.demo.domain.vo.AppUserVo;
import com.superkele.demo.domain.vo.TestVo;
import com.superkele.demo.domain.vo.UserVO;
import com.superkele.translation.annotation.TranslationExecute;
import com.superkele.translation.annotation.constant.DefaultTranslationTypeHandler;
import com.superkele.translation.core.processor.support.DefaultTranslationProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sysUser")
@RequiredArgsConstructor
public class SysUserController {

    @Autowired
    private final DefaultTranslationProcessor defaultTranslationProcessor;

    @GetMapping("/{id}")
    public R<TestVo> getById(@PathVariable Integer id){
        TestVo testVo = new TestVo();
        testVo.setId(id);
        defaultTranslationProcessor.process(testVo);
        return R.ok(testVo);
    }

    @GetMapping("/test")
    @TranslationExecute(field = "data")
    public R<UserVO> test(){
        UserVO userVO = new UserVO();
        userVO.setUserId(1);
        userVO.setUserName("小红");
        return R.ok(userVO);
    }

    @GetMapping("/test2")
    @TranslationExecute(field = "data")
    public R<AppUserVo> test2(){
        AppUserVo appUserVo = new AppUserVo();
        appUserVo.setUserId(1);
        appUserVo.setSexCode(2);
        appUserVo.setStatusCode(1);
        return R.ok(appUserVo);
    }

    @GetMapping("/test3")
    @TranslationExecute(field = "data",listTypeHandler = DefaultTranslationTypeHandler.class)
    public R<Employee> test3(){
        Employee employee = new Employee();
        employee.setEmployeeId(1);
        return R.ok(employee);
    }


}
