package com.hashmap.haf.workflow;

import org.junit.ClassRule;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.runner.RunWith;
import java.util.Arrays;

@RunWith(ClasspathSuite.class)
@ClasspathSuite.ClassnameFilters({"com.hashmap.haf.workflow.*Spec"})
public class WorkflowTestApiSuite {
    @ClassRule
    public static CustomSqlUnit sqlUnit = new CustomSqlUnit(
            Arrays.asList("sql/workflow-schema.sql"),
            "drop-all-tables.sql",
            "workflow-api-test.properties"
    );
}
