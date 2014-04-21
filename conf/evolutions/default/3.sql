# --- !Ups

create table templates (
    id serial primary key,
    name varchar(50),
    tab_trigger varchar(20),
    content varchar(4000),
    mode varchar(40),
    last_updated_on timestamp
);

alter table templates add constraint unique_templates_name_mode unique (name, mode);

# --- !Downs

drop table if exists templates;