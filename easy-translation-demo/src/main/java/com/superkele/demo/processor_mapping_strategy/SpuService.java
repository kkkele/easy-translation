package com.superkele.demo.processor_mapping_strategy;

import com.superkele.translation.annotation.Translation;
import com.superkele.translation.annotation.constant.InvokeBeanScope;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class SpuService {


    @Translation(name = "getSpuByIds", scope = InvokeBeanScope.SINGLETON)
    public List<Spu> getSpuById(List<Integer> ids) {
        return ids.stream()
                .map(id -> new Spu(id, "spuName" + id))
                .collect(Collectors.toList());
    }

    @Translation(name = "getSales", scope = InvokeBeanScope.SINGLETON)
    public Integer getSales(Integer skuId){
        return new Random().nextInt(1000);
    }
}
