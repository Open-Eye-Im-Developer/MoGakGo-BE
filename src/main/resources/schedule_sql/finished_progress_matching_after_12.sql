UPDATE matching_tb
SET matching_status = 'FINISHED'
WHERE DATE(created_at) != CURDATE();
