CREATE TABLE IF NOT EXISTS metadata_config (
  id varchar(31) NOT NULL CONSTRAINT metadata_config_pkey PRIMARY KEY,
  owner_id varchar(31),
  name varchar,
  trigger_schdl varchar,
  trigger_type varchar,
  source_id varchar(31),
  sink_id varchar(31)
);

CREATE TABLE IF NOT EXISTS jdbc (
  id varchar(31) NOT NULL CONSTRAINT jdbc_pkey PRIMARY KEY,
  db_url varchar,
  username varchar,
  password varchar
);

CREATE TABLE IF NOT EXISTS rest (
  id varchar(31) NOT NULL CONSTRAINT rest_pkey PRIMARY KEY,
  url varchar,
  username varchar,
  password varchar
);


CREATE TABLE IF NOT EXISTS query_config (
  id varchar(31) NOT NULL CONSTRAINT rest_pkey PRIMARY KEY,
  query varchar NOT NULL UNIQUE,
  metadata_id varchar(31) NOT NULL,
  trigger_schdl varchar,
  trigger_type varchar
);
