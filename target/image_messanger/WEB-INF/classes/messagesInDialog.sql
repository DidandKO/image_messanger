DROP TABLE IF EXISTS messages_in_dialog;

CREATE TABLE messages_in_dialog
(
    dialog_id  INT PRIMARY KEY,
    message_id INT NOT NULL
);