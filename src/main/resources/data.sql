--default leave types
INSERT IGNORE INTO leave_types (name, default_quota, created_at, updated_at) VALUES
('Sick Leave', 12, NOW(), NOW()),
('Casual Leave', 15, NOW(), NOW()),
('Annual Leave', 21, NOW(), NOW());

-- default admin
INSERT IGNORE INTO users (first_name, last_name, email, employee_id, password, role, active, created_at, updated_at) VALUES
('Admin', 'User', 'admin@gmail.com', 'ADMIN001', '$2a$10$hBQ/wEWqQENan6kQppbQTOJtsdfhtvlSEtqNw6luwCvtNMvnJyWX2', 'ADMIN', true, NOW(), NOW());