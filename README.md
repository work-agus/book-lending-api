# Book Lending API

## Summary
DemandLane Book Lending API is a RESTful backend service built with Spring Boot 3 for managing a library book lending system. It provides a complete set of endpoints to manage books, members, and loan transactions, including borrowing and returning books with business rule enforcement such as maximum loan limits, overdue book checks, and copy availability tracking.

## Security
The API is secured using **JWT-based** stateless authentication, with role-based access control managed through Spring Security

## Database
Data is persisted in PostgreSQL using **JPA/Hibernate**, with **Flyway** handling database schema migrations reliably across environments. All write operations are wrapped in **transactional** boundaries to ensure data consistency.

## Testing
The project includes unit tests for all service layers using JUnit 5 and Mockito, covering success paths, validation failures, and edge cases across **AuthService** , **BookService**, **MemberService**, and **LoanService**.

## Architecture
The service follows a clean layered architecture (Controller → Service → Repository) with **soft-delete** support, **automatic auditing via an
Auditable**
base entity, and full API documentation exposed through **Swagger/OpenAPI** (Springdoc).

## Tech Stack
- **Language**: Java 17
- **Framework**: Spring Boot 3
- **Security**: Spring Security, JWT (jjwt)
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA, Hibernate
- **Migration**: Flyway
- **API Documentation**: Springdoc OpenAPI (Swagger UI)
- **Build Tool**: Maven
- **Utilities**: Lombok, uuid-creator
- **Testing**: JUnit 5, Mockito

## Links
- API Documentation: `/swagger-ui/index.html`
- Health Check: `/actuator/health`
- Metrics: `/actuator/metrics`
- Prometheus Metrics: `/actuator/prometheus`

## How to Run
1. Clone the repository:
    ```bash 
    git clone 
    ```
2. Navigate to the project directory:
    ```bash
    cd book-lending-api
    ```
3. Build the project using docker compose:
    ```bash
    docker-compose up --build
    ```
4. Access the API documentation at `http://localhost:9090/swagger-ui/index.html`

## Disclaimer
*I developed this project without using AI tools to generate code, so all the code in this repository is the result of my own hard work. I am committed to maintaining the integrity and authenticity of the code I write, and ensuring that every line of code reflects my understanding and skills in software development, particularly in Java technology.*