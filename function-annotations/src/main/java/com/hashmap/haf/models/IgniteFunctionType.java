package com.hashmap.haf.models;

import java.util.Arrays;

public class IgniteFunctionType {

    private String service;
    private ConfigurationType[] configs;
    private String functionClazz;

    public IgniteFunctionType(String service, ConfigurationType[] configs, String functionClazz){
        this.service = service;
        this.configs = configs;
        this.functionClazz = functionClazz;
    }

    public ConfigurationType[] getConfigs() {
        return configs;
    }

    public String getService() {
        return service;
    }

    public String getFunctionClazz() {
        return functionClazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IgniteFunctionType)) return false;

        IgniteFunctionType that = (IgniteFunctionType) o;

        if (!getService().equals(that.getService())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getConfigs(), that.getConfigs())) return false;
        return getFunctionClazz().equals(that.getFunctionClazz());
    }

    @Override
    public int hashCode() {
        int result = getService().hashCode();
        result = 31 * result + Arrays.hashCode(getConfigs());
        result = 31 * result + getFunctionClazz().hashCode();
        return result;
    }
}
