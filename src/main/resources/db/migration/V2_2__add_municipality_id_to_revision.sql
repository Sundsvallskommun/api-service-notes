alter table revision add column municipality_id varchar(255) not null;

create index revision_municipality_id_index on revision (municipality_id);
