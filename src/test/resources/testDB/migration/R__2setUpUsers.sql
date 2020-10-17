INSERT INTO users(firstName, lastName, login, email, password, salt, language_id)
VALUES ('admin','admin','admin', '@admin','2bb7998496899acdd8137fad3a44faf96a84a03d7f230ce42e97cd17c7ae429e','salt',1);
INSERT INTO users(firstName, lastName, login, email, password, salt, language_id)
VALUES ('user','user','user', '@user','8031377c4c15e1611986089444c8ff58c95358ffdc95d692a6d10c7b633e99df','salt',2);



-- CREATE TABLE users
-- (
--     user_id     SERIAL PRIMARY KEY,
--     firstName   VARCHAR(40)        NOT NULL,
--     lastName    VARCHAR(40),
--     login       VARCHAR(50) UNIQUE NOT NULL,
--     email       VARCHAR(50) UNIQUE NOT NULL,
--     password    VARCHAR(200)       NOT NULL,
--     salt        VARCHAR(200)       NOT NULL,
--     language_id INTEGER            NOT NULL,
--     FOREIGN KEY (language_id) REFERENCES language (language_id)
-- );
