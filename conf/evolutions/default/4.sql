# --- !Ups

alter table templates add constraint unique_templates_name_mode unique (name, mode);

# --- !Downs

alter table drop constraint if exists unique_templates_name_mode;