CREATE TABLE verify_email_hashes
(
    user_id INTEGER NOT NULL,
    hash VARCHAR(200) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE forgot_password_hashes
(
    user_id INTEGER NOT NULL,
    hash VARCHAR(200) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
);