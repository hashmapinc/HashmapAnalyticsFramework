package com.hashmap.haf.repository;

import com.hashmap.haf.entities.IgniteFunctionTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IgniteFunctionTypeRepository extends CrudRepository<IgniteFunctionTypeEntity, String>{

    IgniteFunctionTypeEntity findByFunctionClazz(String functionClazz);

    IgniteFunctionTypeEntity findByFunctionClazzAndPackageName(String functionClazz, String packageName);
}
