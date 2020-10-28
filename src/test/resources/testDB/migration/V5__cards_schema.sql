CREATE TABLE cards
(
    card_id          SERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    name             VARCHAR(250) NOT NULL,
    background_image VARCHAR(250),
    card_link        VARCHAR(500),
    status_id        INTEGER      NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (status_id) REFERENCES statuses (status_id)
);