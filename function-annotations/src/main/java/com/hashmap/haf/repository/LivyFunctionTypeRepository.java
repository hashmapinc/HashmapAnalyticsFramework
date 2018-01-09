package com.hashmap.haf.repository;

import com.hashmap.haf.entities.LivyFunctionTypeEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LivyFunctionTypeRepository extends CrudRepository<LivyFunctionTypeEntity, String> {

    LivyFunctionTypeEntity findByFunctionClass(String functionClass);
}
