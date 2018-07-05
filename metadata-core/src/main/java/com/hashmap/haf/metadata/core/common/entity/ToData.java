package com.hashmap.haf.metadata.core.common.entity;

/**
 * The interface To dto.
 *
 * @param <T> the type parameter
 */
public interface ToData<T> {

    /**
     * This method convert domain model object to data transfer object.
     *
     * @return the dto object
     */
    T toData();
}
