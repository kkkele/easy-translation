package com.superkele.translation.core.scaner;

import com.superkele.translation.core.metadata.FieldInfo;

import java.util.List;

public interface MappingFieldScanner {

    List<FieldInfo> scan(String... basePackages);
}
