CREATE TABLE youtube_links
(
    youtube_link_id   SERIAL PRIMARY KEY,
    youtube_path      VARCHAR(500),
    congratulation_id INTEGER,
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);