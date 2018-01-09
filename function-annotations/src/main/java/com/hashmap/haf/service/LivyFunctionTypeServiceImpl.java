package com.hashmap.haf.service;

import com.hashmap.haf.dao.LivyFunctionTypeDao;
import com.hashmap.haf.models.LivyFunctionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LivyFunctionTypeServiceImpl implements LivyFunctionTypeService{

    @Autowired
    LivyFunctionTypeDao dao;

    @Override
    public LivyFunctionType save(LivyFunctionType livyFunctionType) {
        return dao.save(livyFunctionType);
    }

    @Override
    public LivyFunctionType findByClass(String functionClazz) {
        return dao.findByClass(functionClazz);
    }
}
