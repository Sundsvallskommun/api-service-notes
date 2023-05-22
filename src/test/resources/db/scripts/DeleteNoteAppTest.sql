-- Entity 1:
INSERT INTO note(id, party_id, created, created_by, modified, modified_by, subject, body, context, role, client_id, municipality_id)
VALUES('2103ac13-1691-4017-b6c6-78fa75ff68fb', 'fe814729-254a-42ab-a123-111f2be83e40', '2022-01-01 12:14:32.234', 'createdBy1', '2022-01-03 12:14:32.234', 'updatedBy1', 'subject1', 'body1', 'context1', 'role1', 'clientId1', '2281');

INSERT INTO revision(id, created, entity_id, entity_type, serialized_snapshot, version)
VALUES('2103ac13-1691-4017-b6c6-78fa75ff68fc', '2022-01-01 12:14:32.234', '2103ac13-1691-4017-b6c6-78fa75ff68fb', 'NoteEntity', '{"id":"2103ac13-1691-4017-b6c6-78fa75ff68fb","party_id":"fe814729-254a-42ab-a123-111f2be83e40","created":"2022-01-01 12:14:32.234","created_by":"createdBy1","modified":"2022-01-03 12:14:32.234","modified_by":"updatedBy1","subject":"subject1","body":"body1","context":"context1","role":"role1","client_id":"clientId1","municipality_id":2281}', 1);