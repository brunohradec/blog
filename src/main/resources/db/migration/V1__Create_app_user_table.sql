CREATE TABLE app_user (
    id SERIAL,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    role VARCHAR(255) NOT NULL DEFAULT 'USER',

    PRIMARY KEY (id),
    UNIQUE (username),
    UNIQUE (email)
)