package com.superkele.translation.core.metadata.support;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.superkele.translation.annotation.Mapping;
import com.superkele.translation.annotation.NullPointerExceptionHandler;
import com.superkele.translation.annotation.RefTranslation;
import com.superkele.translation.annotation.bean.FieldTransInfo;
import com.superkele.translation.annotation.constant.TranslateTiming;
import com.superkele.translation.core.metadata.FieldTranslation;
import com.superkele.translation.core.metadata.FieldTranslationBuilder;
import com.superkele.translation.core.metadata.FieldTranslationEvent;
import com.superkele.translation.core.util.Assert;
import com.superkele.translation.core.util.Pair;
import com.superkele.translation.core.util.ReflectUtils;
import com.superkele.translation.core.util.Singleton;
import lombok.Data;
import org.springframework.core.annotation.AnnotatedElementUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class MappingFiledTranslationBuilder implements FieldTranslationBuilder {

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
        mappingFields.sort(Comparator.comparingInt(o -> o.getValue().sort()));
        short initEvent = 1;
        short leftShift = 0;
        //第一次遍历，转化为FieldTranslationEvent对象
        //并放入map收集基本情况
        //查看是否需要开启缓存功能
        boolean cacheEnabled = false;
        Set<String> uniqueNameSet = new HashSet<>();
        for (Pair<Field, Mapping> pair : mappingFields) {
            Field field = pair.getKey();
            Mapping mapping = pair.getValue();
            FieldTranslationEvent event = new FieldTranslationEvent();
            short eventValue = (short) (initEvent << leftShift);
            RefTranslation mergedAnnotation = AnnotatedElementUtils.getMergedAnnotation(field, RefTranslation.class);
            event.setRefTranslation(mergedAnnotation);
            event.setPropertyName(field.getName());
            event.setEventValue(eventValue);
            event.setAsync(mapping.async());
            event.setTranslator(mapping.translator());
            event.setFieldTransInfo(new FieldTransInfoDTO(field.getDeclaringClass(), field.getName(), mapping.receive(), mapping.mapper(), mapping.other(), Singleton.get(mapping.nullPointerHandler())));
            event.setMappingHandler(Singleton.get(mapping.mappingHandler()));
            fieldNameEventMap.put(field.getName(), event);
            leftShift++;
            String uniqueName = StrUtil.join(",", mapping.translator(), mapping.mapper(), mapping.other());
            if (cacheEnabled || uniqueNameSet.contains(uniqueName)) {
                cacheEnabled = true;
            } else {
                uniqueNameSet.add(uniqueName);
            }
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
            event.setAfterEvents(after.stream().toArray(FieldTranslationEvent[]::new));
        }
        FieldTranslation res = new FieldTranslation();
        res.setSortEvents(ArrayUtil.toArray(sortEvents, FieldTranslationEvent.class));
        res.setConsumeSize(mappingFields.size());
        res.setHasSameInvoker(cacheEnabled);
        return res;
    }


    @Data
    public static class FieldTransInfoDTO implements FieldTransInfo {

        private Class<?> declaringClass;

        private String propertyName;

        private String receive;

        private String[] mapper;

        private String[] others;

        private NullPointerExceptionHandler nullPointerExceptionHandler;

        public FieldTransInfoDTO(Class<?> declaringClass, String propertyName, String receive, String[] mapper, String[] others, NullPointerExceptionHandler nullPointerExceptionHandler) {
            this.declaringClass = declaringClass;
            this.propertyName = propertyName;
            this.receive = receive;
            this.mapper = mapper;
            this.others = others;
            this.nullPointerExceptionHandler = nullPointerExceptionHandler;
        }

        @Override
        public Class<?> getSourceClass() {
            return declaringClass;
        }

        @Override
        public String getPropertyName() {
            return propertyName;
        }

        @Override
        public String receive() {
            return receive;
        }

        @Override
        public String[] mapperPropertyNames() {
            return mapper;
        }

        @Override
        public String[] others() {
            return others;
        }

        @Override
        public NullPointerExceptionHandler getNullPointerExceptionHandler() {
            return nullPointerExceptionHandler;
        }
    }

}
