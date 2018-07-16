package com.hashmapinc.haf.install;

import com.hashmapinc.haf.exceptions.SchemaCreationException;

public interface DatabaseSchemaService {

    void createDatabaseSchema() throws SchemaCreationException;
}
