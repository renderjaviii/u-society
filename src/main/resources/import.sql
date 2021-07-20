-- Category list
INSERT INTO categories
    (id, name)
VALUES
    (1, 'Sports'),
    (2, 'Video games'),
    (3, 'Education');

-- Groups for testing
INSERT INTO groups (id, description, name, objectives, photo, rules, category_id,slug) VALUES(1, 'Group for testing', 'Group name', 'Test,XYZ', 'https://u-society.s3.amazonaws.com/1602210585190-image', 'A bunch', 2, 'group-name');

-- Groups & Users associations
INSERT INTO user_groups (id, is_admin, `role`, status, user_id, group_id) VALUES(1, 1, 'Administrador', 0, 1, 1);
INSERT INTO user_groups (id, is_admin, `role`, status, user_id, group_id) VALUES(2, 0, 'Player', 0, 2, 1);

-- Posts for testing
INSERT INTO posts (id, content, creation_date, description, expiration_date, is_public, group_id, user_id)
VALUES(3, '{"type":"TEXT","value":"Hi everyone"}', '2020-10-09 03:24:19.0', NULL, NULL, 1, 1, 1);
INSERT INTO posts (id, content, creation_date, description, expiration_date, is_public, group_id, user_id)
VALUES(4, '{"type":"IMAGE","value":"https://u-society.s3.amazonaws.com/1602213924661-image"}', '2020-10-09 03:25:26.0', 'Exhibition photo', NULL, 0, 1, 1);
INSERT INTO posts (id, content, creation_date, description, expiration_date, is_public, group_id, user_id)
VALUES(14, '{"type":"SURVEY","value":"¡Fast survey!","options":[{"id":0,"amount":1,"value":"Me interesa."},{"id":1,"amount":1,"value":"I am not interested in"}]}', '2020-10-10 02:15:47.0', NULL, '2020-10-11 08:30:13.0', 0, 1, 1);

-- Surveys interactions records
INSERT INTO surveys (id, user_id, vote, post_id) VALUES(18, 1, 0, 14);
INSERT INTO surveys (id, user_id, vote, post_id) VALUES(28, 2, 1, 14);

-- Posts reactions records
INSERT INTO reacts (id, user_id, value, post_id) VALUES(2, 1, 1, 3);
INSERT INTO reacts (id, user_id, value, post_id) VALUES(3, 2, 1, 3);
INSERT INTO reacts (id, user_id, value, post_id) VALUES(5, 1, 3, 4);
INSERT INTO reacts (id, user_id, value, post_id) VALUES(6, 2, 2, 4);

-- Posts comments records
INSERT INTO comments (id, creation_date, user_id, value, post_id) VALUES(1, '2020-10-10 02:30:51.0', 2, 'Hello.', 3);
INSERT INTO comments (id, creation_date, user_id, value, post_id) VALUES(2, '2020-10-10 02:31:03.0', 2, 'Nice.', 3);
INSERT INTO comments (id, creation_date, user_id, value, post_id) VALUES(3, '2020-10-10 02:31:11.0', 1, 'I cannot believe.', 3);
INSERT INTO comments (id, creation_date, user_id, value, post_id) VALUES(4, '2020-10-10 02:31:23.0', 1, 'Hi everyone, welcome!!', 4);
INSERT INTO comments (id, creation_date, user_id, value, post_id) VALUES(6, '2020-10-10 17:59:19.0', 2, "Hi, how's it going?", 4);

-- Group chat records
INSERT INTO messages (id, content, creation_date, `type`, user_id, group_id) VALUES(1, 'Test message 1', '2020-10-09 03:15:17.0', 0, 2, 1);
INSERT INTO messages (id, content, creation_date, `type`, user_id, group_id) VALUES(2, 'https://u-society.s3.amazonaws.com/1602213346295-image', '2020-10-09 03:15:48.0', 1, 1, 1);
INSERT INTO messages (id, content, creation_date, `type`, user_id, group_id) VALUES(3, 'Test message 2', '2020-10-10 20:38:25.0', 0, 1, 1);
INSERT INTO messages (id, content, creation_date, `type`, user_id, group_id) VALUES(4, 'New.', '2020-10-10 20:45:50.0', 0, 1, 1);
