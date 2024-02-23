UPDATE project_tb
SET project_status = 'FINISHED'
WHERE DATE(created_at) != CURDATE()
AND project_status = 'MATCHED';
