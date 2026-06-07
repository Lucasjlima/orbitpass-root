# Test Infrastructure Specification — OrbitPass E2E Test Suite

## Test Philosophy
This test suite validates the security configuration, whitelisting behavior, and HATEOAS integration of the OrbitPass core microservice. 
Unlike integration tests that rely on external infrastructure (PostgreSQL, RabbitMQ, Spring AI API keys), these E2E tests are implemented as high-level API tests using `@SpringBootTest` and `@AutoConfigureMockMvc`.
By mocking service and messaging layers using `@MockitoBean` (or `@MockBean`), we verify endpoint definitions, serialization/deserialization, validation filters, security policies, and hypermedia integration in a fast, deterministic, and container-free execution environment.

## Feature Inventory & Scope
The E2E test suite targets three core features:
1. **Feature 1: Swagger UI & API Docs Accessibility**: Verify that API documentation and Swagger UI endpoints (`/swagger-ui/**`, `/v3/api-docs/**`, `/swagger-resources/**`, `/webjars/**`) are public, whitelisted, and accessible without credentials.
2. **Feature 2: API Endpoints Security**: Ensure that all non-whitelisted endpoints remain properly secured under authentication policies, returning `401 Unauthorized` for anonymous requests or `403 Forbidden` for role-based authorization failures (e.g. non-admin accessing admin-only paths).
3. **Feature 3: HATEOAS Hypermedia Links**: Check that retrieval of specific tours via `GET /api/tours/{id}` returns a JSON payload augmented with HATEOAS links, specifically including `self` (linking to the tour resource itself) and `all-tours` (linking to the list of all tours).

## Test Architecture
- **Framework**: JUnit 5, Spring Boot Test (`@SpringBootTest` with `@AutoConfigureMockMvc`)
- **HTTP Client**: `MockMvc`
- **Database Context**: Bootstrapped using an in-memory H2 database (with schema generation disabled or configured minimally to avoid PgVector constraints).
- **Service & Integrations Mocking**: All heavy backing services (`TourService`, `TicketService`, `TourDateService`, `UserService`, `VectorStoreService`, `ChatModel`, etc.) and JPA/RabbitMQ publishers are mocked using `@MockitoBean` or `@MockBean` to prevent side effects.

### Directory Layout
```
orbitpass-core/src/test/java/fiap/com/br/orbitpasscore/e2e/
├── SecurityE2ETest.java  # Feature 1 (Swagger/Docs whitelist) & Feature 2 (API Security)
├── HateoasE2ETest.java  # Feature 3 (HATEOAS tour links & related paths)
└── ScenariosE2ETest.java # Tier 3 & Tier 4 (Cross-feature interactions and user journeys)
```

## 4-Tier Test Plan

### Tier 1: Feature Coverage (Happy Path) — 15+ Tests (5 per feature)
- **Feature 1: Swagger UI & API Docs Accessibility**
  - Verify `/v3/api-docs` returns Swagger docs (JSON) with `200 OK`.
  - Verify `/swagger-ui/index.html` returns Swagger UI page with `200 OK`.
  - Verify `/swagger-resources` returns `200 OK`.
  - Verify `/webjars/swagger-ui/index.css` returns `200 OK`.
  - Verify `/v3/api-docs/swagger-config` returns config JSON with `200 OK`.
- **Feature 2: API Endpoints Security**
  - Verify `POST /api/users/login` is public and responds correctly (e.g. `400` or `200` depending on mock).
  - Verify `POST /api/users` (registration) is public and responds correctly.
  - Verify `GET /api/users` (Admin only) blocks anonymous users with `401 Unauthorized`.
  - Verify `GET /api/users` (Admin only) blocks default authenticated users with `403 Forbidden`.
  - Verify `POST /api/tours` (Admin only) blocks default authenticated users with `403 Forbidden`.
