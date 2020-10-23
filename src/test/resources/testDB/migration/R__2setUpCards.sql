INSERT INTO cards(name, background_image, card_link, status_id)
VALUES ('greeting Nomar', null, null,1);
INSERT INTO cards(name, background_image, card_link, status_id)
VALUES ('greeting Oleksandr', 'path_to_image', 'link_to_greeting',2);

INSERT INTO cards(name, background_image, card_link, status_id)
VALUES ('no_congratulation', 'path_to_image', 'link_to_greeting',1);


-- CREATE TABLE cards
-- (
--     card_id          SERIAL PRIMARY KEY,
--     name             VARCHAR(250) NOT NULL,
--     background_image VARCHAR(250),
--     card_link             VARCHAR(250),
--     status_id        INTEGER      NOT NULL,
--     FOREIGN KEY (status_id) REFERENCES statuses (status_id)
-- );