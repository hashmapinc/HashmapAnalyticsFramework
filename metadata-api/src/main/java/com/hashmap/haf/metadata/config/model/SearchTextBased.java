package com.hashmap.haf.metadata.config.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class SearchTextBased<I extends UUIDBased> extends BaseData<I> {

    private static final long serialVersionUID = 1405659605002168221L;

    public SearchTextBased() {
        super();
    }

    public SearchTextBased(I id) {
        super(id);
    }
    
    public SearchTextBased(SearchTextBased<I> searchTextBased) {
        super(searchTextBased);
    }
    
    @JsonIgnore
    public abstract String getSearchText();
}