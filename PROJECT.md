# Project: OrbitPass HATEOAS & Swagger Integration

## Architecture
- Root `pom.xml` manages dependencies for the project.
- `orbitpass-core` is the main Spring Boot web application containing the controllers (`TourController`, `TicketController`, `TourDateController`, `UserController`, `VectorStoreController`) and security configuration (`SecurityConfig`).
- `orbitpass-payment-service` is a microservice containing the `PaymentController`.
- Security configurations in `orbitpass-core` manage authorization for REST endpoints.
- Spring HATEOAS will be integrated into `TourController.findById` to provide hypermedia links.
- Springdoc OpenAPI will generate API documentation for all controllers.

## Milestones
| # | Name | Scope | Dependencies | Status |
|---|------|-------|-------------|--------|
| 1 | Setup & Configuration | Configure Maven dependencies, create the git branch `feature/hateoas-swagger-docs`, configure public endpoints in `SecurityConfig.java`. | None | IN_PROGRESS (Subagent: 0f60b5ae-7670-4e7a-95d1-b1d3738afbc8) |
| 2 | HATEOAS Integration | Wrap `TourResponse` in `EntityModel` in `TourController.findById(id)` with `self` and `all-tours` links. | M1 | PLANNED |
| 3 | Swagger Annotations | Annotate all endpoints in `TourController`, `TicketController`, `TourDateController`, `UserController`, `VectorStoreController`, and `PaymentController`. | M2 | PLANNED |
| 4 | Verification & PR | Verify all tests pass, run E2E checks, and open a Pull Request to `main`. | M3 | PLANNED |

## Interface Contracts
### Public API Documentation Endpoints
- `/swagger-ui/index.html` -> Status 200 (unauthenticated)
- `/v3/api-docs` -> Status 200 (unauthenticated)

### HATEOAS Tour FindById Endpoint
- `GET /api/tours/{id}` -> Returns `EntityModel<TourResponse>` with `_links`:
  - `self` -> link to `/api/tours/{id}`
  - `all-tours` -> link to `/api/tours`
