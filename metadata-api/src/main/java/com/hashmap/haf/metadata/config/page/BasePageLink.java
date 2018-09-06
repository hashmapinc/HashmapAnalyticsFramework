package com.hashmap.haf.metadata.config.page;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@RequiredArgsConstructor
@AllArgsConstructor
public abstract class BasePageLink implements Serializable {

    private static final long serialVersionUID = 6970237696436130040L;

    @Getter
    protected final int limit;

    @Getter @Setter
    protected UUID idOffset;

}