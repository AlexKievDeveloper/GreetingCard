CREATE TABLE cards
(
    card_id          SERIAL PRIMARY KEY,
    name             VARCHAR(250) NOT NULL,
    background_image VARCHAR(250),
    link             VARCHAR(250),
    status_id        INTEGER      NOT NULL,
    FOREIGN KEY (status_id) REFERENCES statuses (status_id)
);