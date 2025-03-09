create table users (
    id bigserial primary key,
    "password" varchar(255) NOT NULL,
    username varchar(255) NOT NULL
);