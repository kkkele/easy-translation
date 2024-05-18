package com.superkele.demo.controller;


import com.superkele.demo.domain.R;
import com.superkele.demo.domain.vo.AppUserVo;
import com.superkele.demo.domain.vo.TestVo;
import com.superkele.demo.domain.vo.UserVO;
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


}
