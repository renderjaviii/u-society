-- Category list
INSERT INTO category (id, name) VALUES(1, 'Deporte');
INSERT INTO category (id, name) VALUES(2, 'Video juegos');
INSERT INTO category (id, name) VALUES(3, 'Educacional');

-- Groups for testing
INSERT INTO `group` (id, description, name, objectives, photo, rules, category_id) VALUES(1, 'Este es un grupo de lolsito.', 'Lolsito pro players!', 'Teast,XYZ,test', 'https://u-society.s3.amazonaws.com/1602210585190-image', 'Muchas', 2);
INSERT INTO `group` (id, description, name, objectives, photo, rules, category_id) VALUES(2, 'Grupo de prueba.', 'Grupo prueba', 'Test', NULL, 'Muchas', 1);

-- Groups & Users associations
INSERT INTO user_group (id, is_admin, `role`, status, user_id, group_id) VALUES(1, 1, 'Administrador', 0, 1, 1);
INSERT INTO user_group (id, is_admin, `role`, status, user_id, group_id) VALUES(2, 0, 'Crack', 0, 2, 1);
INSERT INTO user_group (id, is_admin, `role`, status, user_id, group_id) VALUES(3, 1, 'Administrador', 0, 2, 2);

-- Posts for testing
INSERT INTO post (id, content, creation_date, description, expiration_date, is_public, group_id)
VALUES(3, '{"type":"TEXT","value":"Hola a todos"}', '2020-10-09 03:24:19.0', NULL, NULL, 1, 1);
INSERT INTO post (id, content, creation_date, description, expiration_date, is_public, group_id)
VALUES(4, '{"type":"IMAGE","value":"https://u-society.s3.amazonaws.com/1602213924661-image"}', '2020-10-09 03:25:26.0', 'Foto de la feria', NULL, 0, 1);
INSERT INTO post (id, content, creation_date, description, expiration_date, is_public, group_id)
VALUES(14, '{"type":"SURVEY","value":"¡Encuesta rápida!","options":[{"id":0,"amount":1,"value":"Me interesa."},{"id":1,"amount":1,"value":"No me interesa."}]}', '2020-10-10 02:15:47.0', NULL, '2020-10-11 08:30:13.0', 0, 1);

-- Surveys interactions records
INSERT INTO survey (id, user_id, vote, post_id) VALUES(18, 1, 0, 14);
INSERT INTO survey (id, user_id, vote, post_id) VALUES(28, 2, 1, 14);

-- Posts reactions records
INSERT INTO react (id, user_id, value, post_id) VALUES(2, 1, 1, 3);
INSERT INTO react (id, user_id, value, post_id) VALUES(3, 2, 1, 3);
INSERT INTO react (id, user_id, value, post_id) VALUES(5, 1, 3, 4);
INSERT INTO react (id, user_id, value, post_id) VALUES(6, 2, 2, 4);

-- Posts comments records
INSERT INTO comment (id, creation_date, user_id, value, post_id) VALUES(1, '2020-10-10 02:30:51.0', 2, 'Hola.', 3);
INSERT INTO comment (id, creation_date, user_id, value, post_id) VALUES(2, '2020-10-10 02:31:03.0', 2, 'Chévere.', 3);
INSERT INTO comment (id, creation_date, user_id, value, post_id) VALUES(3, '2020-10-10 02:31:11.0', 1, 'Que va.', 3);
INSERT INTO comment (id, creation_date, user_id, value, post_id) VALUES(4, '2020-10-10 02:31:23.0', 1, 'Hola soy el admin, bienvenido!', 4);
INSERT INTO comment (id, creation_date, user_id, value, post_id) VALUES(6, '2020-10-10 17:59:19.0', 2, 'Hola, cómo van!', 4);

-- Group chat records
INSERT INTO message (id, content, creation_date, `type`, user_id, group_id) VALUES(1, 'Mensaje de prueba.', '2020-10-09 03:15:17.0', 0, 2, 1);
INSERT INTO message (id, content, creation_date, `type`, user_id, group_id) VALUES(2, 'https://u-society.s3.amazonaws.com/1602213346295-image', '2020-10-09 03:15:48.0', 1, 1, 1);
INSERT INTO message (id, content, creation_date, `type`, user_id, group_id) VALUES(3, 'Mensaje de prueba.', '2020-10-10 20:38:25.0', 0, 1, 1);
INSERT INTO message (id, content, creation_date, `type`, user_id, group_id) VALUES(4, 'nuevo.', '2020-10-10 20:45:50.0', 0, 1, 1);