- **Feature 3: HATEOAS Hypermedia Links**
  - Verify `GET /api/tours/{id}` returns the tour object with `self` link.
  - Verify `GET /api/tours/{id}` returns the tour object with `all-tours` link.
  - Verify link URLs match the expected patterns (`/api/tours/{id}`).
  - Verify metadata / fields of the tour are correctly serialized alongside the HATEOAS links.
  - Verify `GET /api/tours/{id}` with a mock admin token displays the same HATEOAS links.

### Tier 2: Boundary & Corner Cases — 15+ Tests (5 per feature)
- **Feature 1: Swagger UI & API Docs Accessibility**
  - Access `/swagger-ui/` without trailing slash or path parameters to check redirection.
  - Request API docs with headers requesting XML/non-supported media types.
  - Request non-existent webjars assets (e.g., `/webjars/non-existent.js`) - expect `404 Not Found` (but not `401`/`403`).
  - Access Swagger config with trailing slashes `/v3/api-docs/swagger-config/`.
  - Access swagger resources with invalid query params - expect response with `200 OK` or `400/404` depending on mapping, not security blocks.
- **Feature 2: API Endpoints Security**
  - Access `/api/users` with an expired/invalid JWT token - expect `401 Unauthorized`.
  - Access `/api/users` with a malformed JWT token header (e.g. `Bearer 12345`) - expect `401 Unauthorized`.
  - Register a new user with invalid credentials / validation errors - verify it rejects with `400 Bad Request` but does not block on security context.
  - Try to access `/api/vector-store/seed` (Admin-only) with a default user token - expect `403 Forbidden`.
  - Try to access a non-existent secure endpoint (e.g. `/api/tours/non-existent/action`) as anonymous - expect `401 Unauthorized` before `404 Not Found`.
- **Feature 3: HATEOAS Hypermedia Links**
  - Fetch a non-existent tour ID (`GET /api/tours/999`) - expect `404 Not Found` handled by the global exception handler without HATEOAS links.
  - Fetch a tour with boundary/extreme ID values (e.g., `GET /api/tours/-1` or `GET /api/tours/999999999999`) and confirm correct HTTP response code.
  - Request HATEOAS links with custom `Accept` headers (e.g. `application/hal+json`) to confirm HAL representation support.
  - Confirm the HATEOAS links format matches `_links.self.href` and `_links.all-tours.href` standard structure.
  - Verify that the HATEOAS `self` link dynamically maps the requested host/port.

### Tier 3: Cross-Feature Combinations — 3+ Tests
- **Test 3.1: Swagger Access and Secure Execution Linkage**
  - Verify that retrieving Swagger API docs dynamically documents the security requirements (JWT Bearer) and matches actual security mappings verified in the security tests.
- **Test 3.2: User Authenticated Journey to HATEOAS Resource**
  - Authenticate a User -> Use JWT to retrieve a Tour -> Verify the HATEOAS `self` link matches the URL used to access the tour.
- **Test 3.3: Admin Role Propagation to HATEOAS endpoint**
  - Authenticate an Admin -> Verify access to secure tour update -> Verify that the returned HATEOAS self-link points to the same controller that is guarded under Admin/User role filters.

### Tier 4: Real-World Application Scenarios — 5+ Tests
- **Scenario 1: Public Explorer to Customer Sign-up and Documentation Search**
  - Simulates a developer reading the API docs (`/v3/api-docs`), then registering a user, logging in, and retrieving a specific tour with HATEOAS links.
- **Scenario 2: Privilege Escalation Attempt**
  - Simulates a user logging in as a default user and attempting administrative tasks (updating a tour, seeding vector store) and verifying failure, followed by an admin logging in and successfully performing the task.
- **Scenario 3: Complete Secure Tour & Booking Workflow**
  - Simulates registering a user, logging in, attempting to book a ticket for a tour date, validating security and service calls.
- **Scenario 4: Security Audit on Invalid Token & Authentication Expiration**
  - Simulates a client sending expired, malformed, or fake tokens across public and private endpoints.
- **Scenario 5: Interactive Chatbot & Service Integration**
  - Simulates an authenticated user checking chat assistant endpoint `/api/vector-store/chat` and verifying appropriate responses and restrictions.
