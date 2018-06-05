package com.hashmap.haf.metadata.config.entity;

import com.hashmap.haf.metadata.core.common.entity.BaseSqlEntity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DataResourceEntity<D> extends BaseSqlEntity<D> {
}