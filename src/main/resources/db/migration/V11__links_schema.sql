CREATE TABLE links
(
    link_id   SERIAL PRIMARY KEY,
    link      VARCHAR(500),
    congratulation_id INTEGER,
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);