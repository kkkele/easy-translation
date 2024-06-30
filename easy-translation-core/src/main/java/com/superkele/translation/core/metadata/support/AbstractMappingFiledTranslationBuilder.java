package com.superkele.translation.core.metadata.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.constant.TranslateTiming;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 基于@Mapping注解的字段翻译构建器
 */
public abstract class AbstractMappingFiledTranslationBuilder implements FieldTranslationBuilder {

    @Override
    public FieldTranslation build(Class<?> clazz, boolean isJsonSerialize) {
        Field[] fields = ReflectUtils.getFields(clazz);
        List<Pair<Field, Mapping>> mappingFields = Arrays.stream(fields)
                .map(field -> Pair.of(field, AnnotatedElementUtils.getMergedAnnotation(field, Mapping.class)))
                .filter(pair -> ObjectUtil.isNotNull(pair.getValue()))
                .filter(pair -> pair.getValue().timing() == (isJsonSerialize ? TranslateTiming.JSON_SERIALIZE : TranslateTiming.AFTER_RETURN))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(mappingFields)) {
            return computeFieldTranslation(mappingFields);
        }
        return null;
    }


    protected FieldTranslation computeFieldTranslation(List<Pair<Field, Mapping>> mappingFields) {
        //fieldName event map
        Map<String, FieldTranslationEvent> fieldNameEventMap = new HashMap<>();
        //记录了不同的 eventMask 可以触发的事件
        Map<Short, List<FieldTranslationEvent>> eventMaskAfterMap = new HashMap<>();
        List<FieldTranslationEvent> sortEvents = new ArrayList<>();
        //先将所有的mapping和field改造成FieldTranslationEvent对象
        //1.简单排序
        mappingFields.sort((o1, o2) -> {
            Mapping v1 = o1.getValue();
            Mapping v2 = o2.getValue();
            if (v1.sort() != v2.sort()) {
                return v1.sort() - v2.sort();
            } else {
                return v1.async() ? -1 : 1;
            }
        });
        short initEvent = 1;
        short leftShift = 0;
        //第一次遍历，转化为FieldTranslationEvent对象
        //并放入map收集基本情况
        //查看是否需要开启缓存功能
        for (Pair<Field, Mapping> pair : mappingFields) {
            short eventValue = (short) (initEvent << leftShift++);
            Field field = pair.getKey();
            Mapping mapping = pair.getValue();
            FieldTranslationEvent event = new FieldTranslationEvent();
            event.setEventValue(eventValue);
            setPropertyName(event,field,mapping);
            setAsync(event,field,mapping);
            setTranslator(event,field,mapping);
            setNotNullMapping(event,field,mapping);
            setOthers(event,field,mapping);
            setNullPointerExceptionHandler(event,field,mapping);
            setMappingStrategy(event,field,mapping);
            setReceive(event,field,mapping);
            setRefTranslation(event,field,mapping);
            setGroupKey(event, field, mapping);
            setMapperDesc(event, field, mapping);
            setResultHandler(event,field,mapping);
            processAfterBuild(field, event);
            fieldNameEventMap.put(field.getName(), event);
        }
        //第二次遍历，划分sort事件和after事件(触发的时机不同，sort事件是按顺序直接触发（一定会），after事件是回调触发（存在不执行的可能）)
        for (Pair<Field, Mapping> pair : mappingFields) {
            String fieldName = pair.getKey().getName();
            Mapping mapping = pair.getValue();
            FieldTranslationEvent event = fieldNameEventMap.get(fieldName);
            if (mapping.after().length == 0) {
                event.setTriggerMask((short) 0);
                sortEvents.add(event);
            } else {
                short eventMask = 0;
                short[] preEvents = new short[mapping.after().length];
                int count = 0;
                for (int i = 0; i < mapping.after().length; i++) {
                    String afterFieldName = mapping.after()[i];
                    //获取前置事件
                    FieldTranslationEvent preEvent = fieldNameEventMap.get(afterFieldName);
                    Assert.notNull(preEvent, "找不到名为 [" + afterFieldName + "]的前置事件，after字段必须为加了@Mapping注解(或其对应的组合注解)的字段,如果有多个参数，请使用数组传参\n" +
                            "If a precedent named [" + afterFieldName + "] could not be found, the after field must be a field with a @Mapping annotation (or its corresponding combination annotation), if there are multiple parameters, use an array of parameters");
                    if (preEvent.isAsync()) {
                        preEvents[count++] = preEvent.getEventValue();
                    }
                    eventMask |= preEvent.getEventValue();
                }
                List<FieldTranslationEvent> afterEvents = eventMaskAfterMap.computeIfAbsent(eventMask, key -> new ArrayList<>());
                //设置前置事件掩码
                event.setTriggerMask(eventMask);
                afterEvents.add(event);
            }
        }
        //第三次遍历，给每个事件增加 after 事件
        for (Pair<Field, Mapping> pair : mappingFields) {
            FieldTranslationEvent event = fieldNameEventMap.get(pair.getKey().getName());
            List<FieldTranslationEvent> after = new ArrayList<>();
            eventMaskAfterMap.forEach((eventMask, events) -> {
                if ((eventMask.shortValue() & event.getEventValue()) == event.getEventValue()) {
                    after.addAll(events);
                }
            });
            event.setActiveEvents(after.stream().toArray(FieldTranslationEvent[]::new));
        }
        FieldTranslation res = new FieldTranslation();
        res.setName(mappingFields.get(0).getKey().getDeclaringClass().getName());
        res.setSortEvents(ArrayUtil.toArray(sortEvents, FieldTranslationEvent.class));
        res.setConsumeSize(mappingFields.size());
        return res;
    }

    protected abstract void setReceive(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setMappingStrategy(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setNullPointerExceptionHandler(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setOthers(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setNotNullMapping(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setTranslator(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setAsync(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setPropertyName(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setRefTranslation(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setResultHandler(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setMapperDesc(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void setGroupKey(FieldTranslationEvent event, Field field, Mapping mapping);

    protected abstract void processAfterBuild(Field field, FieldTranslationEvent event);


}