INSERT INTO role (name) VALUES ('ROLE_ADMIN');
INSERT INTO role (name) VALUES ('ROLE_USER');
INSERT INTO "user" (username, password) VALUES ('admin', '123123'); -- senha: admin123
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1);