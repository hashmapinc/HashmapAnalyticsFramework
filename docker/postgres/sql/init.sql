CREATE DATABASE thingsboard;

\c thingsboard;

CREATE TABLE ts_kv (
 ID serial PRIMARY KEY,
 url VARCHAR (255) NOT NULL,
 name VARCHAR (255) NOT NULL,
 description VARCHAR (255),
 rel VARCHAR (50)
);

INSERT INTO ts_kv (url, name) VALUES ('http://www.postgresq.com','Tutorial');

INSERT INTO ts_kv (url, name) VALUES ('http://www.postgresqltutorial.com','PostgreSQL Tutorial');
