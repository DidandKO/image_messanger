DROP TABLE IF EXISTS messages;

CREATE TABLE messages
(
    message_id INT PRIMARY KEY,
    timestamp VARCHAR NOT NULL,
    body VARCHAR(120) NOT NULL,
    image_src VARCHAR(255),
    byte_code VARCHAR(65535),
    sender INT NOT NULL
);