DROP TABLE IF EXISTS dialog;

CREATE TABLE dialog
(
    dialog_id INT PRIMARY KEY,
    dialog_owner INT NOT NULL,
    partner INT,
    subject VARCHAR(255),
    new_messages_count INT NOT NULL
);