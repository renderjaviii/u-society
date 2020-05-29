INSERT INTO authenticationConfig(name, value) VALUES('config.otp-expiry-time', '1');

INSERT INTO authenticationConfig(name, value) VALUES('provider.authentication-service.url', 'http://localhost:8075/');
INSERT INTO authenticationConfig(name, value) VALUES('provider.authentication-service.authentication-path', '/oauth/token');
INSERT INTO authenticationConfig(name, value) VALUES('provider.authentication-service.users-path', '/users');

INSERT INTO authenticationConfig(name, value) VALUES('provider.authentication-client.id', 'clientIdTest');
INSERT INTO authenticationConfig(name, value) VALUES('provider.authentication-client.id', 'admin');
