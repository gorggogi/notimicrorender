-- Insert Sample Users
-- ------------------------------------------------------
-- This section inserts two sample users into the 'users' table.
-- The 'userId' is auto-incremented, so it is not included in the statement.
-- Passwords should be hashed in a real application; placeholders are used here.
-- ------------------------------------------------------
INSERT INTO users (firstName, lastName, email, password, userPhoneNumber)
VALUES 
('Angelo', 'Fecunda', 'angelo@gmail.com', 'securepassword123', '09266434056'),
('Fecunda', 'Angelo', 'fecunda@gmail.com', 'securepassword123', '09694413811');

-- ------------------------------------------------------
-- Insert Sample Alert Types
-- ------------------------------------------------------
-- This section inserts common alert types into the 'AlertTypes' table.
-- The 'alertId' is auto-incremented and does not need to be specified.
-- These types can be used for user notification preferences.
-- ------------------------------------------------------
INSERT INTO AlertTypes (alertName, alertDescription)
VALUES
('Weather', 'Notifications about weather conditions and forecasts.'),
('Emergency', 'Critical alerts regarding immediate threats to safety.'),
('Traffic', 'Updates on traffic conditions, road closures, and accidents.'),
('Health', 'Health advisories and public health announcements.'),
('School', 'Updates from educational institutions, including closures and events.'),
('Community', 'Announcements for local community events and news.');

-- ------------------------------------------------------
-- Insert Sample User Preferences
-- ------------------------------------------------------
-- This section links the sample users to specific alert types.
-- It sets preferences for both users to receive 'Weather' (alertId=1) and 'Traffic' (alertId=3) alerts.
-- This assumes the users and alert types have been inserted and their IDs are known.
-- ------------------------------------------------------
INSERT INTO UserPreferenceAlertType (userId, alertId, isEnabled)
VALUES
-- Preferences for Angelo Fecunda (userId=1)
(1, 1, true), -- Weather
(1, 3, true), -- Traffic
-- Preferences for Fecunda Angelo (userId=2)
(2, 1, true), -- Weather
(2, 3, true); -- Traffic

-- ------------------------------------------------------
-- End of Script
-- ------------------------------------------------------
