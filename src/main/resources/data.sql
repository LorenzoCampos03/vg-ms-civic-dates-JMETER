-- Clear existing data if any (though in-memory is usually clean)
DELETE FROM event_calendar;
DELETE FROM event_type_image;
DELETE FROM event;
DELETE FROM academic_calendar;

-- Seed event_type_image with user's Cloudinary image URLs
INSERT INTO event_type_image (event_type, image_url) VALUES
('CIVICO', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/civico_iycrnh.webp'),
('RELIGIOSO', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/religioso_uqc6ax.jpg'),
('INSTITUCIONAL', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/institucional_ib0cnf.avif'),
('CULTURAL', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/cultura_l3y9ni.jpg'),
('INCIDENTE', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/incidente_yjpvrl.avif');

-- Seed academic_calendar (Active & Inactive calendars)
INSERT INTO academic_calendar (id, institution_id, academic_year, academic_year_name, start_date, end_date, status, created_at, updated_at) VALUES
(1, 'INST001', 2026, 'Año Académico 2026', '2026-03-01', '2026-12-15', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'INST001', 2025, 'Año Académico 2025', '2025-03-01', '2025-12-15', 'INACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Seed events
INSERT INTO event (id, institution_id, title, description, start_date, end_date, event_type, is_holiday, is_recurring, is_national, affects_classes, created_by, status, created_at, updated_at, image_url) VALUES
(101, 'INST001', 'Día del Trabajo', 'Feriado nacional por el día del trabajo', '2026-05-01', '2026-05-01', 'CIVICO', TRUE, TRUE, TRUE, TRUE, 'admin', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/civico_iycrnh.webp'),
(102, 'INST001', 'Aniversario Institucional', 'Celebración del aniversario del colegio', '2026-06-15', '2026-06-15', 'INSTITUCIONAL', FALSE, TRUE, FALSE, FALSE, 'admin', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/institucional_ib0cnf.avif'),
(103, 'INST001', 'Feria de Ciencias', 'Feria de ciencias y tecnología escolar', '2026-08-20', '2026-08-22', 'CULTURAL', FALSE, FALSE, FALSE, FALSE, 'admin', 'ACTIVE', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/cultura_l3y9ni.jpg');

-- Seed event_calendar (map events to calendar 1)
INSERT INTO event_calendar (id, calendar_id, event_id, created_at) VALUES
(1, 1, 101, CURRENT_TIMESTAMP),
(2, 1, 102, CURRENT_TIMESTAMP),
(3, 1, 103, CURRENT_TIMESTAMP);
