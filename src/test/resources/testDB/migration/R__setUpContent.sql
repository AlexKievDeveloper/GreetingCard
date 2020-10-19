INSERT INTO links(link,type_id, congratulation_id) VALUES ('you_tube_1',1,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('you_tube_2',1,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('you_tube_3',1,2);

INSERT INTO links(link,type_id, congratulation_id) VALUES ('audio_1',2,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('audio_2',2,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('audio_3',2,2);

INSERT INTO links(link,type_id, congratulation_id) VALUES ('image_1',3,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('image_2',3,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('image_3',3,2);

INSERT INTO links(link,type_id, congratulation_id) VALUES ('link_1',4,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('link_1',4,1);
INSERT INTO links(link,type_id, congratulation_id) VALUES ('link_1',4,2);

/*CREATE TABLE links
(
    link_id           SERIAL PRIMARY KEY,
    link              VARCHAR(500),
    type_id           INTEGER,
    congratulation_id INTEGER,
    FOREIGN KEY (type_id) REFERENCES types (type_id),
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id)
);*/