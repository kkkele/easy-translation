package com.superkele.translation.core.test.mapping;

import com.superkele.translation.core.metadata.ParamDesc;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;

public class DefaultConfigurableFiledTranslationFactoryTest {


    public <T> void test(List<String> param,T param2,String hello){
    }


    @Test
    public void methodTest() throws NoSuchMethodException {
        Method method = DefaultConfigurableFiledTranslationFactoryTest.class.getMethod("test", List.class, Object.class, String.class);
        ParamDesc[] paramDescs = buildParamDesc(method);
        for (ParamDesc paramDesc : paramDescs) {
            System.out.println(paramDesc);
        }
    }

    public ParamDesc[] buildParamDesc(Method method) {
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        return Arrays.stream(genericParameterTypes)
                .map(type -> {
                    ParamDesc paramDesc = new ParamDesc();
                    if (type instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) type;
                        paramDesc.setTargetClass(searchForClass(parameterizedType.getRawType().getTypeName()));
                        paramDesc.setTypes(Arrays.stream(parameterizedType.getActualTypeArguments())
                                .map(t -> searchForClass(t.getTypeName()))
                                .toArray(Class[]::new));
                    } else {
                        paramDesc.setTargetClass(searchForClass(type.getTypeName()));
                    }
                    return paramDesc;
                })
                .toArray(ParamDesc[]::new);
    }

    public Class<?> searchForClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            return Object.class;
        }
    }
}
