INSERT OR IGNORE INTO invoices (id, appointment_id, patient_name, services, insurance_adjustment, total_amount, balance, status, created_at) VALUES
('inv-1', 1, 'Alice Johnson', 'Lab:100', 0, 100, 0, 'Paid', '2025-06-01T10:30:00'),         
('inv-2', 2, 'Bob Brown', 'Consult:100', 20, 80, 40, 'Partial', '2025-06-02T16:00:00'),    
('inv-3', 3, 'Charlie Davis', 'XRay:75', 0, 75, 75, 'Sent', '2025-06-03T09:30:00');         