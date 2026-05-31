# MultiPost

MultiPost is a full-stack multi-platform content publishing system. It manages original drafts, adapts them into platform-specific versions, creates publish batches, and tracks publish task execution across multiple target platforms.

The current implementation focuses on the engineering workflow around publishing: authentication, draft versioning, platform adaptation, reliable task dispatch, retry handling, platform capability modeling, and scheduled publishing. Real platform publishing is represented by a mock publisher so the core backend flow can be tested without depending on third-party platform approvals.

## Features

- JWT-based registration and login.
- User-isolated draft management with soft delete and version increments.
- Platform-specific content adaptation for WeChat, Zhihu, Bilibili, and Xiaohongshu.
- Manual editing of adapted platform content before publishing.
- Publish batch creation with one publish task per platform.
- Idempotent publish request handling through `requestId`.
- Outbox-based reliable dispatch for publish tasks.
- Optional RabbitMQ dispatch path for asynchronous execution.
- Automatic retry for transient mock publish failures.
- Per-task status tracking and aggregate batch status calculation.
- Platform capability matrix describing publish mode, auth type, schedule support, status query support, and media upload support.
- Scheduled publish support for platforms that declare `supportsSchedule`.
- React frontend for content editing, adaptation, publish control, and task status tracking.
- OpenAPI UI through Springdoc.

## Tech Stack

Backend:

- Java 17
- Spring Boot 3.3
- Spring Web, Spring Security, Spring Data JPA, Validation
- H2 for local development
- MySQL for Docker profile
- RabbitMQ for optional async task dispatch
- Redis dependency reserved for infrastructure support
- Springdoc OpenAPI

Frontend:

- React 18
- TypeScript
- Vite
- Ant Design
- Axios
- Lucide React

Infrastructure:

- Docker multi-stage build
- Docker Compose with app, MySQL, Redis, and RabbitMQ

## Project Structure

```text
src/main/java/com/example/multipost
  auth/        JWT auth, registration, login, current-user resolution
  content/     original draft CRUD and versioning
  adapter/     platform adaptation, validation, and adapted content storage
  platform/    platform enum and capability matrix
  publish/     publish batches, tasks, outbox, retry, scheduling
  config/      security, OpenAPI, RabbitMQ, SPA routing

frontend/src
  api/         typed API clients
  components/ shared UI components
  pages/      dashboard, login, content, adaptation, publish pages
  styles/     global styling
```

## Local Development

### Backend

The default profile uses an in-memory H2 database and disables RabbitMQ dispatch.

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

Useful URLs:

- App/API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- H2 console: `http://localhost:8080/h2-console`
- Health check: `http://localhost:8080/actuator/health`

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server runs separately from the Spring Boot backend. For production packaging, the Dockerfile builds the frontend and copies the generated static files into the Spring Boot application.

## Docker Compose

Run the full stack with MySQL, Redis, and RabbitMQ:

```bash
docker compose up --build
```

The Docker profile enables RabbitMQ dispatch:

```yaml
multipost:
  rabbit:
    enabled: true
```

RabbitMQ management UI is exposed at:

```text
http://localhost:15672
```

Default credentials:

```text
username: multipost
password: multipost
```

For production-like use, replace `MULTIPOST_JWT_SECRET` in `compose.yaml` with a long random secret.

## Testing

Run backend tests:

```bash
./mvnw test
```

On Windows PowerShell:

```powershell
.\mvnw.cmd test
```

Run frontend type-check and production build:

```bash
cd frontend
npm run build
```

Current test coverage includes:

- Platform adapter formatting and validation.
- Register/login/content/adapt/publish happy path.
- User isolation.
- Transient publish failure retry.
- Platform capability matrix response.
- Scheduled publish creation.
- Rejection of scheduled publish for unsupported platforms.

## Key Backend Flows

### Content Adaptation

Original content is stored once and converted into platform-specific `PlatformContent` records. Each adapted record is tied to the source content version, so publishing always targets the generated version for the selected draft.

### Reliable Publish Dispatch

Publish batch creation writes tasks and outbox events in the database transaction. After commit, the outbox processor dispatches due events. This avoids losing publish tasks when the request succeeds but async dispatch fails.

### Retry Handling

The execution service claims a task, runs the publisher, and retries transient failures up to the configured limit:

```yaml
multipost:
  retry:
    max-attempts: 3
```

### Scheduled Publish

When `scheduledAt` is in the future, the batch and tasks are marked `SCHEDULED`. Outbox events use `nextRetryAt` as their due time and are dispatched only after the scheduled timestamp.

The service validates scheduled publishes against the platform capability matrix. Platforms without `supportsSchedule` are rejected before tasks are created.

## Platform Capability Matrix

The capability matrix is the source of truth for platform integration behavior. It describes:

- publish mode: `API`, `MANUAL`, `CLIENT_ASSISTED`, or `MOCK`
- auth type: `APP_SECRET`, `OAUTH2`, `USER_CONFIRMATION`, or `NONE`
- schedule support
- status query support
- media upload support
- integration notes

This keeps platform-specific constraints out of the UI and publish workflow logic.

## API Overview

Auth:

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/auth/me`

Content:

- `POST /api/contents`
- `GET /api/contents`
- `GET /api/contents/{id}`
- `PUT /api/contents/{id}`
- `DELETE /api/contents/{id}`

Adaptation:

- `POST /api/contents/{contentId}/adapt`
- `GET /api/contents/{contentId}/platform-contents`
- `PUT /api/platform-contents/{id}`

Platform capabilities:

- `GET /api/platform-capabilities`

Publishing:

- `POST /api/publish/batches`
- `GET /api/publish/batches/{id}`
- `GET /api/publish/tasks/{id}`
- `POST /api/publish/tasks/{id}/retry`
