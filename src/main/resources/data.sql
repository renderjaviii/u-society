INSERT INTO managerConfig(name, value) VALUES('config.otp-expiry-time', '1');
INSERT INTO managerConfig(name, value) VALUES ('config.access-token.signing-key', '123');

-- AWS - S3
INSERT INTO managerConfig(name, value) VALUES('config.aws.endpoint-url', 'https://u-society.s3.amazonaws.com');
INSERT INTO managerConfig(name, value) VALUES('config.aws.bucket-name', 'u-society');
INSERT INTO managerConfig(name, value) VALUES('config.aws.access-key', 'NONE');
INSERT INTO managerConfig(name, value) VALUES('config.aws.secret-key', 'NONE');
INSERT INTO managerConfig(name, value) VALUES('config.aws.session-token', 'NONE');

INSERT INTO managerConfig(name, value) VALUES('web.authentication.url', 'http://localhost:8075/');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.path', '/oauth/token');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.users-path', '/v1/users');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.client-id', 'clientIdTest');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.client-secret', 'admin');
INSERT INTO managerConfig(name, value) VALUES('web.time-out', '30');

-- Category list
INSERT INTO category (id, name) VALUES(1, 'Deporte');
INSERT INTO category (id, name) VALUES(2, 'Video juegos');
INSERT INTO category (id, name) VALUES(3, 'Educacional');

-- Group for testing
INSERT INTO `group` (id, description, name, objectives, photo, rules, category_id) VALUES(1, 'Este es un grupo de lolsito.', 'Lolsito pro players', '["Jugar LOL."]', 'https://u-society.s3.amazonaws.com/1602210585190-image', '["No ser manco."]', 2);
INSERT INTO company_db.user_group (id, is_admin, `role`, status, user_id, group_id) VALUES(1, 1, 'Administrador', 0, 1, 1);