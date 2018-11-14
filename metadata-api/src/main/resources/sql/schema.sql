CREATE TABLE IF NOT EXISTS metadata_config (
  id varchar(31) NOT NULL CONSTRAINT metadata_config_pkey PRIMARY KEY,
  owner_id varchar(31),
  name varchar,
  source_id varchar(31),
  sink_id varchar(31)
);

CREATE TABLE IF NOT EXISTS metadata_resource_jdbc (
  id varchar(31) NOT NULL CONSTRAINT jdbc_pkey PRIMARY KEY,
  db_url varchar,
  username varchar,
  password varchar
);

CREATE TABLE IF NOT EXISTS metadata_resource_rest (
  id varchar(31) NOT NULL CONSTRAINT rest_pkey PRIMARY KEY,
  url varchar
);


CREATE TABLE IF NOT EXISTS metadata_query (
  id varchar(31) NOT NULL CONSTRAINT query_pkey PRIMARY KEY,
  query varchar NOT NULL UNIQUE,
  metadata_id varchar(31) NOT NULL,
  trigger_schdl varchar,
  trigger_type varchar,
  attribute varchar,
  CONSTRAINT metadata_query_config_foreign_key foreign key (metadata_id) references metadata_config
);
