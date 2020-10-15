CREATE TABLE congratulations
(
    congratulation_id SERIAL PRIMARY KEY,
    status            INTEGER,
    card_id           INTEGER NOT NULL,
    user_id           INTEGER NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards (card_id),
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);