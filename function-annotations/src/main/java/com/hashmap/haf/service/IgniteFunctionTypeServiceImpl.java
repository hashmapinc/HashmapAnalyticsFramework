package com.hashmap.haf.service;

import com.hashmap.haf.dao.IgniteFunctionTypeDao;
import com.hashmap.haf.models.IgniteFunctionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class IgniteFunctionTypeServiceImpl implements IgniteFunctionTypeService{
    @Autowired
    IgniteFunctionTypeDao igniteFunctionTypeDao;


    @Override
    public IgniteFunctionType save(IgniteFunctionType igniteFunctionType) {
        return igniteFunctionTypeDao.save(igniteFunctionType);
    }
}
