INSERT OR IGNORE INTO physicians 
(id, name, email, password, specialty, officeHours, notifyAppointment, notifyBilling, notifyMessages, phone, officeAddress)
VALUES
('1', 'Dr. Smith', 'smith@hospital.com', 'test123', 'Cardiology', 'Mon-Fri 9am-5pm', 1, 0, 1, '(204) 123-4567', '123 Heart St.'),
('2', 'Dr. Lee', 'lee@clinic.org', 'test123', 'Pediatrics', 'Tue-Thu 10am-4pm', 1, 1, 0, '(204) 987-6543', '456 Child Ave.'),
('3', 'Dr. Patel', 'patel@clinic.org', 'test123', 'Dermatology', 'Wed-Fri 8am-2pm', 0, 1, 1, '(204) 555-9999', '789 Skin Blvd.');
