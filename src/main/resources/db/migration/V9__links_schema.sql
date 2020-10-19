CREATE TABLE links
(
    link_id           SERIAL PRIMARY KEY,
    link              VARCHAR(500),
    type_id           INTEGER,
    congratulation_id INTEGER,
    FOREIGN KEY (type_id) REFERENCES types (type_id),
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);