CREATE TABLE IF NOT EXISTS users (
                                     id CHAR(36) PRIMARY KEY,
                                     email VARCHAR(255) UNIQUE NOT NULL,
                                     password VARCHAR(255) NOT NULL,
                                     role VARCHAR(50) NOT NULL
);

-- Insert user only if email doesn't exist
INSERT INTO users (id, email, password, role)
SELECT '223e4567-e89b-12d3-a456-426614174006',
       'testuser@test.com',
       '$2a$12$6dwsAJZKjmi9X2MnC2w1Y.NL8WCuzJtrQcKz40vQWdYQZA9dZEg7S',
       'ADMIN'
WHERE NOT EXISTS (
    SELECT 1 FROM users
    WHERE email = 'testuser@test.com'
);
