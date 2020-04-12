INSERT INTO config(name, value) VALUES ('config.access-token.signing-key', '123');
INSERT INTO config(name, value) VALUES ('config.access-token.validitity-seconds', '43200');
INSERT INTO config(name, value) VALUES ('config.grant.types', 'password,client_credentials,refresh_token');
INSERT INTO config(name, value) VALUES ('config.refresh-token.validitity-seconds', '86400');
INSERT INTO config(name, value) VALUES ('config.scope', 'WEB');


INSERT INTO `role`(id, description, name) VALUES (1, 'Super user.', 'ROLE_ADMIN');
INSERT INTO `role`(id, description, name) VALUES (2, 'Standard user.', 'ROLE_USER');

INSERT INTO privilege(id, description, name) VALUES (1, 'User administrator privilege.', 'ADMIN_PRIVILEGE');
INSERT INTO privilege(id, description, name) VALUES (2, 'Standard user privilege.', 'BASIC_PRIVILEGE');

INSERT INTO role_privilege(role_id, privilege_id) VALUES (1, 1);
INSERT INTO role_privilege(role_id, privilege_id) VALUES (2, 2);

-- Credential for testing -> clientId: clientIdTest, clientSecret: admin
INSERT INTO credential(id, client_id, client_secret, created_at, credentials_expired, description, grant_type, `scope`) VALUES (1, 'clientIdTest', '$2a$10$Pz5yIaKO/JwGQ0pio1XLs.xDPrPv95SO0F2A3BCKV9USuD1xqUlZ.', '2020-04-10', 0, "Credential for testing.", 'password,client_credentials,refresh_token', 'WEB');

-- User for testing -> username: renderjaviii, password: Password123
INSERT INTO `user`(id, account_locked, birth_date, created_at, document_number, first_name, gender, last_access_at, last_name, password, phone_number, username, role_id) VALUES (1, 0, '1998-06-03', '2020-04-11', '1015475241', 'Javier', 'M', '2020-04-11 22:22:38', 'Ardila', '$2a$10$EhhFZK1Rq8Zz31guk56vJOJXIOEyngnwhN/QdOFKMjpDA5ljhtdAG', '3146628827', 'renderjaviii', 1);
