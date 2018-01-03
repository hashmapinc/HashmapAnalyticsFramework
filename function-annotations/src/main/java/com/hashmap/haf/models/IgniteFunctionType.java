package com.hashmap.haf.models;

import java.io.Serializable;
import java.util.Arrays;

public class IgniteFunctionType implements Serializable{

    private static final long serialVersionUID = -4173993086008058657L;
    private String service;
    private ConfigurationType[] configs;
    private String functionClazz;
    private String packageName;

    public IgniteFunctionType(String service, ConfigurationType[] configs,
                              String functionClazz, String packageName){
        this.service = service;
        this.configs = configs;
        this.functionClazz = functionClazz;
        this.packageName = packageName;
    }

    public IgniteFunctionType(){

    }

    public void setService(String service) {
        this.service = service;
    }

    public void setConfigs(ConfigurationType[] configs) {
        this.configs = configs;
    }

    public void setFunctionClazz(String functionClazz) {
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IgniteFunctionType)) return false;

        IgniteFunctionType that = (IgniteFunctionType) o;

        if (getService() != null ? !getService().equals(that.getService()) : that.getService() != null) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        if (!Arrays.equals(getConfigs(), that.getConfigs())) return false;
        if (getFunctionClazz() != null ? !getFunctionClazz().equals(that.getFunctionClazz()) : that.getFunctionClazz() != null)
            return false;
        return getPackageName() != null ? getPackageName().equals(that.getPackageName()) : that.getPackageName() == null;
    }

    @Override
    public int hashCode() {
        int result = getService() != null ? getService().hashCode() : 0;
        result = 31 * result + Arrays.hashCode(getConfigs());
        result = 31 * result + (getFunctionClazz() != null ? getFunctionClazz().hashCode() : 0);
        result = 31 * result + (getPackageName() != null ? getPackageName().hashCode() : 0);
        return result;
    }
}
