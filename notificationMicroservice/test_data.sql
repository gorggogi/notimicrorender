-- Test Data for Notification Microservice
-- This script adds sample users to test the "send to all users" functionality

USE notification_db;

-- Insert test users with phone numbers
INSERT INTO Users (userId, firstName, phoneNumber, userPhoneNumber) VALUES
(2, 'John', '09171234567', NULL),
(3, 'Maria', '09281234567', NULL),
(4, 'Pedro', '09391234567', NULL),
(5, 'Ana', '09451234567', NULL),
(6, 'Carlos', '09561234567', NULL);

-- Insert a user without phone number to test error handling
INSERT INTO Users (userId, firstName, phoneNumber, userPhoneNumber) VALUES
(7, 'TestUser', NULL, NULL);

-- Verify the data was inserted
SELECT * FROM Users ORDER BY userId;

-- Expected result: 
-- - User 1 (dilig) already exists with phone 09279602351
-- - Users 2-6 have valid phone numbers 
-- - User 7 has no phone number (should be skipped in "send all")
-- - Total: 6 users should receive notifications when using "send all"
