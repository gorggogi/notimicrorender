-- Sample users for testing userPhoneNumber functionality
-- Run this script to populate your database with test data

INSERT INTO Users (userId, firstName, userPhoneNumber) VALUES 
(0, 'Charlene', '09279602351'),
(1, 'Angelo', '09266434056');
-- Verify the data was inserted
SELECT userId, firstName, userPhoneNumber FROM Users;
