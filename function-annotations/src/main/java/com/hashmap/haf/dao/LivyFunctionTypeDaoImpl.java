package com.hashmap.haf.dao;

import com.hashmap.haf.entities.LivyFunctionTypeEntity;
import com.hashmap.haf.models.LivyFunctionType;
import com.hashmap.haf.repository.LivyFunctionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LivyFunctionTypeDaoImpl implements LivyFunctionTypeDao{

    @Autowired
    private LivyFunctionTypeRepository livyFunctionTypeRepository;

    @Override
    public LivyFunctionType save(LivyFunctionType livyFunctionType) {
        LivyFunctionTypeEntity livyFunctionTypeEntity = new LivyFunctionTypeEntity(livyFunctionType);
        return livyFunctionTypeRepository.save(livyFunctionTypeEntity).toData();
    }

    @Override
    public LivyFunctionType findByClass(String functionClazz) {
        LivyFunctionTypeEntity function = livyFunctionTypeRepository.findByFunctionClass(functionClazz);
        if(function != null){
            return function.toData();
        }
        return null;
    }
}
