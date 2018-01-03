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

    @Id
    @Column(name = "functionClazz")
    private String functionClazz;

    @Column(name = "packageName")
    private String packageName;

    @ElementCollection
    @MapKeyColumn(name="KEY")
    @Column(name="VALUE")
    @CollectionTable(name="IGNITE_FUNCTION_CONFIGURATIONS")
    private Map<String, String> configurations = new HashMap<>();

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getFunctionClazz() {
        return functionClazz;
    }

    public void setFunctionClazz(String functionClazz) {
        this.functionClazz = functionClazz;
    }

    public Map<String, String> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Map<String, String> configurations) {
        this.configurations = configurations;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public IgniteFunctionTypeEntity() {
        super();
    }

    public IgniteFunctionTypeEntity(IgniteFunctionType igniteFunctionType) {
        this.service = igniteFunctionType.getService();
        this.functionClazz = igniteFunctionType.getFunctionClazz();
        this.packageName = igniteFunctionType.getPackageName();
        Map<String, String> configs = new HashMap<>();
        for(ConfigurationType config: igniteFunctionType.getConfigs()) {
           configs.put(config.getKey(), config.getStringValue());
        }
        this.configurations = configs;
    }

    public IgniteFunctionType toData() {
        List<ConfigurationType> configs = new ArrayList<>();
        if(configurations != null && !configurations.isEmpty()) {
            for (Map.Entry<String, String> entry : configurations.entrySet()) {
                configs.add(new ConfigurationType(entry.getKey(), entry.getValue()));
            }
        }
        ConfigurationType[] configurationTypes = configs.toArray(new ConfigurationType[configs.size()]);
        return new IgniteFunctionType(service, configurationTypes, functionClazz, getPackageName());
    }

}
