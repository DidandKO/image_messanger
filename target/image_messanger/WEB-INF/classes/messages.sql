DROP TABLE IF EXISTS messages;

CREATE TABLE messages
(
    message_id INT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    body VARCHAR(4095) NOT NULL
);