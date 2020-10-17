CREATE TABLE another_links
(
    another_link_id   SERIAL PRIMARY KEY,
    another_path      VARCHAR(500),
    congratulation_id INTEGER,
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);