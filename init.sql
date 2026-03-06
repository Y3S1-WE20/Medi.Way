-- ============================================================
-- MEDI.WAY Database Initialization Script
-- ============================================================
-- This script runs automatically when MySQL container starts
-- for the first time.
-- ============================================================

-- Create database if not exists
CREATE DATABASE IF NOT EXISTS mediway;

-- Use the database
USE mediway;

-- Grant privileges to application user
GRANT ALL PRIVILEGES ON mediway.* TO 'mediway'@'%';
FLUSH PRIVILEGES;

-- ============================================================
-- Note: Tables are created automatically by Hibernate
-- based on JPA entity definitions with ddl-auto=update
-- ============================================================

-- Optional: Insert sample data for testing/demo
-- Uncomment below if you want sample data

/*
-- Sample Doctors
INSERT INTO doctors (name, email, specialization) VALUES
    ('Dr. Sarah Wilson', 'sarah.wilson@mediway.com', 'Cardiology'),
    ('Dr. Michael Chen', 'michael.chen@mediway.com', 'Neurology'),
    ('Dr. Emily Brown', 'emily.brown@mediway.com', 'Pediatrics'),
    ('Dr. James Taylor', 'james.taylor@mediway.com', 'Orthopedics'),
    ('Dr. Lisa Anderson', 'lisa.anderson@mediway.com', 'General Medicine');
*/

-- Show created tables
SHOW TABLES;
