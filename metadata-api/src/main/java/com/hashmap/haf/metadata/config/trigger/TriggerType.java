package com.hashmap.haf.metadata.config.trigger;

public enum TriggerType {
    CRON("CRON");

    private String triggerType;

    public String getSourceType() {
        return this.triggerType;
    }

    public void setSourceType(String triggerType) {
        this.triggerType = triggerType;
    }

    private TriggerType(String triggerType) {
        this.triggerType = triggerType;
    }
}
