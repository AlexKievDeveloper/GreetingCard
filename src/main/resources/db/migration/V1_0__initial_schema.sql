CREATE TABLE IF NOT EXISTS languages
(
    language_id BIGINT PRIMARY KEY,
    name    VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS users
(
    user_id     SERIAL PRIMARY KEY,
    firstName   VARCHAR(40),
    lastName    VARCHAR(40),
    login       VARCHAR(50) UNIQUE NOT NULL,
    email       VARCHAR(50) UNIQUE NOT NULL,
    password    VARCHAR(200)       NOT NULL,
    salt        VARCHAR(200)       NOT NULL,
    language_id INTEGER,
    facebook    VARCHAR(200),
    google      VARCHAR(200),
    pathToPhoto VARCHAR(200),

    FOREIGN KEY (language_id) REFERENCES languages (language_id)
);

CREATE TABLE IF NOT EXISTS statuses
(
    status_id BIGINT PRIMARY KEY,
    status    VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS roles
(
    role_id BIGINT PRIMARY KEY,
    role    VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS cards
(
    card_id          SERIAL PRIMARY KEY,
    user_id          BIGINT NOT NULL,
    name             VARCHAR(250) NOT NULL,
    background_image VARCHAR(250),
    card_link        VARCHAR(500),
    status_id        INTEGER      NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (status_id) REFERENCES statuses (status_id)
);

CREATE TABLE IF NOT EXISTS users_cards
(
    users_cards_id SERIAL PRIMARY KEY,
    card_id        INTEGER NOT NULL,
    user_id        INTEGER NOT NULL,
    role_id        INTEGER NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards (card_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (role_id) REFERENCES roles (role_id),
    UNIQUE(card_id, user_id)
);

CREATE TABLE IF NOT EXISTS congratulations
(
    congratulation_id SERIAL PRIMARY KEY,
    message           text,
    card_id           INTEGER NOT NULL,
    user_id           INTEGER NOT NULL,
    status_id         INTEGER NOT NULL,
    FOREIGN KEY (card_id) REFERENCES cards (card_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    FOREIGN KEY (status_id) REFERENCES statuses (status_id)
);

CREATE TABLE IF NOT EXISTS types
(
    type_id BIGINT PRIMARY KEY,
    type    VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS links
(
    link_id           SERIAL PRIMARY KEY,
    link              VARCHAR(500),
    type_id           INTEGER,
    congratulation_id INTEGER,
    FOREIGN KEY (type_id) REFERENCES types (type_id),
    FOREIGN KEY (congratulation_id) REFERENCES congratulations (congratulation_id) ON DELETE CASCADE ON UPDATE CASCADE
);