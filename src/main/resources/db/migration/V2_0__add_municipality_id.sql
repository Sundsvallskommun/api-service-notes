alter table note add column municipality_id varchar(255) not null;

create index note_municipality_id_index on note (municipality_id);
