# --- !Ups

create table tags (
    id bigint auto_increment primary key,
    name varchar(100)
);

create table snippet_tags (
    snippet_id bigint,
    tag_id bigint,
    primary key (snippet_id, tag_id)
);


# --- !Downs

drop table if exists snippet_tags;
drop table if exists tags;