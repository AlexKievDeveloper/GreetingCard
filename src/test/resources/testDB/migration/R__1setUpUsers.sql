INSERT INTO users(firstName, lastName, login, email, password, salt, language_id)
VALUES ('admin','admin','admin', '@admin','K7eZhJaJms3YE3+tOkT6+WqEoD1/IwzkLpfNF8euQp4=','salt',1);
INSERT INTO users(firstName, lastName, login, email, password, salt, language_id, pathToPhoto)
VALUES ('user','user','user', '@user','gDE3fEwV4WEZhgiURMj/WMlTWP/cldaSptEMe2M+md8=','salt',2,'profile/00.png');
INSERT INTO users(firstName, lastName, login, email, password, salt, language_id)
VALUES ('new','new','new', '@new','new','new',2);



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
