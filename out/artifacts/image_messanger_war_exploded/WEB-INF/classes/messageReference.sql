DROP TABLE IF EXISTS message_reference;

CREATE TABLE message_reference
(
    mr_id INT PRIMARY KEY,
    message_id INT NOT NULL,
    to_ VARCHAR(255) NOT NULL,
    from_ VARCHAR(255) NOT NULL
);