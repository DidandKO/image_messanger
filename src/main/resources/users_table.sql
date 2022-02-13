DROP TABLE IF EXISTS users_table;

CREATE TABLE users_table
(
    user_id INT PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    lastname VARCHAR(255) NOT NULL
);