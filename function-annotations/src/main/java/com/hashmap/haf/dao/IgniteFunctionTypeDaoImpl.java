package com.hashmap.haf.dao;

import com.hashmap.haf.entities.IgniteFunctionTypeEntity;
import com.hashmap.haf.models.IgniteFunctionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IgniteFunctionTypeDaoImpl implements IgniteFunctionTypeDao{

    @Autowired
    private IgniteFunctionTypeRepository igniteFunctionTypeRepository;

    @Override
    public IgniteFunctionType save(IgniteFunctionType igniteFunctionType) {
        IgniteFunctionTypeEntity igniteFunctionTypeEntity = new IgniteFunctionTypeEntity(igniteFunctionType);
        return igniteFunctionTypeRepository.save(igniteFunctionTypeEntity).toData();
    }
}
