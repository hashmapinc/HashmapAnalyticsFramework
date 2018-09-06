package com.hashmap.haf.metadata.config.page;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashmap.haf.metadata.config.model.SearchTextBased;
import com.hashmap.haf.metadata.config.model.UUIDBased;

import java.util.List;
import java.util.UUID;

public class TextPageData<T extends SearchTextBased<? extends UUIDBased>> {

    private final List<T> data;
    private final TextPageLink nextPageLink;
    private final boolean hasNext;

    public TextPageData(List<T> data, TextPageLink pageLink) {
        super();
        this.data = data;
        int limit = pageLink.getLimit();
        if (data != null && data.size() == limit) {
            int index = data.size()-1;
            UUID idOffset = data.get(index).getId().getId();
            String textOffset = data.get(index).getSearchText();
            nextPageLink = new TextPageLink(limit, pageLink.getTextSearch(), idOffset, textOffset);
            hasNext = true;
        } else {
            nextPageLink = null;
            hasNext = false;
        }
    }

    @JsonCreator
    public TextPageData(@JsonProperty("data") List<T> data,
                        @JsonProperty("nextPageLink") TextPageLink nextPageLink,
                        @JsonProperty("hasNext") boolean hasNext) {
        this.data = data;
        this.nextPageLink = nextPageLink;
        this.hasNext = hasNext;
    }

    public List<T> getData() {
        return data;
    }

    @JsonProperty("hasNext")
    public boolean hasNext() {
        return hasNext;
    }

    public TextPageLink getNextPageLink() {
        return nextPageLink;
    }

}