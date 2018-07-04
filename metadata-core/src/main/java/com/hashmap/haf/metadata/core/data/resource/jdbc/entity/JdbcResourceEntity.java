package com.hashmap.haf.metadata.core.data.resource.jdbc.entity;

import com.datastax.driver.core.utils.UUIDs;
import com.fasterxml.uuid.Generators;
import com.hashmap.haf.metadata.core.common.constants.ModelConstants;
import com.hashmap.haf.metadata.core.data.resource.DataResourceEntity;
import com.hashmap.haf.metadata.core.data.resource.jdbc.model.JdbcResource;
import com.hashmap.haf.metadata.core.data.resource.jdbc.model.JdbcResourceId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Slf4j
@Table(name = ModelConstants.JDBC_TABLE_NAME)
public class JdbcResourceEntity extends DataResourceEntity<JdbcResource> {

    @Column(name = ModelConstants.JDBC_DBURL)
    private String dbUrl;

    @Column(name = ModelConstants.JDBC_USERNAME)
    private String username;

    @Column(name = ModelConstants.JDBC_PASSWORD)
    private String password;

    public JdbcResourceEntity() {
        super();
    }

    public JdbcResourceEntity(JdbcResource jdbcResource) {
        if (jdbcResource.getId() != null) {
            this.setId(jdbcResource.getId().getId());
        } else {
            this.setId(Generators.timeBasedGenerator().generate());
        }

        this.dbUrl = jdbcResource.getDbUrl();
        this.username = jdbcResource.getUsername();
        this.password = jdbcResource.getPassword();
    }

    @Override
    public JdbcResource toData() {
        JdbcResource jdbcResource = new JdbcResource(new JdbcResourceId(getId()));
        jdbcResource.setCreatedTime(UUIDs.unixTimestamp(getId()));
        jdbcResource.setDbUrl(dbUrl);
        jdbcResource.setUsername(username);
        jdbcResource.setPassword(password);
        return jdbcResource;
    }
}
