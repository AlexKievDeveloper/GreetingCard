CREATE TABLE congratulation_contents
(
    content_id   SERIAL PRIMARY KEY,
    yuotube_link VARCHAR(500),
    image        VARCHAR(500),
    audio        VARCHAR(500),
    another_link VARCHAR(500),
    congratulation_id INTEGER,
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);