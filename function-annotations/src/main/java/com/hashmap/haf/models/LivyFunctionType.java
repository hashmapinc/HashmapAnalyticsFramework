package com.hashmap.haf.models;

import java.io.Serializable;
import java.util.Arrays;

public class LivyFunctionType implements Serializable{
    private static final long serialVersionUID = 1275247009061242559L;
    private String mainClass;
    private String jar;
    private String functionClass;
    private ConfigurationType[] configs;

    public LivyFunctionType(String mainClass, String jar, String functionClass, ConfigurationType[] configs) {
        this.mainClass = mainClass;
        this.jar = jar;
        this.functionClass = functionClass;
        this.configs = configs;
    }

    public LivyFunctionType() {
    }

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getJar() {
        return jar;
    }

    public void setJar(String jar) {
        this.jar = jar;
    }

    public ConfigurationType[] getConfigs() {
        return configs;
    }

    public void setConfigs(ConfigurationType[] configs) {
        this.configs = configs;
    }

    public String getFunctionClass() {
        return functionClass;
    }

    public void setFunctionClass(String functionClass) {
        this.functionClass = functionClass;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LivyFunctionType)) return false;

        LivyFunctionType that = (LivyFunctionType) o;

        if (!getMainClass().equals(that.getMainClass())) return false;
        if (!getJar().equals(that.getJar())) return false;
        if (!getFunctionClass().equals(that.getFunctionClass())) return false;
        // Probably incorrect - comparing Object[] arrays with Arrays.equals
        return Arrays.equals(getConfigs(), that.getConfigs());
    }

    @Override
    public int hashCode() {
        int result = getMainClass().hashCode();
        result = 31 * result + getJar().hashCode();
        result = 31 * result + getFunctionClass().hashCode();
        return result;
    }
}
