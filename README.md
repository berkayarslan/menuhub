# MenuHub Backend

REST API for text-based restaurant menu & pricing discovery platform.

## Stack
- **Backend**: Spring Boot 3.3, Java 21
- **Database**: PostgreSQL
- **Cache**: Redis
- **Queue**: RabbitMQ
- **Object Storage**: MinIO
- **API Port**: 8080

## Quick Start

### 1. Start Infrastructure
```bash
docker compose up -d postgres redis rabbitmq minio
```

### 2. Run Backend
**Option A: IntelliJ IDE (Recommended)**
- Click green Run button in IDE

**Option B: Maven**
```bash
cd backend
mvn spring-boot:run
```

Backend will start on `http://localhost:8080`

### 3. Frontend (Separate Repository)
Frontend is maintained in a separate repository.  
See: https://github.com/berkayarslan/menuhub-frontend

## Documentation
- `AGENTS.md` - AI agent development guide
- `PROJECT_CONTEXT.md` - Turkish project details
- `backend/` - Spring Boot application

## API Endpoints
- Public: `GET /api/restaurants`, `GET /api/restaurants/{id}`, `GET /api/restaurants/{id}/menu-items`
- Admin: `POST /api/admin/**` (JWT protected)
- Auth: `POST /api/auth/login`

## Database
- Auto-migrations via Hibernate DDL-auto: update
- Seed data loaded on first startup

## Admin Credentials
- Username: `admin`
- Password: `admin123`
- JWT Secret: `12345678901234567890123456789012`
