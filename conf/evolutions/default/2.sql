# --- !Ups

create table tags (
    id serial primary key,
    name varchar(100)
);

create table snippet_tags (
    snippet_id integer,
    tag_id integer,
    primary key (snippet_id, tag_id)
);


# --- !Downs

drop table if exists snippet_tags;
drop table if exists tags;