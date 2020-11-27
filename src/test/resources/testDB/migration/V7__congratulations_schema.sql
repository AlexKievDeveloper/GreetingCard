CREATE TABLE congratulations
(
    congratulation_id SERIAL PRIMARY KEY,
    message           text,
    card_id           INTEGER NOT NULL,
    user_id           INTEGER NOT NULL,
    status_id         INTEGER NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards (card_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (status_id) REFERENCES statuses (status_id)
);