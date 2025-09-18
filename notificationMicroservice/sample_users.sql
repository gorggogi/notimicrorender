-- Sample users for testing userPhoneNumber functionality
-- Run this script to populate your database with test data

INSERT INTO Users (firstName, userPhoneNumber) VALUES 
('John', '09123456789'),
('Jane', '09987654321'),
('Bob', '09555123456'),
('Alice', '09777888999'),
('Charlie', '09111222333');

-- Verify the data was inserted
SELECT userId, firstName, userPhoneNumber FROM Users;
