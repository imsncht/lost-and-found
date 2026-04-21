CREATE DATABASE IF NOT EXISTS lostfound;
USE lostfound;

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(120) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER'
);

CREATE TABLE items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    description TEXT,
    category VARCHAR(80),
    location VARCHAR(150),
    itemDate DATE,
    type VARCHAR(20),
    status VARCHAR(20) DEFAULT 'OPEN',
    imagePath VARCHAR(255),
    userId BIGINT,
    FOREIGN KEY (userId) REFERENCES users(id)
);

CREATE TABLE claims (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    itemId BIGINT,
    claimantId BIGINT,
    message TEXT,
    status VARCHAR(20) DEFAULT 'PENDING',

    FOREIGN KEY (itemId) REFERENCES items(id),
    FOREIGN KEY (claimantId) REFERENCES users(id)
);

INSERT INTO users(name,email,password,role)
VALUES(
'Admin',
'admin@lostfound.com',
'$2a$10$7EqJtq98hPqEX7fNZaFWoOHiM8zY6P6KycdxY6SY3Y0JVpaz6RtZ2',
'ADMIN'
);