# Code Standards

Projektspezifische Coding Conventions für das Foodtruck Booking Service.

## Allgemeine Regeln

### Sprache
- Code: Englisch
- Kommentare: Englisch
- Commit-Messages: Englisch
- Dokumentation: Deutsch

### Formatierung
- Indentation: 4 Spaces (keine Tabs)
- Line Length: 120 Zeichen
- Encoding: UTF-8

## Java-Spezifisch

### Naming Conventions

**Packages**:
- Lowercase, keine Underscores
- Schema: `org.example.foodtruckbookingservice.[layer].[feature]`
- Beispiel: `org.example.foodtruckbookingservice.controller.booking`

**Classes**:
- PascalCase
- Aussagekräftige Namen
- Controller: `*Controller` (z.B. `BookingController`)
- Service: `*Service` (z.B. `BookingService`)
- Repository: `*Repository` (z.B. `BookingRepository`)
- DTO: `*DTO` oder `*Request`/`*Response`
- Entity: Keine Suffixe (z.B. `Booking`)

**Methods**:
- camelCase
- Verben für Aktionen: `createBooking()`, `findById()`
- Boolean-Getter: `is*()` oder `has*()` (z.B. `isConfirmed()`)

**Variables**:
- camelCase
- Aussagekräftige Namen (keine Abkürzungen außer `id`, `dto`)
- Constants: `UPPER_SNAKE_CASE`

**Test Classes**:
- Name der getesteten Klasse + `Test`
- Beispiel: `BookingServiceTest`

**Test Methods**:
- Schema: `methodName_scenario_expectedBehavior()`
- Beispiel: `createBooking_withValidData_returnsCreatedBooking()`

## Package-Struktur

```
org.example.foodtruckbookingservice
├── controller       # REST Controllers
├── service          # Business Logic
├── repository       # Data Access
├── entity           # JPA Entities
├── dto              # Data Transfer Objects
│   ├── request      # Request DTOs
│   └── response     # Response DTOs
├── exception        # Custom Exceptions
├── config           # Configuration Classes
└── util             # Utility Classes
```

## Annotations

### Controller
```java
@RestController
@RequestMapping("/api/v1/bookings")
@Validated
public class BookingController {
    // ...
}
```

### Service
```java
@Service
@Transactional(readOnly = true)
public class BookingService {

    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        // ...
    }
}
```

### Entity
```java
@Entity
@Table(name = "booking")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {
    // ...
}
```

### Repository
```java
@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    // Custom queries
}
```

## Lombok-Nutzung

**Erlaubt**:
- `@Data` für DTOs
- `@Builder` für DTOs und Entities
- `@NoArgsConstructor` / `@AllArgsConstructor`
- `@Slf4j` für Logging

**Verboten**:
- `@SneakyThrows`
- `@Cleanup`
- `@Synchronized`

## Exception Handling

### Custom Exceptions
```java
public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(UUID id) {
        super("Booking not found with id: " + id);
    }
}
```

### Global Exception Handler
```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(BookingNotFoundException ex) {
        // ...
    }
}
```

## Validation

### DTO Validation
```java
public class BookingRequest {

    @NotBlank(message = "Foodtruck name is required")
    @Size(max = 200, message = "Foodtruck name must not exceed 200 characters")
    private String foodtruckName;

    @NotBlank(message = "Customer name is required")
    @Size(max = 200)
    private String customerName;

    @NotNull(message = "Start time is required")
    @Future(message = "Start time must be in the future")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    @AssertTrue(message = "End time must be after start time")
    private boolean isValidTimeRange() {
        return endTime.isAfter(startTime);
    }
}
```

## Logging

### Log Levels
- `ERROR`: Fehler, die sofortige Aufmerksamkeit erfordern
- `WARN`: Potenzielle Probleme
- `INFO`: Wichtige Business-Events
- `DEBUG`: Detaillierte Informationen für Entwickler
- `TRACE`: Sehr detaillierte Informationen

### Logging-Beispiele
```java
@Slf4j
@Service
public class BookingService {

    public BookingResponse createBooking(BookingRequest request) {
        log.info("Creating booking for foodtruck: {}", request.getFoodtruckName());

        try {
            // Business logic
            log.debug("Booking created with id: {}", booking.getId());
            return response;
        } catch (Exception e) {
            log.error("Failed to create booking", e);
            throw e;
        }
    }
}
```

## Transaktionen

- `@Transactional(readOnly = true)` auf Service-Klasse
- `@Transactional` auf schreibenden Methoden
- Transaktionen NICHT in Controllern
- Keine verschachtelten Transaktionen ohne Grund

## Comments

### JavaDoc
Für öffentliche APIs:
```java
/**
 * Creates a new booking for a foodtruck.
 *
 * @param request the booking request containing all required information
 * @return the created booking with generated ID
 * @throws BookingConflictException if the foodtruck is already booked for the given time
 */
public BookingResponse createBooking(BookingRequest request) {
    // ...
}
```

### Inline Comments
Nur für komplexe Logik:
```java
// Check for overlapping bookings using interval logic
boolean hasOverlap = existingBookings.stream()
    .anyMatch(existing ->
        startTime.isBefore(existing.getEndTime()) &&
        endTime.isAfter(existing.getStartTime())
    );
```

### Keine Kommentare für Selbsterklärendes
```java
// SCHLECHT:
// Get the booking by ID
Booking booking = repository.findById(id);

// GUT (kein Kommentar nötig):
Booking booking = repository.findById(id);
```

## Testing

### Arrange-Act-Assert Pattern
```java
@Test
void createBooking_withValidData_returnsCreatedBooking() {
    // Arrange
    BookingRequest request = BookingRequest.builder()
        .foodtruckName("Test Truck")
        .customerName("John Doe")
        .startTime(LocalDateTime.now().plusDays(1))
        .endTime(LocalDateTime.now().plusDays(1).plusHours(2))
        .build();

    // Act
    BookingResponse response = bookingService.createBooking(request);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getFoodtruckName()).isEqualTo("Test Truck");
    assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);
}
```

### Test Naming
- Beschreibend und aussagekräftig
- Was wird getestet, unter welcher Bedingung, was ist das Ergebnis
- Keine `test*` Präfixe in Namen

## Constants

```java
public final class BookingConstants {

    public static final int MAX_FOODTRUCK_NAME_LENGTH = 200;
    public static final int MAX_CUSTOMER_NAME_LENGTH = 200;
    public static final String STATUS_PENDING = "PENDING";

    private BookingConstants() {
        throw new UnsupportedOperationException("Utility class");
    }
}
```

## Utility Classes

```java
public final class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static boolean isOverlapping(LocalDateTime start1, LocalDateTime end1,
                                       LocalDateTime start2, LocalDateTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }
}
```
