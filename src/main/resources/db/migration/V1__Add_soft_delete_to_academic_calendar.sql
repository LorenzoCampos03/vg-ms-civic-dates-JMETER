-- Add academic_year_name column to academic_calendar table
ALTER TABLE academic_calendar 
ADD COLUMN academic_year_name VARCHAR(255) NULL;

-- Add status column to academic_calendar table
ALTER TABLE academic_calendar 
ADD COLUMN status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE';

-- Create indexes for filtering by status
CREATE INDEX idx_academic_calendar_status ON academic_calendar(status);
CREATE INDEX idx_academic_calendar_institution_status ON academic_calendar(institution_id, status);
