# E2E Test Suite Status & Execution Guide — OrbitPass

This document details the status, coverage, and execution instructions for the OrbitPass E2E Test Suite.

## Test Runner Execution

To execute the E2E test suite, run the following command from the project root:

```bash
mvn test -pl orbitpass-core -Dtest=*E2ETest
```

For a clean compile and test run, use:

```bash
mvn clean test -pl orbitpass-core -Dtest=*E2ETest
```

## Test Inventory Summary

The E2E test suite contains **38 tests** distributed across 4 tiers:

### 1. Feature Coverage (Tier 1) — 15 Tests
*   **Feature 1: Swagger UI & API Docs Whitelist Accessibility (5 tests)**
    *   `swaggerUiIndex_returnsSuccessWithoutAuth`
    *   `v3ApiDocs_returnsSuccessWithoutAuth`
    *   `v3ApiDocsSwaggerConfig_returnsSuccessWithoutAuth`
    *   `swaggerResources_returnsSuccessWithoutAuth`
    *   `webjarsSwaggerUi_returnsSuccessWithoutAuth`
*   **Feature 2: API Endpoints Security (5 tests)**
    *   `getUsers_anonymous_returnsUnauthorized`
    *   `getUsers_defaultUser_returnsForbidden`
    *   `createTour_anonymous_returnsUnauthorized`
    *   `createTour_defaultUser_returnsForbidden`
    *   `seedVectorStore_defaultUser_returnsForbidden`
*   **Feature 3: HATEOAS Hypermedia Links (5 tests)**
    *   `getTourById_returnsSelfLink`
    *   `getTourById_returnsAllToursLink`
    *   `getTourById_returnsCorrectContent`
    *   `getTourById_admin_returnsHateoasLinks`
    *   `getTourById_defaultUser_returnsHateoasLinks`

### 2. Boundary & Corner Cases (Tier 2) — 15 Tests
*   **Feature 1: Swagger UI & API Docs Whitelist Accessibility (5 tests)**
    *   `swaggerUiRoot_returnsRedirectOrSuccessWithoutAuth`
    *   `v3ApiDocsWithMethodPost_returnsMethodNotAllowedWithoutAuth`
    *   `swaggerResourcesWithMethodPost_returnsMethodNotAllowedWithoutAuth`
    *   `webjarsNonExistentResource_returnsNotFoundWithoutAuth`
    *   `v3ApiDocsSwaggerConfigWithTrailingSlash_returnsNotFoundWithoutAuth`
*   **Feature 2: API Endpoints Security (5 tests)**
    *   `getUserProfile_accessOtherUser_returnsForbidden`
    *   `invalidToken_returnsUnauthorized`
    *   `malformedToken_returnsUnauthorized`
    *   `nonExistentEndpoint_anonymous_returnsUnauthorized`
    *   `securedPostWithValidationErrors_anonymous_returnsUnauthorized`
*   **Feature 3: HATEOAS Hypermedia Links (5 tests)**
    *   `getTourById_nonExistentTour_returnsNotFoundWithoutHateoasLinks`
    *   `getTourById_invalidIdFormat_returnsBadRequestWithoutHateoasLinks`
    *   `getTourById_extremeIdValue_returnsNotFoundWithoutHateoasLinks`
    *   `getTourById_customHalAcceptHeader_returnsHalJsonMediaType`
    *   `getTourById_dynamicHostMapping_returnsHateoasLinksWithDynamicPort`

### 3. Cross-Feature Combinations (Tier 3) — 3 Tests
*   `swaggerAccessAndSecureExecutionLinkage` (Tests Feature 1 docs vs Feature 2 access control blocks)
*   `userAuthenticatedJourneyToHateoasResource` (Tests Feature 2 authentication path leading to Feature 3 HATEOAS lookup)
*   `adminRolePropagationToHateoasEndpoint` (Tests Feature 2 administrator authorization role matched with Feature 3 HATEOAS links)

### 4. Real-World Application Scenarios (Tier 4) — 5 Tests
*   `scenario1_happyPathUserJourney` (Sign-up new user, list tours, retrieve tour details checking HATEOAS self/all-tours links, purchase ticket)
*   `scenario2_privilegeEscalationAttempt` (Verify user cannot call admin endpoints, verify admin successfully creates tour and seeds vector store)
*   `scenario3_asyncTicketPaymentFlow` (Book pending ticket, push simulated payment outcome on rabbitmq listener, verify ticket transitions state)
*   `scenario4_concurrentSeatBookingConflict` (Verify correct propagation of `InsufficientSpotsException` and HTTP Conflict return code)
*   `scenario5_interactiveChatbotServiceIntegration` (Check secured `/api/vector-store/chat` integration and chat message mapping)

## Current Status & Verification

As of the current design-first phase, the application compiles and launches context successfully. The test suite execution results in:
*   **Tests Executed**: 38
*   **Passed**: 29
*   **Failed**: 9

### Reason for Expected Failures
The 9 failing tests are related directly to HATEOAS hypermedia links (`_links.self` and `_links['all-tours']`) on the `GET /api/tours/{id}` endpoint. These assertions are expected to fail because the HATEOAS hypermedia links implementation track is not yet completed in the main codebase (`TourController`). All security, validation, exception handling, and custom business scenarios compile and pass successfully.
