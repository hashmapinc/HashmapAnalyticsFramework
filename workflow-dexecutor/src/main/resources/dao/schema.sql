CREATE TABLE IF NOT EXISTS workflow {
    id varchar(31) NOT NULL CONSTRAINT workflow_pk PRIMARY KEY,
    name varchar(255)
}

CREATE TABLE IF NOT EXISTS task {
    id varchar(31) NOT NULL CONSTRAINT task_pk PRIMARY KEY,
    name varchar(255),
    inputCache varchar(255),
    outputCache varchar(255)
}

CREATE TABLE IF NOT EXISTS task_functional_arguments {
    task_id varchar(31) NOT NULL,
    arg_name varchar(255),
    arg_value varchar(255)
}

CREATE TABLE IF NOT EXISTS task_configurations {
    task_id varchar(31) NOT NULL,
    arg_name varchar(255),
    arg_value varchar(255)
}
