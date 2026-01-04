-- Add public_id column to all user-generated media tables
-- This allows building URLs dynamically with transformations instead of storing full URLs
-- Encyclopedia media is excluded as it's admin-managed

-- 1. Pet Media (avatars and gallery)
ALTER TABLE pet.pet_media
ADD COLUMN IF NOT EXISTS public_id TEXT;

CREATE INDEX IF NOT EXISTS idx_pet_media_public_id ON pet.pet_media(public_id);

COMMENT ON COLUMN pet.pet_media.public_id IS 'Cloudinary public ID for building URLs with transformations';

-- 2. Post Media (photos/videos in posts)
ALTER TABLE social.post_media
ADD COLUMN IF NOT EXISTS public_id TEXT;

CREATE INDEX IF NOT EXISTS idx_post_media_public_id ON social.post_media(public_id);

COMMENT ON COLUMN social.post_media.public_id IS 'Cloudinary public ID for post media';

-- 3. User Avatar & Cover Image (store public_id separately for user media)
ALTER TABLE auth.users
ADD COLUMN IF NOT EXISTS avatar_public_id TEXT;

ALTER TABLE auth.users
ADD COLUMN IF NOT EXISTS cover_image_public_id TEXT;

ALTER TABLE auth.users
ADD COLUMN IF NOT EXISTS full_name VARCHAR(255);

ALTER TABLE auth.users
ADD COLUMN IF NOT EXISTS cover_image_url TEXT;

CREATE INDEX IF NOT EXISTS idx_users_avatar_public_id ON auth.users(avatar_public_id);
CREATE INDEX IF NOT EXISTS idx_users_full_name ON auth.users(full_name);

COMMENT ON COLUMN auth.users.avatar_public_id IS 'Cloudinary public ID for user avatar (avatar_url will be built from this)';
COMMENT ON COLUMN auth.users.cover_image_public_id IS 'Cloudinary public ID for user cover image';
COMMENT ON COLUMN auth.users.full_name IS 'Tên đầy đủ của người dùng';
COMMENT ON COLUMN auth.users.cover_image_url IS 'URL ảnh bìa profile (built from cover_image_public_id)';

