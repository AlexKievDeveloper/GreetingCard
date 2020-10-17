CREATE TABLE images
(
    image_id          SERIAL PRIMARY KEY,
    image_path        VARCHAR(500),
    congratulation_id INTEGER,
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);