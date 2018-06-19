package com.hashmapinc.haf.models;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ActivationType {
    EMAIL("email"), LINK("link"), NONE("none");

    private String type;

    ActivationType(String type) {
        this.type = type;
    }

    public String getType(){
        return this.type;
    }

    @JsonCreator
    public ActivationType fromValue(String type){
        for (ActivationType act : values()){
            if(act.getType().equalsIgnoreCase(type)){
                return act;
            }
        }
        return null;
    }

    @JsonValue
    public String toValue(){
        return this.getType();
    }
}
