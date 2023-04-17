
    create table note (
       id varchar(255) not null,
        body longtext,
        case_id varchar(255),
        case_link varchar(512),
        case_type varchar(255),
        client_id varchar(255),
        context varchar(255),
        created datetime(6),
        created_by varchar(255),
        external_case_id varchar(255),
        modified datetime(6),
        modified_by varchar(255),
        municipality_id varchar(255) not null,
        party_id varchar(255),
        role varchar(255),
        subject varchar(255),
        primary key (id)
    ) engine=InnoDB;

    create table revision (
       id varchar(255) not null,
        created datetime(6),
        entity_id varchar(255),
        entity_type varchar(255),
        serialized_snapshot longtext,
        version integer,
        primary key (id)
    ) engine=InnoDB;
create index note_party_id_index on note (party_id);
create index note_context_index on note (context);
create index note_client_id_index on note (client_id);
create index note_role_index on note (role);
create index note_municipality_id_index on note (municipality_id);
create index revision_entity_id_index on revision (entity_id);
create index revision_entity_type_index on revision (entity_type);