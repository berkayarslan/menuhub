# MenuHub Monorepo

Stack:
- Backend: Spring Boot 3.3, Java 21
- Frontend: Next.js 14
- DB: PostgreSQL
- Cache: Redis
- Queue: RabbitMQ
- Object Storage: MinIO



### initial
docker compose up -d postgres redis rabbitmq minio

### Backend
cd backend
mvn spring-boot:run

### Frontend
cd frontend
npm install
npm run dev
