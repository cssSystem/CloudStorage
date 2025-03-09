create table files (
    id bigserial primary key,
    filename varchar(255),
    size bigserial,
    "file" oid
);

