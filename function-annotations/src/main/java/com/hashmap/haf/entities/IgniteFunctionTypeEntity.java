package com.hashmap.haf.entities;

import com.hashmap.haf.models.ConfigurationType;
import com.hashmap.haf.models.IgniteFunctionType;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "IGNITE_FUNCTIONS")
public class IgniteFunctionTypeEntity {

    @Column(name = "SERVICE")
    private String service;

    @Column(name = "functionClazz")
    private String functionClazz;

    @ElementCollection
    @MapKeyColumn(name="KEY")
    @Column(name="VALUE")
    @CollectionTable(name="IGNITE_FUNCTION_CONFIGURATIONS")
    private Map<String, String> configurations = new HashMap<>();

    public IgniteFunctionTypeEntity() {
        super();
    }

    public IgniteFunctionTypeEntity(IgniteFunctionType igniteFunctionType) {
        this.service = igniteFunctionType.getService();
        this.functionClazz = igniteFunctionType.getFunctionClazz();
        Map<String, String> configs = new HashMap<>();
        for(ConfigurationType config: igniteFunctionType.getConfigs()) {
           configs.put(config.getKey(), config.getStringValue());
        }
        this.configurations = configs;
    }

    public IgniteFunctionType toData() {
        List<ConfigurationType> configs = new ArrayList();
        for(Map.Entry<String, String> entry: configurations.entrySet()) {
            configs.add(new ConfigurationType(entry.getKey(), entry.getValue()));
        }
        return new IgniteFunctionType(service, configs.toArray(new ConfigurationType[configs.size()]), functionClazz);
    }

}
