package com.superkele.demo.service.impl;


import cn.hutool.core.map.MapUtil;
import com.superkele.translation.boot.annotation.Translator;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DeptService {


    private static final Map<Integer, Integer> map;
    private static final Map<Integer, String> deptNameMap;

    static {
        map = MapUtil.newHashMap();
        map.put(1, 1);
        map.put(2, 2);
        deptNameMap = MapUtil.newHashMap();
        deptNameMap.put(1, "研发部门");
        deptNameMap.put(2, "测试部门");
    }

    /**
     * 模仿关联表获取Id
     *
     * @param id
     * @return
     */
    @Translator("getDeptId")
    public Integer getDeptIdById(Integer id) {
        return map.get(id);
    }

    @Translator("getDeptName")
    public String getDeptNameById(Integer id) {
        return deptNameMap.get(id);
    }
}
