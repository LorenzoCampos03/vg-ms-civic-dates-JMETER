-- Add image_url column to event table
ALTER TABLE event ADD COLUMN image_url VARCHAR(255) NULL;

-- Create table for default images per event type
CREATE TABLE event_type_image (
    event_type VARCHAR(50) PRIMARY KEY,
    image_url VARCHAR(255) NOT NULL
);

-- Seed event_type_image with user's Cloudinary image URLs
INSERT INTO event_type_image (event_type, image_url) VALUES
('CIVICO', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/civico_iycrnh.webp'),
('RELIGIOSO', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/religioso_uqc6ax.jpg'),
('INSTITUCIONAL', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/institucional_ib0cnf.avif'),
('CULTURAL', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/cultura_l3y9ni.jpg'),
('INCIDENTE', 'https://res.cloudinary.com/dsrqcffal/image/upload/v1779314785/incidente_yjpvrl.avif')
ON CONFLICT (event_type) DO UPDATE SET image_url = EXCLUDED.image_url;
