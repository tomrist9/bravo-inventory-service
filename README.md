# Bravo Inventory Service

A backend service built for Bravo supermarkets to handle real-time inventory management and sales reporting. When a product is sold at the register, the stock drops immediately — and at the end of the day, you can pull a report of what sold the most.

---

## What it does

- **Bulk Sale API** — accepts multiple product sales at once (think: 10 registers firing at the same time) and reduces stock accordingly
- **Stock Management** — checks availability before selling, throws a proper error if something's out of stock or doesn't exist
- **Sales Report** — returns the top-selling products of the day, powered by a PostgreSQL stored procedure
- **Concurrency Safety** — uses pessimistic locking so two registers can't oversell the same product simultaneously

---

## Tech Stack

- Java 17 + Spring Boot 3.2.5
- PostgreSQL 16
- Flyway (database migrations)
- Hibernate / Spring Data JPA
- SimpleJdbcCall + REF_CURSOR (for stored procedure)
- Testcontainers + JUnit 5 + Mockito

---

## Architecture

The project follows Hexagonal (Clean) Architecture — business logic lives in the domain layer and has no idea about databases or HTTP. Adapters handle all the external stuff.
src/main/java/com/bravo/inventory/
├── application/ # Use cases and services
├── domain/ # Models, ports (interfaces)
└── infrastructure/ # Controllers, JPA entities, adapters

---

## Running locally

You'll need Docker running for the database.

```bash
docker-compose up -d
mvn spring-boot:run
```

The app starts on `http://localhost:8080`.

---

## API

### Bulk Sale

POST /api/v1/sales/bulk
```json
{
  "registerId": "KASSA-01",
  "lineItems": [
    { "productId": 1, "quantity": 2 },
    { "productId": 2, "quantity": 1 }
  ]
}
```

### Top Selling Products
GET /api/v1/reports/top-selling?limit=5

---

## Tests

```bash
mvn test
```

29 tests total — unit and integration. Integration tests spin up a real PostgreSQL instance via Testcontainers, so Docker needs to be running.
