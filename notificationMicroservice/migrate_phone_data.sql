-- Migrate phone number data to standardize on userPhoneNumber column
-- Run this SQL script in your database

-- Step 1: Copy data from phoneNumber to userPhoneNumber
UPDATE users 
SET userPhoneNumber = phoneNumber 
WHERE phoneNumber IS NOT NULL AND (userPhoneNumber IS NULL OR userPhoneNumber = '');

-- Step 2: Verify the migration worked
SELECT userId, firstName, phoneNumber, userPhoneNumber 
FROM users 
WHERE phoneNumber IS NOT NULL;

-- Step 3: (Optional) Drop the redundant phoneNumber column after verification
-- Uncomment the line below after you verify the data migration worked:
-- ALTER TABLE users DROP COLUMN phoneNumber;
