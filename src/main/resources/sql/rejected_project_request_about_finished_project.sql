UPDATE project_join_request_tb pjr
    JOIN project_tb p ON pjr.project_id = p.id
SET pjr.join_request_status = 'REJECTED'
WHERE p.project_status in ('FINISHED', 'CANCELED')
  AND pjr.join_request_status = 'PENDING';
