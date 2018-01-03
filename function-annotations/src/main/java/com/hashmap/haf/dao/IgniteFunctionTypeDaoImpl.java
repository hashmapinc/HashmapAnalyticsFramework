package com.hashmap.haf.dao;

import com.hashmap.haf.entities.IgniteFunctionTypeEntity;
import com.hashmap.haf.models.IgniteFunctionType;
import com.hashmap.haf.repository.IgniteFunctionTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

@Component
public class IgniteFunctionTypeDaoImpl implements IgniteFunctionTypeDao{

    @Autowired
    private IgniteFunctionTypeRepository igniteFunctionTypeRepository;

    @Override
    public IgniteFunctionType save(IgniteFunctionType igniteFunctionType) {
        IgniteFunctionTypeEntity igniteFunctionTypeEntity = new IgniteFunctionTypeEntity(igniteFunctionType);
        return igniteFunctionTypeRepository.save(igniteFunctionTypeEntity).toData();
    }

    @Override
    public IgniteFunctionType findByClazz(String functionClazz) {
        IgniteFunctionTypeEntity function = igniteFunctionTypeRepository.findByFunctionClazz(functionClazz);
        if(function != null){
            return function.toData();
        }
        return null;
    }

    @Override
    public IgniteFunctionType findByClazzAndPackage(String functionClazz, String packageName) {
        IgniteFunctionTypeEntity function = igniteFunctionTypeRepository.findByFunctionClazzAndPackageName(functionClazz, packageName);
        if(function != null){
            return function.toData();
        }
        return null;
    }

    @Override
    public List<IgniteFunctionType> findAll() {
        Iterable<IgniteFunctionTypeEntity> all = igniteFunctionTypeRepository.findAll();
        List<IgniteFunctionType> list = Collections.emptyList();
        if (all != null && !all.iterator().hasNext()) {
            Iterator<IgniteFunctionTypeEntity> entities = all.iterator();
            list = new ArrayList<>();
            while (entities.hasNext()) {
                IgniteFunctionTypeEntity entity = entities.next();
                if (entity != null) {
                    list.add(entity.toData());
                }
            }
        }
        return list;
    }
}
