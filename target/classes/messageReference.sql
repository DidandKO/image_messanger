DROP TABLE IF EXISTS message_reference;

CREATE TABLE message_reference
(
    mr_id INT PRIMARY KEY,
    message_id INT FOREIGN KEY,
    to VARCHAR(255) NOT NULL,
    from VARCHAR(255) NOT NULL
);