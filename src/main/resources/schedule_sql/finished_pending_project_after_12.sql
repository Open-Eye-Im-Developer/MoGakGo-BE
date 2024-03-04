UPDATE project_tb
SET project_status = 'CANCELED'
WHERE DATE(created_at) != CURDATE()
AND project_status = 'PENDING';
