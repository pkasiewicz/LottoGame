# Lotto Game 🎰

Backend application for a lottery system where users submit 6 numbers (1-99) and check results after Saturday draws at 12:00 PM.

## Architecture

Built with **Hexagonal Architecture** (Ports & Adapters) with 4 independent modules:

```
┌─────────────────────┐
│  Number Receiver    │ ← User submits 6 numbers
└──────────┬──────────┘
           │
           ↓ (stored in DB)
┌─────────────────────┐
│ Winning Numbers     │ ← Generates winning numbers (Sat 11:55 AM)
│    Generator        │
└──────────┬──────────┘
           │
           ↓ (both ready at 12:00 PM)
┌─────────────────────┐
│  Result Checker     │ ← Compares tickets vs winning numbers
└──────────┬──────────┘
           │
           ↓ (results stored)
┌─────────────────────┐
│ Result Announcer    │ ← User checks their ticket result
└─────────────────────┘
```

Each module follows hexagonal architecture:
- **Domain** - business logic (use cases, entities)
- **Application** - facades, validators, configuration
- **Infrastructure** - JPA repositories, REST controllers, schedulers

## Tech Stack

- **Java 21**
- **Spring Boot 3.5.6**
- **PostgreSQL** (with Flyway migrations)
- **Spring Data JPA**
- **Spring Scheduler** (automated draws)
- **Bean Validation**
- **Lombok**
- **Springdoc OpenAPI** (Swagger UI)
- **Testcontainers** (integration tests)
- **WireMock** (API mocking)

*(Spring Security planned for future releases)*

## Prerequisites

- Java 21+
- Maven 3.x
- PostgreSQL (or Docker)
- Docker (optional, for containerized DB)

## Installation

```bash
git clone https://github.com/yourusername/lottogame.git
cd lottogame
mvn clean install
```

## Configuration

`src/main/resources/application.yml`:

```yaml
spring:
   datasource:
      url: jdbc:postgresql://${DB_HOST:localhost}:${DB_PORT:5432}/${DB_NAME:lotto_db}
      username: ${DB_USERNAME:admin}
      password: ${DB_PASSWORD:admin}  # override with env variable in production

lotto:
   number-generator:
      count: 6
      lowerBand: 1
      upperBand: 99
   number-receiver:
      count: 6
      lower-band: 1
      upper-band: 99
```

## Running

```bash
mvn spring-boot:run
```

Application runs on `http://localhost:8084`

## API Endpoints

### Submit Numbers
```http
POST /api/tickets
Content-Type: application/json

{
  "numbers": [1, 15, 23, 45, 67, 89]
}
```

**Response:**
```json
{
  "ticketId": "550e8400-e29b-41d4-a716-446655440000",
  "numbers": [1, 15, 23, 45, 67, 89],
  "drawDate": "2025-12-20T12:00:00"
}
```

### Check Result
```http
GET /api/results/{ticketId}
```

**Response:**
```json
{
  "status": "WIN_MESSAGE",
  "result": {
    "ticketId": "550e8400-e29b-41d4-a716-446655440000",
    "userNumbers": [1, 15, 23, 45, 67, 89],
    "wonNumbers": [1, 15, 23, 45, 67, 99],
    "hitNumbers": [1, 15, 23, 45, 67],
    "hitCount": 5,
    "drawDate": "2025-12-20T12:00:00",
    "isWinner": true
  }
}
```

**Possible statuses:**
- `WIN_MESSAGE` / `LOSE_MESSAGE` - draw completed
- `WAITING_FOR_DRAW` - results not ready yet (check after Saturday 12:00 PM)
- `ALREADY_CHECKED` - result was retrieved before
- `TICKET_NOT_FOUND` - invalid ticket ID

## Swagger UI

Interactive API documentation:
```
http://localhost:8084/swagger-ui.html
```

## How It Works

1. **User submits numbers** via POST `/api/tickets`
    - Numbers validated (6 unique, range 1-99)
    - Ticket assigned to next Saturday's draw

2. **Winning numbers generated** (Saturday 11:55 AM)
    - Fetched from external API: `https://www.randomnumberapi.com/api/v1.0/random`
    - Fallback: internal random generator
    - Validated (6 unique, range 1-99)

3. **Results calculated** (Saturday 12:00 PM)
    - Scheduler compares all tickets vs winning numbers
    - Winners determined (3 or more matching numbers)
    - Results cached to avoid duplicate processing

4. **User checks result** via GET `/api/results/{ticketId}`
    - Returns match details, hit count, win/lose status
    - Results cached after first check

## Project Structure

```
src/main/java/pl/pkasiewicz/lottogame/
├── domain/                  # Cross-cutting ports
│   └── port/                # DrawDateGenerable, IdGenerable, WinningNumbersProvider
├── infrastructure/          # Cross-cutting adapters
│   ├── adapter/             # DrawDateGenerator, IdGenerator, WinningNumbersProviderAdapter
│   └── api/error/           # GlobalExceptionHandler
│
├── numberreceiver/
│   ├── domain/
│   │   └── port/            # NumberReceiverUseCase, TicketRepository
│   ├── application/         # NumberReceiverFacade
│   └── infrastructure/
│       └── api/             # REST controller
│
├── numbergenerator/
│   ├── domain/
│   │   └── port/            # WinningNumbersGeneratorUseCase, WinningNumbersRepository
│   ├── application/         # WinningNumbersGeneratorFacade
│   └── infrastructure/
│       ├── http/            # External API adapter
│       └── scheduler/       # Saturday 11:55 AM cron
│
├── resultchecker/
│   ├── domain/
│   │   └── port/            # ResultCheckerUseCase, TicketProvider, TicketResultRepository
│   ├── application/         # ResultCheckerFacade
│   └── infrastructure/
│       ├── adapter/         # TicketProviderAdapter, TicketResultRepositoryAdapter
│       └── scheduler/       # Saturday 12:00 PM cron
│
└── resultannouncer/
    ├── domain/
    │   └── port/            # ResultAnnouncerUseCase, TicketExistenceChecker, TicketResultProvider
    ├── application/         # ResultAnnouncerFacade
    └── infrastructure/
        ├── adapter/         # TicketExistenceCheckerAdapter, TicketResultProviderAdapter
        └── api/             # REST controller
```

## Testing

```bash
# Run all tests
mvn test

# Run integration tests with Testcontainers
mvn verify
```

Tests use:
- Testcontainers (PostgreSQL)
- WireMock (external API mocking)
- AssertJ (fluent assertions)

## Database Migrations

Flyway migrations in `src/main/resources/db/migration/`:
- Automatic on startup
- Version-controlled schema changes

## Future Plans

- [ ] Add Spring Security (JWT authentication)
- [ ] Migrate to microservices architecture
- [ ] Add user accounts and ticket history
- [ ] Prize tier system (3+ matches = smaller prizes)

## Author

Piotr Kasiewicz - [GitHub](https://github.com/pkasiewicz)