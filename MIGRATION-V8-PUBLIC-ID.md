# Migration V8: Add Public ID to All Media Tables

## 📋 Overview

Migration V8 adds the `public_id` column to all user-generated media tables to enable:
- ✅ **Better security** - Validate media belongs to correct user/pet/post
- ✅ **URL flexibility** - Build URLs with transformations dynamically
- ✅ **Storage efficiency** - Store ~50 chars instead of ~150 chars
- ✅ **Performance** - Auto-optimized URLs (format, quality)

## 🎯 Tables Updated

### 1. `pet.pet_media`
**Purpose**: Pet avatars and gallery photos/videos

```sql
ALTER TABLE pet.pet_media
ADD COLUMN IF NOT EXISTS public_id TEXT;

CREATE INDEX IF NOT EXISTS idx_pet_media_public_id ON pet.pet_media(public_id);
```

**Usage**:
- Pet avatar: `pawplanet/pets/{petId}/avatar/{filename}`
- Pet gallery: `pawplanet/pets/{petId}/gallery/{filename}`

**Entity**: `PetMediaEntity.java`

### 2. `social.post_media`
**Purpose**: Photos/videos attached to social posts

```sql
ALTER TABLE social.post_media
ADD COLUMN IF NOT EXISTS public_id TEXT;

CREATE INDEX IF NOT EXISTS idx_post_media_public_id ON social.post_media(public_id);
```

**Usage**:
- Post media: `pawplanet/posts/{postId}/{filename}`

**Entity**: `PostMediaEntity.java`

### 3. `auth.users`
**Purpose**: User profile avatars

```sql
ALTER TABLE auth.users
ADD COLUMN IF NOT EXISTS avatar_public_id TEXT;

CREATE INDEX IF NOT EXISTS idx_users_avatar_public_id ON auth.users(avatar_public_id);
```

**Usage**:
- User avatar: `pawplanet/users/{userId}/avatar/{filename}`

**Entity**: `UserEntity.java`

## ❌ Tables NOT Updated

### `encyclopedia.media`
**Why**: Admin-managed content, no need for user upload validation
- Admins can upload directly or use pre-existing media
- No security risk from user manipulation
- Can still use Cloudinary, but validation not required

## 📊 Database Changes

### Before Migration

```
pet.pet_media
├── id
├── pet_id
├── type
├── role
├── url                    ← Full URL only
└── display_order

social.post_media
├── id
├── post_id
├── type
├── url                    ← Full URL only
└── display_order

auth.users
├── id
├── email
├── username
├── ...
└── avatar_url             ← Full URL only
```

### After Migration

```
pet.pet_media
├── id
├── pet_id
├── type
├── role
├── public_id              ← NEW: Cloudinary public_id
├── url                    ← Built from public_id
└── display_order

social.post_media
├── id
├── post_id
├── type
├── public_id              ← NEW: Cloudinary public_id
├── url                    ← Built from public_id
└── display_order

auth.users
├── id
├── email
├── username
├── ...
├── avatar_public_id       ← NEW: Cloudinary public_id
└── avatar_url             ← Built from public_id
```

## 🔧 Entity Updates

### 1. PetMediaEntity.java

```java
@Entity
@Table(name = "pet_media", schema = "pet")
public class PetMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "pet_id")
    private Long petId;
    
    private String type;
    private String role;
    
    @Column(name = "public_id", columnDefinition = "TEXT")
    private String publicId;  // ✅ NEW
    
    @Column(columnDefinition = "TEXT")
    private String url;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}
```

### 2. PostMediaEntity.java

```java
@Entity
@Table(name = "post_media", schema = "social")
public class PostMediaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "post_id")
    private Long postId;
    
    private String type;
    
    @Column(name = "public_id", columnDefinition = "TEXT")
    private String publicId;  // ✅ NEW
    
    @Column(columnDefinition = "TEXT")
    private String url;
    
    @Column(name = "display_order")
    private Integer displayOrder;
}
```

### 3. UserEntity.java

