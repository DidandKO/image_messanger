DROP TABLE IF EXISTS messages;

CREATE TABLE messages
(
    message_id INT PRIMARY KEY,
    timestamp TIMESTAMP NOT NULL,
    body VARCHAR(4095) NOT NULL,
    image_src VARCHAR(255),
    byte_code VARCHAR(2040),
    sender INT NOT NULL
);