CREATE TABLE IF NOT EXISTS workflow(
    id varchar(31) NOT NULL CONSTRAINT workflow_pkey PRIMARY KEY,
    name varchar(255)
);

CREATE TABLE IF NOT EXISTS workflow_tasks(
    id varchar(31) NOT NULL CONSTRAINT workflow_task_pkey PRIMARY KEY,
    dtype varchar(31) NOT NULL,
    name varchar(255),
    class_name varchar(255),
    input_cache varchar(255),
    output_cache varchar(255),
    jar varchar(255),
    workflow_id varchar(31),
    FOREIGN KEY (workflow_id) REFERENCES workflow(id)
);

CREATE TABLE IF NOT EXISTS workflow_configurations(
    workflow_entity_id varchar(31) NOT NULL,
    param varchar(255) NOT NULL,
    value varchar(255),
    PRIMARY KEY (workflow_entity_id, param),
    FOREIGN KEY (workflow_entity_id) REFERENCES workflow(id)
);

CREATE TABLE IF NOT EXISTS tasks_to_tasks_mapping(
    base_task_entity_id varchar(31) NOT NULL,
    to_base_task_entity_id varchar(31) NOT NULL,
    FOREIGN KEY (base_task_entity_id) REFERENCES workflow_tasks(id)
);

CREATE TABLE IF NOT EXISTS livy_task_arguments(
    livy_task_entity_id varchar(31) NOT NULL,
    param varchar(255) NOT NULL,
    value varchar(255),
    PRIMARY KEY (livy_task_entity_id, param),
    FOREIGN KEY (livy_task_entity_id) REFERENCES workflow_tasks(id)
);

CREATE TABLE IF NOT EXISTS livy_task_configurations(
    livy_task_entity_id varchar(31) NOT NULL,
    param varchar(255) NOT NULL,
    value varchar(255),
    PRIMARY KEY (livy_task_entity_id, param),
    FOREIGN KEY (livy_task_entity_id) REFERENCES workflow_tasks(id)
);

CREATE TABLE IF NOT EXISTS spark_ignite_task_arguments(
    spark_ignite_task_entity_id varchar(31) NOT NULL,
    param varchar(255) NOT NULL,
    value varchar(255),
    PRIMARY KEY (spark_ignite_task_entity_id, param),
    FOREIGN KEY (spark_ignite_task_entity_id) REFERENCES workflow_tasks(id)
);

CREATE TABLE IF NOT EXISTS spark_ignite_task_configurations(
    spark_ignite_task_entity_id varchar(31) not null,
    param varchar(255) not null,
    value varchar(255),
    PRIMARY KEY (spark_ignite_task_entity_id, param),
    FOREIGN KEY (spark_ignite_task_entity_id) REFERENCES workflow_tasks(id)
);




