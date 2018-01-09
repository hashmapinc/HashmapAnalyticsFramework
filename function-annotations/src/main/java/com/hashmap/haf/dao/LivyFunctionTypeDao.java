package com.hashmap.haf.dao;

import com.hashmap.haf.models.LivyFunctionType;

public interface LivyFunctionTypeDao {

    LivyFunctionType save(LivyFunctionType livyFunctionType);

    LivyFunctionType findByClass(String functionClazz);
}
