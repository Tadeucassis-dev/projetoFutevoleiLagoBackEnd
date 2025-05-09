CREATE TABLE role (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE "user" (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        username VARCHAR(255) NOT NULL UNIQUE,
                        password VARCHAR(255) NOT NULL
);

CREATE TABLE user_roles (
                            user_id BIGINT NOT NULL,
                            role_id BIGINT NOT NULL,
                            PRIMARY KEY (user_id, role_id),
                            FOREIGN KEY (user_id) REFERENCES "user"(id),
                            FOREIGN KEY (role_id) REFERENCES role(id)
);

CREATE TABLE student (
                         id BIGINT AUTO_INCREMENT PRIMARY KEY,
                         name VARCHAR(255) NOT NULL,
                         age INT NOT NULL,
                         school_unit VARCHAR(255) NOT NULL,
                         identity_file_path VARCHAR(255),
                         attendance_file_path VARCHAR(255),
                         approved BOOLEAN NOT NULL,
                         user_id BIGINT,
                         FOREIGN KEY (user_id) REFERENCES "user"(id)
);