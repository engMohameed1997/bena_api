-- Migration: Change job_requests.user_id from BIGINT to UUID
-- This aligns the user_id with the UUID type used in the users table

-- Step 1: Add a new temporary UUID column
ALTER TABLE job_requests ADD COLUMN user_id_uuid UUID;

-- Step 2: Delete existing rows (test data) since numeric IDs cannot be converted to UUIDs
-- In production, you would need to map the old numeric IDs to actual user UUIDs
DELETE FROM job_request_images WHERE job_request_id IN (SELECT id FROM job_requests);
DELETE FROM job_requests;

-- Step 3: Drop the old column
ALTER TABLE job_requests DROP COLUMN user_id;

-- Step 4: Rename the new column
ALTER TABLE job_requests RENAME COLUMN user_id_uuid TO user_id;

-- Step 5: Add NOT NULL constraint
ALTER TABLE job_requests ALTER COLUMN user_id SET NOT NULL;

-- Step 6: Add foreign key constraint to users table (optional but recommended)
-- ALTER TABLE job_requests ADD CONSTRAINT fk_job_requests_user FOREIGN KEY (user_id) REFERENCES users(id);
