package com.hashmap.haf.dao;

import com.hashmap.haf.models.IgniteFunctionType;

import java.util.List;

public interface IgniteFunctionTypeDao {

    IgniteFunctionType save(IgniteFunctionType igniteFunctionType);

    IgniteFunctionType findByClazz(String functionClazz);

    IgniteFunctionType findByClazzAndPackage(String functionClazz, String packageName);

    List<IgniteFunctionType> findAll();

}

