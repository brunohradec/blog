CREATE TABLE post (
    id SERIAL,
    title VARCHAR(255) NOT NULL,
    content TEXT,
    timestamp TIMESTAMP,
    author_id BIGINT NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (author_id) REFERENCES app_user(id)
)