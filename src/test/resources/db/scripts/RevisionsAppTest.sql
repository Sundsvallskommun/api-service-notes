INSERT INTO revision(id, entity_id, entity_type, serialized_snapshot, version, created) VALUES
	('59328e70-4297-4bb5-ba69-cb17f2d15a17', '9791682e-4ba8-4f3a-857a-54e14836a53b', 'NoteEntity', '{ \"id\" : \"456a78cc-1194-45b2-84da-e06c63078f92\", \"partyId\" : \"81471222-5798-11e9-ae24-57fa13b361e1\", \"context\" : \"SUPPORT\", \"clientId\" : \"SUPPORT_MGMT\", \"role\" : \"FIRST_LINE_SUPPORT\", \"createdBy\" : \"John Doe\", \"created\" : \"2023-04-18T10:08:48.778+02:00\", \"subject\" : \"This is a subject\", \"body\" : \"This is a note\", \"caseId\" : \"12345\", \"caseType\" : \"Bygg\u00E4rende\", \"caseLink\" : \"http:\/\/test.sundsvall.se\/case1337\", \"externalCaseId\" : \"2229\", \"municipalityId\" : \"0115\"}', 1, '2022-01-01 12:14:32.234'),
	('5ac0398d-67d7-4267-b7b1-d9983b51758b', '9791682e-4ba8-4f3a-857a-54e14836a53b', 'NoteEntity', '{ \"id\" : \"456a78cc-1194-45b2-84da-e06c63078f92\", \"partyId\" : \"81471222-5798-11e9-ae24-57fa13b361e1\", \"context\" : \"SUPPORT\", \"clientId\" : \"SUPPORT_MGMT\", \"role\" : \"SECOND_LINE_SUPPORT\", \"createdBy\" : \"John Doe\", \"created\" : \"2023-04-18T10:08:48.778+02:00\", \"subject\" : \"This is a subject\", \"body\" : \"This is a changed note\", \"caseId\" : \"12345\", \"caseType\" : \"Bygg\u00E4rende\", \"caseLink\" : \"http:\/\/test.sundsvall.se\/case1337\", \"externalCaseId\" : \"2229\", \"municipalityId\" : \"0115\", \"modifiedBy\" : \"The Dragon\"}', 2, '2022-02-02 12:14:32.234'),
	('207ef370-607b-4502-9d16-bf38defb1dfd', '9791682e-4ba8-4f3a-857a-54e14836a53b', 'NoteEntity', '{}', 3, '2022-02-03 12:14:32.234'),
	('f9e222f3-2476-4ead-bb1a-3e7e25f9c6ee', '9791682e-4ba8-4f3a-857a-54e14836a53b', 'NoteEntity', '{}', 4, '2022-02-04 12:14:32.234'),
	('203c924b-dd67-4802-b99f-256ef6f2de69', '9791682e-4ba8-4f3a-857a-54e14836a53b', 'NoteEntity', '{}', 5, '2022-02-05 12:14:32.234'),
	('c05f025e-b758-40ce-aba7-06e7243395bb', 'abe72bbd-9808-4f3a-8aec-cd2945f5a201', 'NoteEntity', '{}', 11, '2022-02-01 12:14:32.234'),
	('9906cef3-810e-4ddc-977a-af0aa259a838', 'abe72bbd-9808-4f3a-8aec-cd2945f5a201', 'NoteEntity', '{}', 12, '2022-02-02 12:14:32.234');