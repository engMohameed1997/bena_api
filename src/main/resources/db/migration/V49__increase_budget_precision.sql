-- Increase budget field precision to handle larger amounts (Iraqi Dinar)
-- Change from DECIMAL(10,2) to DECIMAL(15,2) to support up to 9,999,999,999,999.99

ALTER TABLE job_requests 
ALTER COLUMN budget TYPE DECIMAL(15,2);

ALTER TABLE job_requests 
ALTER COLUMN worker_price_offer TYPE DECIMAL(15,2);
