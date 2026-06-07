# Original User Request

## Initial Request — 2026-06-07T13:37:24-03:00

Create an agent team to implement HATEOAS and Swagger OpenAPI documentation:
- One teammate as the Lead Coordinator / Integration Agent to configure dependencies in root and module pom.xml files, update SecurityConfig.java in orbitpass-core, and coordinate final PR reviews.
- One teammate as the API Architect (HATEOAS & Swagger) to add HATEOAS support to TourController's findById endpoint (using EntityModel to wrap TourResponse record) and annotate all endpoints with simple, objective OpenAPI documentation.
- One teammate playing Devil's Advocate / QA to verify that Swagger UI and API docs endpoints are fully accessible without authentication, that HATEOAS links are properly formed, and that all Maven builds and tests compile successfully.

Working directory: `C:\Users\lucas\.gemini\antigravity\worktrees\orbitpass-root\execute-agent-orchestration-plan`
Integrity mode: development

## Requirements

### R1. Configure Maven Dependencies
Add `springdoc-openapi-starter-webmvc-ui` (version `2.8.5`) to the `<dependencyManagement>` in the root `pom.xml`, and as a dependency in `orbitpass-core/pom.xml` and `orbitpass-payment-service/pom.xml`. Add `spring-boot-starter-hateoas` to `orbitpass-core/pom.xml`.

### R2. Expose Swagger Endpoints in Security Config
Update `SecurityConfig.java` in `orbitpass-core` to permit public access (without authentication) to:
- `/swagger-ui/**`
- `/v3/api-docs/**`
- `/swagger-resources/**`
- `/webjars/**`

### R3. Integrate HATEOAS in Tour Controller
Wrap `TourResponse` in `EntityModel<TourResponse>` in `TourController.findById(id)`. Include:
- A `self` link pointing to the controller's `findById` endpoint.
- An `all-tours` link pointing to the controller's `findAll` endpoint.

### R4. Annotate Controllers with Swagger OpenAPI
Use `@Tag(name = "...", description = "...")` at the class level and `@Operation`, `@ApiResponse` at the endpoint level to document all endpoints in:
- `TourController`
- `TicketController`
- `TourDateController`
- `UserController`
- `VectorStoreController`
- `PaymentController`

### R5. Adhere to Git Flow Policies
Create all changes on a feature branch (e.g., `feature/hateoas-swagger-docs`) and open a Pull Request (PR) to `main`. Do not commit directly to `main`.

## Acceptance Criteria

### Build and Test
- [ ] Maven build compiles successfully (`mvn clean compile` passes).
- [ ] Existing automated tests run successfully (`mvn test` passes).

### Endpoints and Security
- [ ] Swagger UI at `/swagger-ui/index.html` is accessible without authentication.
- [ ] API Docs at `/v3/api-docs` are accessible without authentication.
- [ ] Other endpoints (e.g., `/api/users`, `/api/tickets/purchase`) remain secured.

### HATEOAS Link Verifications
- [ ] Requesting `GET /api/tours/{id}` returns a JSON with `_links` object containing `self` and `all-tours` relations.

### Git Version Control
- [ ] Feature branch is created and pushed.
- [ ] A Pull Request is successfully opened on GitHub.
