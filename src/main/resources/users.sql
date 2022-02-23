DROP TABLE IF EXISTS users_table;

CREATE TABLE users_table
(
    user_id INT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(20) NOT NULL,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL,
    offline_time_in_minutes INT NOT NULL
);