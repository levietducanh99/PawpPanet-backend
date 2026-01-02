-- V5: Add display_order to species_section_contents
-- Purpose: Add display_order column for consistent ordering of species sections

-- Add display_order column to species_section_contents
ALTER TABLE encyclopedia.species_section_contents
ADD COLUMN IF NOT EXISTS display_order INTEGER;

-- Set default display_order based on section_id
UPDATE encyclopedia.species_section_contents
SET display_order = section_id::INTEGER
WHERE display_order IS NULL;

-- Add comment
COMMENT ON COLUMN encyclopedia.species_section_contents.display_order IS 'Order for displaying sections in the species detail page';

