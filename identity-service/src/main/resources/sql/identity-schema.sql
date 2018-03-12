create table if not exists user_authorities (
    id varchar(255) NOT NULL,
    authorities_id varchar(255)
);

create table if not exists haf_users (
    id varchar(255) NOT NULL CONSTRAINT haf_user_pkey PRIMARY KEY,
    enabled boolean default false,
    first_name varchar(255),
    last_name varchar(255),
    password varchar(255),
    tenant_id varchar(255),
    user_name varchar(255)
);

alter table user_authorities add constraint haf_user_fk foreign key (id) references users;