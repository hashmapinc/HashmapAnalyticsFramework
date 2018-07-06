package com.hashmap.haf.metadata.config.entity;

import com.fasterxml.uuid.Generators;
import com.hashmap.haf.metadata.config.constants.ModelConstants;
import com.hashmap.haf.metadata.config.utils.UUIDConverter;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.util.UUID;

@Data
@MappedSuperclass
public abstract class BaseSqlEntity<D> implements BaseEntity<D> {

    @Id
    @Column(name = ModelConstants.ID_PROPERTY)
    protected String id;

    @Override
    public UUID getId() {
        if (id == null) {
            return Generators.timeBasedGenerator().generate();
        }
        return UUIDConverter.fromString(id);
    }

    public void setId(UUID id) {
        this.id = UUIDConverter.fromTimeUUID(id);
    }

    protected UUID toUUID(String src){
        return UUIDConverter.fromString(src);
    }

    protected String toString(UUID timeUUID){
        return UUIDConverter.fromTimeUUID(timeUUID);
    }

}
