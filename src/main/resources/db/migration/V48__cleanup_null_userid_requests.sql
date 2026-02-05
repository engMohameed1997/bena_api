-- Clean up job requests with NULL user_id
-- These are old requests created before UUID migration

-- Delete job request images for requests with NULL user_id
DELETE FROM job_request_images 
WHERE job_request_id IN (
    SELECT id FROM job_requests WHERE user_id IS NULL
);

-- Delete job requests with NULL user_id
DELETE FROM job_requests WHERE user_id IS NULL;
