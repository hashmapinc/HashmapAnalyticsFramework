package com.hashmap.haf.repository;

import com.hashmap.haf.entities.IgniteFunctionTypeEntity;
import org.springframework.data.repository.CrudRepository;

public interface IgniteFunctionTypeRepository extends CrudRepository<IgniteFunctionTypeEntity, String>{

    IgniteFunctionTypeEntity findByFunctionClazz(String functionClazz);
}
