package com.hashmap.haf.entities;

import com.hashmap.haf.models.ConfigurationType;
import com.hashmap.haf.models.LivyFunctionType;
import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "LIVY_FUNCTIONS")
public class LivyFunctionTypeEntity {

    @Column(name = "mainClass")
    private String mainClass;

    @Column(name = "jar")
    private String jar;

    @Id
    @Column(name = "functionClass")
    private String functionClass;

    @ElementCollection
    @MapKeyColumn(name="KEY")
    @Column(name="VALUE")
    @CollectionTable(name="LIVY_FUNCTION_CONFIGURATIONS")
    private Map<String, String> configurations = new HashMap<>();

    public LivyFunctionTypeEntity(LivyFunctionType livyFunctionType) {
        this.mainClass = livyFunctionType.getMainClass();
        this.jar = livyFunctionType.getJar();
        this.functionClass = livyFunctionType.getFunctionClass();
        Map<String, String> configs = new HashMap<>();
        for(ConfigurationType config: livyFunctionType.getConfigs()) {
            configs.put(config.getKey(), config.getStringValue());
        }
        this.configurations = configs;
    }

    public LivyFunctionType toData() {
        List<ConfigurationType> configs = new ArrayList<>();
        if(configurations != null && !configurations.isEmpty()) {
            for (Map.Entry<String, String> entry : configurations.entrySet()) {
                configs.add(new ConfigurationType(entry.getKey(), entry.getValue()));
            }
        }
        ConfigurationType[] configurationTypes = configs.toArray(new ConfigurationType[configs.size()]);
        return new LivyFunctionType(mainClass, jar, functionClass, configurationTypes);
    }
}
