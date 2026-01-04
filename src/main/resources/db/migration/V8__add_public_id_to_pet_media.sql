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

-- 3. User Avatar (store public_id separately for user avatars)
ALTER TABLE auth.users
ADD COLUMN IF NOT EXISTS avatar_public_id TEXT;

CREATE INDEX IF NOT EXISTS idx_users_avatar_public_id ON auth.users(avatar_public_id);

COMMENT ON COLUMN auth.users.avatar_public_id IS 'Cloudinary public ID for user avatar (avatar_url will be built from this)';

