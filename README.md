# Book Lending API

## Summary
DemandLane Book Lending API is a RESTful backend service built with Spring Boot 3 for managing a library book lending system. It provides a complete set of endpoints to manage books, members, and loan transactions — including borrowing and returning books with business rule enforcement such as maximum loan limits, overdue book checks, and copy availability tracking.

## Security
The API is secured using JWT-based stateless authentication, with role-based access control managed through Spring Security

## Database
Data is persisted in PostgreSQL using JPA/Hibernate, with Flyway handling database schema migrations reliably across environments. All write operations are wrapped in transactional boundaries to ensure data consistency.

## Testing
The project includes unit tests for all service layers using JUnit 5 and Mockito, covering success paths, validation failures, and edge cases across **AuthService** , **BookService**, **MemberService**, and **LoanService**.

## Architecture
The service follows a clean layered architecture (Controller → Service → Repository) with soft-delete support, automatic auditing via an
Auditable
base entity, and full API documentation exposed through Swagger/OpenAPI (Springdoc).

