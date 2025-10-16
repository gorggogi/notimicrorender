# Database Insertion Guide

This document provides instructions on how to use the `insert_data.sql` script to populate your MySQL database with sample data for the notification microservice.

## Prerequisites

- A running MySQL database instance.
- A database schema that matches the `User` and `AlertType` entities in the application.
- A MySQL client (like MySQL Workbench, DBeaver, or the command-line client) to execute the script.

## Script Overview

The `insert_data.sql` script is located in the `src/main/resources/` directory and performs the following actions:

1.  **Inserts Sample Users**: Adds two sample users, "Angelo Fecunda" and "Fecunda Angelo", to the `users` table.
2.  **Inserts Sample Alert Types**: Populates the `AlertTypes` table with six common alert categories (Weather, Emergency, Traffic, etc.).
3.  **Sets User Preferences**: Subscribes both sample users to the 'Weather' and 'Traffic' alert types by adding entries to the `UserPreferenceAlertType` join table.

## How to Use the Script

1.  **Connect to Your Database**: Open your preferred MySQL client and connect to the database where your `users` and `AlertTypes` tables are located.

2.  **Open the SQL Script**: Navigate to `src/main/resources/insert_data.sql` and open it.

3.  **Execute the Script**: Copy the contents of the script and paste them into the query editor of your MySQL client. Run the script.

4.  **Verify the Data**: After execution, you can verify that the data has been inserted correctly by running the following queries:

    ```sql
    -- Check for the sample users
    SELECT * FROM users WHERE email LIKE '%@gmail.com';

    -- Check for the alert types
    SELECT * FROM AlertTypes;

    -- Check for the users' preferences
    SELECT * FROM UserPreferenceAlertType WHERE userId IN (1, 2);
    ```

This will confirm that the sample data is now in your database and ready for use by the application.
