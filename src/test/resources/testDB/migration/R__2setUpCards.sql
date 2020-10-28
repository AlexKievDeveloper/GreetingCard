INSERT INTO cards(user_id, name, background_image, card_link, status_id)
VALUES (1,'greeting Nomar', null, null,1);
INSERT INTO cards(user_id, name, background_image, card_link, status_id)
VALUES (2,'greeting Oleksandr', 'path_to_image', 'link_to_greeting',2);
INSERT INTO cards(user_id, name, background_image, card_link, status_id)
VALUES (2,'no_congratulation', 'path_to_image', 'link_to_greeting',1);


-- CREATE TABLE cards
-- (
--     card_id          SERIAL PRIMARY KEY,
--     user_id          BIGINT NOT NULL,
--     name             VARCHAR(250) NOT NULL,
--     background_image VARCHAR(250),
--     card_link        VARCHAR(500),
--     status_id        INTEGER      NOT NULL,
--     FOREIGN KEY (user_id) REFERENCES users (user_id),
--     FOREIGN KEY (status_id) REFERENCES statuses (status_id)
-- );