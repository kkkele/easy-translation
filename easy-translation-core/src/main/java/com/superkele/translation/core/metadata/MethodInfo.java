package com.superkele.translation.core.metadata;


import com.superkele.translation.core.function.Translator;

import java.io.Serializable;
import java.lang.reflect.Method;


public  class MethodInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Method originMethod;

    private Object invokeObj;

    private String declaringBeanName;
    
}
