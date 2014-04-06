# --- !Ups

create table snippets (
    id serial primary key,
    title varchar(100),
    description varchar(400),
    code varchar(4000)
);

# --- !Downs

drop table if exists snippets;