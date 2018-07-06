package com.hashmap.haf.metadata.config.entity.data.resource;


import com.hashmap.haf.metadata.config.entity.BaseSqlEntity;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class DataResourceEntity<D> extends BaseSqlEntity<D> {
}
