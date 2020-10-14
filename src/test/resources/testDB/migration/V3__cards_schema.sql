CREATE TABLE cards
(
    card_id          SERIAL PRIMARY KEY,
    name             VARCHAR(50)  NOT NULL,
    status           INTEGER      NOT NULL,
    background_image VARCHAR(200) NULL,
    card_link        VARCHAR(200) NULL,
    user_id          INTEGER      NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users (user_id)
);