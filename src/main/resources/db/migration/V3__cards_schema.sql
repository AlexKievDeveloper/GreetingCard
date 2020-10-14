CREATE TABLE cards
(
    card_id SERIAL PRIMARY KEY,
    name    VARCHAR(50) NOT NULL,
    status  INTEGER         NOT NULL,
    user_id INTEGER         NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);