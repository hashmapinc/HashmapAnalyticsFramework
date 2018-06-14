package com.hashmap.haf.metadata.config.test;

import org.junit.ClassRule;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.runner.RunWith;

import java.util.Arrays;

@RunWith(ClasspathSuite.class)
@ClasspathSuite.ClassnameFilters({
        "com.hashmap.haf.metadata.config.test.controller.*SqlTest"
})
public class ControllerSqlITSuite {

    @ClassRule
    public static CustomSqlUnit sqlUnit = new CustomSqlUnit(
            Arrays.asList("sql/schema.sql"),
            "sql/drop-all-tables.sql",
            "sql-test.properties",
            Arrays.asList()
    );
}
