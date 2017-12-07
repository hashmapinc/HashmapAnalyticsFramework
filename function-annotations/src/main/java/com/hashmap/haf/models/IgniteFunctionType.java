package com.hashmap.haf.models;

import java.util.Arrays;

public class IgniteFunctionType {

    private String service;
    private ConfigurationType[] configs;

    public IgniteFunctionType(String service, ConfigurationType[] configs){
        this.service = service;
        this.configs = configs;
    }

    public ConfigurationType[] getConfigs() {
        return configs;
    }

    public String getService() {
        return service;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IgniteFunctionType)) return false;

        IgniteFunctionType that = (IgniteFunctionType) o;

        if (!getService().equals(that.getService())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getConfigs(), that.getConfigs());
    }

    @Override
    public int hashCode() {
        int result = getService().hashCode();
        result = 31 * result + Arrays.hashCode(getConfigs());
        return result;
    }
}
