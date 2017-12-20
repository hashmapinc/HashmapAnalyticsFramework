package com.hashmap.haf.service;

import com.hashmap.haf.dao.IgniteFunctionTypeDao;
import com.hashmap.haf.models.IgniteFunctionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class IgniteFunctionTypeServiceImpl implements IgniteFunctionTypeService{
    @Autowired
    IgniteFunctionTypeDao dao;


    @Override
    public IgniteFunctionType save(IgniteFunctionType igniteFunctionType) {
        return dao.save(igniteFunctionType);
    }

    @Override
    public IgniteFunctionType findByClazz(String functionClazz) {
        return dao.findByClazz(functionClazz);
    }

    @Override
    public List<IgniteFunctionType> findAll() {
        return dao.findAll();
    }
}
