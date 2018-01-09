package com.hashmap.haf.service;

import com.hashmap.haf.models.LivyFunctionType;

public interface LivyFunctionTypeService {

    LivyFunctionType save(LivyFunctionType livyFunctionType);

    LivyFunctionType findByClass(String functionClazz);
}
