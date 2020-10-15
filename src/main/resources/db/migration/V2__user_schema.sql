CREATE TABLE users
(
    user_id       SERIAL PRIMARY KEY,
    firstName     VARCHAR(40)        NOT NULL,
    lastName      VARCHAR(40),
    login         VARCHAR(50) UNIQUE NOT NULL,
    email         VARCHAR(50) UNIQUE NOT NULL,
    role          INTEGER                NOT NULL,
    password      VARCHAR(200)       NOT NULL,
    sole          VARCHAR(200)       NOT NULL,
    language_name varchar(50)        NOT NULL,
    FOREIGN KEY (language_name) REFERENCES language (language_id)
);
