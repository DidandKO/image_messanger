DROP TABLE IF EXISTS dialog;

CREATE TABLE dialog
(
    dialog_id INT PRIMARY KEY,
    dialog_owner INT NOT NULL,
    partner INT NOT NULL,
    subject VARCHAR(255)
--     message_list ARRAY
);