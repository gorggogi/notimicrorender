-- Quick Test Data - Just 3 more users for testing

INSERT INTO Users (userId, firstName, phoneNumber) VALUES
(2, 'John', '09171234567'),
(3, 'Maria', '09281234567'),
(4, 'Pedro', '09391234567');

-- Check the results
SELECT userId, firstName, phoneNumber FROM Users ORDER BY userId;