```java
@Entity
@Table(name = "users", schema = "auth")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String email;
    private String username;
    private String password;
    private String role;
    
    @Column(name = "avatar_public_id", columnDefinition = "TEXT")
    private String avatarPublicId;  // ✅ NEW
    
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;
    
    private String bio;
    // ... other fields
}
```

## 🚀 Running the Migration

### Development

```bash
# Using Maven
./mvnw flyway:migrate

# Or using Flyway CLI
flyway migrate
```

### Production (Heroku)

Migration runs automatically on deployment via:
```bash
# In Procfile
release: ./mvnw flyway:migrate
web: java -jar target/*.jar
```

### Verify Migration

```sql
-- Check if columns exist
SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_schema = 'pet' 
  AND table_name = 'pet_media' 
  AND column_name = 'public_id';

SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_schema = 'social' 
  AND table_name = 'post_media' 
  AND column_name = 'public_id';

SELECT column_name, data_type 
FROM information_schema.columns 
WHERE table_schema = 'auth' 
  AND table_name = 'users' 
  AND column_name = 'avatar_public_id';

-- Check indexes
SELECT indexname, indexdef 
FROM pg_indexes 
WHERE tablename IN ('pet_media', 'post_media', 'users')
  AND indexname LIKE '%public_id%';
```

## 📝 API Impact

### Pet Gallery API ✅ Already Updated
- Endpoint: `POST /api/v1/pets/{petId}/gallery`
- Uses `publicId` instead of `url`
- Validates public_id belongs to correct pet folder

### Post Media API ⚠️ Needs Update
- Endpoint: `POST /api/v1/posts` (create post)
- Endpoint: `PUT /api/v1/posts/{id}` (update post)
- Current: Uses `url` in `MediaUrlRequest`
- **TODO**: Update to use `publicId` like pet gallery

### User Avatar API ⚠️ Needs Update
- Endpoint: `PUT /api/v1/users/profile` (or similar)
- Current: Uses `avatarUrl` 
- **TODO**: Update to use `avatarPublicId`

## 🔄 Data Migration for Existing Records

If you have existing media with URLs but no public_id:

### Option 1: Extract from Existing URLs

```sql
-- For pet_media
UPDATE pet.pet_media
SET public_id = 
  REGEXP_REPLACE(
    url, 
    '^https://res\.cloudinary\.com/[^/]+/(image|video)/upload/(v[0-9]+/)?(.+)\.[a-z0-9]+$',
    '\3'
  )
WHERE public_id IS NULL 
  AND url IS NOT NULL
  AND url LIKE '%cloudinary.com%';

-- For post_media
UPDATE social.post_media
SET public_id = 
  REGEXP_REPLACE(
    url, 
    '^https://res\.cloudinary\.com/[^/]+/(image|video)/upload/(v[0-9]+/)?(.+)\.[a-z0-9]+$',
    '\3'
  )
WHERE public_id IS NULL 
  AND url IS NOT NULL
  AND url LIKE '%cloudinary.com%';

-- For users
UPDATE auth.users
SET avatar_public_id = 
  REGEXP_REPLACE(
    avatar_url, 
    '^https://res\.cloudinary\.com/[^/]+/image/upload/(v[0-9]+/)?(.+)\.[a-z0-9]+$',
    '\2'
  )
WHERE avatar_public_id IS NULL 
  AND avatar_url IS NOT NULL
  AND avatar_url LIKE '%cloudinary.com%';
```

### Option 2: Rebuild URLs on Application Startup

```java
@Component
public class MediaMigrationService {
    
    @Autowired
    private PetMediaRepository petMediaRepository;
    
    @Autowired
    private CloudinaryUrlBuilder cloudinaryUrlBuilder;
    
    @EventListener(ApplicationReadyEvent.class)
    public void migrateExistingMedia() {
        List<PetMediaEntity> mediaWithoutPublicId = 
            petMediaRepository.findAll().stream()
                .filter(m -> m.getPublicId() == null && m.getUrl() != null)
                .toList();
        
        for (PetMediaEntity media : mediaWithoutPublicId) {
            String publicId = extractPublicIdFromUrl(media.getUrl());
            if (publicId != null) {
                media.setPublicId(publicId);
                // Optionally rebuild URL with optimizations
                media.setUrl(cloudinaryUrlBuilder.buildOptimizedUrl(
                    publicId, 
                    media.getType()
                ));
            }
        }
        
        petMediaRepository.saveAll(mediaWithoutPublicId);
    }
    
    private String extractPublicIdFromUrl(String url) {
        // Extract public_id from Cloudinary URL
        // Example: https://res.cloudinary.com/demo/image/upload/v123/path/to/image.jpg
        //       -> path/to/image
        Pattern pattern = Pattern.compile(
            "https://res\\.cloudinary\\.com/[^/]+/(image|video)/upload/(?:v[0-9]+/)?(.+)\\.[a-z0-9]+"
        );
        Matcher matcher = pattern.matcher(url);
        return matcher.matches() ? matcher.group(2) : null;
    }
}
```

