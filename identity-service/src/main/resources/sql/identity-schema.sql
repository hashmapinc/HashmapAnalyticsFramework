create table if not exists haf_users (
    id varchar(255) NOT NULL CONSTRAINT haf_user_pkey PRIMARY KEY,
    enabled boolean default false,
    first_name varchar(255),
    last_name varchar(255),
    tenant_id varchar(255),
    client_id varchar(255),
    customer_id varchar(255),
    user_name varchar(255),
    CONSTRAINT haf_users_unq_key UNIQUE (client_id, user_name)
);

create table if not exists haf_user_authorities (
    user_id varchar(255),
    authorities_id varchar(255)
);

create table if not exists haf_user_details (
    user_id varchar(255) not null,
    key_value varchar(255),
    key_name varchar(255) not null,
    primary key (user_id, key_name)
);


create table if not exists haf_user_credentials (
    id varchar(31) NOT NULL CONSTRAINT haf_user_credentials_pkey PRIMARY KEY,
    activation_token varchar(255) UNIQUE,
    password varchar(255),
    reset_token varchar(255) UNIQUE,
    user_id varchar(31) UNIQUE
);

/*alter table haf_user_authorities add constraint haf_user_foreign_key_auth foreign key (user_id) references haf_users;
#alter table haf_user_permissions add constraint haf_user_foreign_key_perm foreign key (user_id) references haf_users;
#alter table haf_user_details add constraint haf_user_foreign_key_details foreign key (user_id) references haf_users;*/
