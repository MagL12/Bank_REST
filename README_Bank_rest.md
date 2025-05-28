# Система управления банковскими картами

Описать данный файл по ТЗ

# Bankcards Application

## Prerequisites
- Docker and Docker Compose
- Java 17
- Maven

## Setup
1. Clone the repository:
   git clone <your-repo-url>
   cd bankcards</your-repo-url>

text

Копировать

2. Create a `.env` file in the root directory with the following content:
   APPLICATION_SECURITY_ENCRYPTION_SECRET_KEY=<your-32-byte-key>
   SECURITY_JWT_SECRET_KEY=<your-64-byte-key></your-64-byte-key></your-32-byte-key>

text

Копировать
Generate secure keys using OpenSSL:
openssl rand -base64 32  # For APPLICATION_SECURITY_ENCRYPTION_SECRET_KEY
openssl rand -base64 64  # For SECURITY_JWT_SECRET_KEY

text

Копировать

3. Build and run the application with Docker Compose:
   docker-compose up --build

text

Копировать

4. Access the application:
- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`

## Notes
- The `.env` file is ignored by `.gitignore` to keep sensitive keys secure.
- Ensure the `.env` file is created before running the application.