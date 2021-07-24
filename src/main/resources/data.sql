INSERT INTO managerConfigs(name, value) VALUES('config.otp-expiry-time', '1');
INSERT INTO managerConfigs(name, value) VALUES ('config.access-token.signing-key', '123');
INSERT INTO managerConfigs(name, value) VALUES('config.user.validate-otp', '0');

-- AWS: S3
INSERT INTO managerConfigs(name, value) VALUES('config.aws.endpoint-url', 'https://u-society.s3.amazonaws.com');
INSERT INTO managerConfigs(name, value) VALUES('config.aws.bucket-name', 'u-society');
INSERT INTO managerConfigs(name, value) VALUES('config.aws.access-key', 'NONE');
INSERT INTO managerConfigs(name, value) VALUES('config.aws.secret-key', 'NONE');
INSERT INTO managerConfigs(name, value) VALUES('config.aws.session-token', 'NONE');

-- Authentication API
INSERT INTO managerConfigs(name, value) VALUES('web.authentication.url', 'https://localhost:8075/authentication');
INSERT INTO managerConfigs(name, value) VALUES('web.authentication.path', '/oauth/token');
INSERT INTO managerConfigs(name, value) VALUES('web.authentication.users-path', '/v1/users');
INSERT INTO managerConfigs(name, value) VALUES('web.authentication.client-id', 'clientIdTest');
INSERT INTO managerConfigs(name, value) VALUES('web.authentication.client-secret', 'admin');
INSERT INTO managerConfigs(name, value) VALUES('web.time-out', '30');
