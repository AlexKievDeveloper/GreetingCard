CREATE TABLE users
(
    user_id     SERIAL PRIMARY KEY,
    firstName   VARCHAR(40),
    lastName    VARCHAR(40),
    login       VARCHAR(50) UNIQUE NOT NULL,
    email       VARCHAR(50) UNIQUE NOT NULL,
    email_verified BOOLEAN,
    password    VARCHAR(200)       NOT NULL,
    salt        VARCHAR(200)       NOT NULL,
    language_id INTEGER,
    facebook    VARCHAR(200),
    google      VARCHAR(200),
    pathToPhoto VARCHAR(200),

    FOREIGN KEY (language_id) REFERENCES languages (language_id)
);

CREATE RULE verify_email AS ON DELETE TO verify_email_hashes
    DO UPDATE users SET email_verified = '1'
    WHERE users.user_id = verify_email_hashes.user_id;