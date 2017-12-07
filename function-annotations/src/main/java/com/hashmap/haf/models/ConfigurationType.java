package com.hashmap.haf.models;

public class ConfigurationType {
    private final String key;
    private final String value;

    public ConfigurationType(String key, String value){
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getStringValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConfigurationType)) return false;

        ConfigurationType that = (ConfigurationType) o;

        if (!getKey().equals(that.getKey())) return false;
        return value.equals(that.value);
    }

    @Override
    public int hashCode() {
        int result = getKey().hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }
}
