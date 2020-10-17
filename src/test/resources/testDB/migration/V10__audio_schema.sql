CREATE TABLE audios
(
    audio_id          SERIAL PRIMARY KEY,
    audio_path        VARCHAR(500),
    congratulation_id INTEGER,
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);