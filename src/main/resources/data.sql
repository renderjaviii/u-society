INSERT INTO managerConfig(name, value) VALUES('config.otp-expiry-time', '1');
INSERT INTO managerConfig(name, value) VALUES ('config.access-token.signing-key', '123');

-- AWS - S3
INSERT INTO managerConfig(name, value) VALUES('config.aws.access-key', 'ASIASWV534S4E3O52UQU');
INSERT INTO managerConfig(name, value) VALUES('config.aws.secret-key', 'Gop4uk99eOIEwPFUsrBuZliZIZnj3nBMpNeuCv8f');
INSERT INTO managerConfig(name, value) VALUES('config.aws.session-token', 'FwoGZXIvYXdzEK3//////////wEaDKfpTqp0DGUN3i4gQiLQAb+L7nurmpkT5Ei7FL0BPi+6/U0cQmYtG/bgxNw6b5KBYcO5oGHj6dfirKhgOtuFhHTjPesGYLHV8tm8kMIzYMMc0bqsI6WTlcZu2C8Q1ZKbExV/0PfeDUJG6DyCCubbZrUHoGs6WBZC/svM/hB6bqAF34LdxFBWJsRw6ogkVzrngwKgsm09he8/cKLJvlNBxC48Qd4E2k/rkfAvjlX6rrPjz3tdvDfw++AlkbKwKFUTTxiScQIIYYx6wx664gdJ3Zlksd1pF0ktPhK596h9Vfco/cTo+wUyLTW1QUH0gu4DRVmq/XpdDa3XuX0ZePpM5Wgs3oywn1b22Byp2yVOctHjduLV/w==');
INSERT INTO managerConfig(name, value) VALUES('config.aws.endpoint-url', 'https://u-society.s3.amazonaws.com');
INSERT INTO managerConfig(name, value) VALUES('config.aws.bucket-nme', 'u-society');

INSERT INTO managerConfig(name, value) VALUES('web.authentication.url', 'http://localhost:8075/');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.path', '/oauth/token');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.users-path', '/v1/users');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.client-id', 'clientIdTest');
INSERT INTO managerConfig(name, value) VALUES('web.authentication.client-secret', 'admin');
INSERT INTO managerConfig(name, value) VALUES('web.time-out', '30');
