package com.hashmap.haf.service;


import com.hashmap.haf.models.IgniteFunctionType;

import java.util.List;

public interface IgniteFunctionTypeService {

    IgniteFunctionType save(IgniteFunctionType igniteFunctionType);

    IgniteFunctionType findByClazz(String functionClazz);

    List<IgniteFunctionType> findAll();

}
