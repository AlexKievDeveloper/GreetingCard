INSERT INTO users_cards(user_id,card_id, role_id) VALUES (1, 1, 1);
INSERT INTO users_cards(user_id,card_id, role_id) VALUES (2, 1, 2);

INSERT INTO users_cards(user_id,card_id, role_id) VALUES (2, 2, 1);
INSERT INTO users_cards(user_id,card_id, role_id) VALUES (1, 2, 2);




-- CREATE TABLE users_cards
-- (
--     card_id INTEGER NOT NULL,
--     user_id INTEGER NOT NULL,
--     role_id INTEGER NOT NULL,
--     FOREIGN KEY (card_id) REFERENCES cards (card_id),
--     FOREIGN KEY (user_id) REFERENCES users (user_id),
--     FOREIGN KEY (role_id) REFERENCES roles (role_id)
-- );