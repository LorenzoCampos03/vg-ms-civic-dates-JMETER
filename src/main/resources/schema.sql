-- Academic Calendar
CREATE TABLE IF NOT EXISTS academic_calendar (
    id SERIAL PRIMARY KEY,
    institution_id VARCHAR(255) NOT NULL,
    academic_year INT NOT NULL,
    academic_year_name VARCHAR(255),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- Event Table
CREATE TABLE IF NOT EXISTS event (
    id BIGSERIAL PRIMARY KEY,
    institution_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    description VARCHAR(1000),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    event_type VARCHAR(50) NOT NULL,
    is_holiday BOOLEAN DEFAULT FALSE,
    is_recurring BOOLEAN DEFAULT FALSE,
    is_national BOOLEAN DEFAULT FALSE,
    affects_classes BOOLEAN DEFAULT FALSE,
    created_by VARCHAR(255),
    status VARCHAR(20) DEFAULT 'ACTIVE' NOT NULL,
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    image_url VARCHAR(255),
    notification_channels VARCHAR(255),
    custom_message VARCHAR(1000),
    target_roles VARCHAR(255),
    is_notification_scheduled BOOLEAN DEFAULT FALSE
);

-- Event Type Image Table
CREATE TABLE IF NOT EXISTS event_type_image (
    event_type VARCHAR(50) PRIMARY KEY,
    image_url VARCHAR(255) NOT NULL
);

-- Event Calendar Association Table
CREATE TABLE IF NOT EXISTS event_calendar (
    id SERIAL PRIMARY KEY,
    calendar_id INT NOT NULL,
    event_id BIGINT NOT NULL,
    created_at TIMESTAMP
);

-- Create Indexes
CREATE INDEX IF NOT EXISTS idx_acad_cal_status ON academic_calendar(status);
CREATE INDEX IF NOT EXISTS idx_acad_cal_inst_status ON academic_calendar(institution_id, status);