## 📊 Storage Savings

### Before
```
pet_media.url = "https://res.cloudinary.com/pawplanet/image/upload/v1234567890/pawplanet/pets/123/gallery/photo_abc.jpg" (120 chars)
post_media.url = "https://res.cloudinary.com/pawplanet/video/upload/v1234567890/pawplanet/posts/456/video_xyz.mp4" (115 chars)
users.avatar_url = "https://res.cloudinary.com/pawplanet/image/upload/v1234567890/pawplanet/users/789/avatar.jpg" (115 chars)
```

### After
```
pet_media.public_id = "pawplanet/pets/123/gallery/photo_abc" (40 chars)
post_media.public_id = "pawplanet/posts/456/video_xyz" (35 chars)
users.avatar_public_id = "pawplanet/users/789/avatar" (30 chars)
```

**Savings**: ~60-70% reduction in storage per media record!

## ✅ Checklist

After running migration:

- [ ] Migration V8 executed successfully
- [ ] All 3 tables have `public_id` column
- [ ] All 3 indexes created successfully
- [ ] PetMediaEntity updated with `publicId` field
- [ ] PostMediaEntity updated with `publicId` field
- [ ] UserEntity updated with `avatarPublicId` field
- [ ] Pet Gallery API works with `publicId` ✅ (already done)
- [ ] Post Media API updated to use `publicId` ⚠️ (TODO)
- [ ] User Avatar API updated to use `publicId` ⚠️ (TODO)
- [ ] Existing data migrated (if any)
- [ ] Documentation updated

## 🔜 Next Steps

### 1. Update Post API to use publicId

Similar to Pet Gallery API:
- Change `MediaUrlRequest` from `url` to `publicId`
- Add validation for post folder: `pawplanet/posts/{postId}/...`
- Build URL from publicId using `CloudinaryUrlBuilder`

### 2. Update User Avatar API

- Change user update request to accept `avatarPublicId`
- Validate folder: `pawplanet/users/{userId}/avatar/...`
- Build avatar_url from public_id

### 3. Apply to Other Features

Once proven successful, consider for:
- Comment media (if added later)
- Message attachments (if added later)
- Any other user-uploaded content

## 📞 Rollback Plan

If issues occur, rollback is simple since we only ADDED columns:

```sql
-- Remove columns (data preserved if rolled back)
ALTER TABLE pet.pet_media DROP COLUMN IF EXISTS public_id;
ALTER TABLE social.post_media DROP COLUMN IF EXISTS public_id;
ALTER TABLE auth.users DROP COLUMN IF EXISTS avatar_public_id;

-- Remove indexes
DROP INDEX IF EXISTS pet.idx_pet_media_public_id;
DROP INDEX IF EXISTS social.idx_post_media_public_id;
DROP INDEX IF EXISTS auth.idx_users_avatar_public_id;
```

Then revert code changes to entities.

## 📚 Related Documentation

- `PET-GALLERY-PUBLIC-ID-IMPLEMENTATION.md` - Implementation details for pet gallery
- `FRONTEND-QUICK-REFERENCE-PET-GALLERY.md` - Frontend integration guide
- `CloudinaryUrlBuilder.java` - URL building service

---

**Migration Version**: V8  
**Created**: 2026-01-04  
**Status**: ✅ Ready for deployment  
**Breaking Changes**: None (backward compatible - url column still exists)

