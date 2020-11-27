INSERT INTO congratulations(message, user_id, card_id,status_id)
 VALUES ('from Roma',1,1,1);
INSERT INTO congratulations(message, user_id, card_id,status_id)
 VALUES ('from Sasha',1,1,1);
INSERT INTO congratulations(message, user_id, card_id,status_id)
 VALUES ('from Nastya',2,1,2);

INSERT INTO congratulations(message, user_id, card_id,status_id)
 VALUES ('from Nomar',1,2,2);
INSERT INTO congratulations(message, user_id, card_id,status_id)
 VALUES ('from Sasha',2,2,2);
INSERT INTO congratulations(message, user_id, card_id,status_id)
 VALUES ('from Tolik',2,2,2);




-- CREATE TABLE congratulations
-- (
--     congratulation_id SERIAL PRIMARY KEY,
--     message           text,
--     user_id           INTEGER NOT NULL,
--     card_id           INTEGER NOT NULL,
--     status_id         INTEGER NOT NULL,
--     FOREIGN KEY (card_id) REFERENCES cards (card_id),
--     FOREIGN KEY (user_id) REFERENCES users (user_id),
--     FOREIGN KEY (status_id) REFERENCES statuses (status_id)
-- );