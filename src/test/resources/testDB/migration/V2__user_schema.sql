CREATE TABLE users
(
    user_id     SERIAL PRIMARY KEY,
    firstName   VARCHAR(40),
    lastName    VARCHAR(40),
    login       VARCHAR(50) UNIQUE NOT NULL,
    email       VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(200)       NOT NULL,
    salt        VARCHAR(200)       NOT NULL,
    language_id INTEGER,
    facebook    VARCHAR(200),
    google      VARCHAR(200),
    pathToPhoto VARCHAR(200),

    FOREIGN KEY (language_id) REFERENCES languages (language_id)
);