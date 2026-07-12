# AssetFlow — Enterprise Asset & Resource Management System

Centralized ERP-style platform for tracking, allocating, and maintaining
physical assets and shared resources across departments.

## Stack

- **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Spring Security (JWT), PostgreSQL, Maven
- **Frontend:** React 19 (Vite), React Router, TanStack Query, Axios

## Repository structure

```
assetflow/
  backend/     Spring Boot API (layered: controller -> service -> repository -> entity)
  frontend/    React SPA (Vite)
```

## Backend package layout

```
com.assetflow
  config/       Spring configuration (security, CORS, etc.)
  security/     JWT filter, auth entry points
  controller/   REST endpoints
  service/      Business logic
  repository/   Spring Data JPA repositories
  entity/       JPA entities
  dto/          Request/response DTOs (entities are never exposed directly)
  exception/    Custom exceptions + global exception handler
```

## Frontend structure

```
src/
  api/          Axios client + per-resource API modules
  components/   Reusable UI (layout/, common/)
  context/      React context providers (auth, etc.)
  hooks/        Custom hooks
  pages/        Route-level screens
  routes/       Route definitions + route guards
  styles/       Global styles
```

## Local setup

### Prerequisites
- Java 17+, Maven 3.9+
- Node 18+
- PostgreSQL running locally

### Database
Create a local database (defaults assume `assetflow` / user `postgres`):

```sql
CREATE DATABASE assetflow;
```

Override credentials via environment variables if needed:
`DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.

### Backend

```bash
cd backend
mvn spring-boot:run
```
Runs on `http://localhost:8080/api`.

### Frontend

```bash
cd frontend
cp .env.example .env
npm install
npm run dev
```
Runs on `http://localhost:5173`.

## Development phases

This project is built in five phases, each a fully working, demo-ready
increment:

1. **Foundation** — auth, departments, employee directory, dashboard skeleton
2. **Asset catalog** — categories, asset registration, lifecycle states
3. **Operations** — allocation, transfers, returns, resource booking
4. **Workflows** — maintenance approvals, audit cycles, notifications, activity logs
5. **Insights & ship** — reports/analytics, KPI dashboard, optimization, polish, deployment

## Roles

`ADMIN`, `ASSET_MANAGER`, `DEPARTMENT_HEAD`, `EMPLOYEE`. Signup always
creates an `EMPLOYEE` account — elevated roles are assigned only by an
Admin from the Employee Directory.
