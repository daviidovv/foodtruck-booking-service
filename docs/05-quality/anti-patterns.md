# Anti-Patterns

Dokumentierte Fehler und Probleme, die in diesem Projekt NICHT wiederholt werden dürfen.

## Allgemeine Anti-Patterns

### God Classes
**Problem**: Klassen mit zu vielen Verantwortlichkeiten (> 500 Zeilen).
**Lösung**: Aufteilen in kleinere, fokussierte Klassen.
**Regel**: Eine Klasse = eine Verantwortlichkeit (Single Responsibility Principle).

### Magic Numbers
**Problem**: Zahlen ohne Kontext im Code.
```java
// SCHLECHT:
if (bookings.size() > 100) { ... }

// GUT:
private static final int MAX_BOOKINGS_PER_FOODTRUCK = 100;
if (bookings.size() > MAX_BOOKINGS_PER_FOODTRUCK) { ... }
```

### Hardcoded Values
**Problem**: Konfigurationswerte direkt im Code.
```java
// SCHLECHT:
String dbUrl = "jdbc:postgresql://localhost:5432/foodtruck";

// GUT:
@Value("${spring.datasource.url}")
private String dbUrl;
```

### String Concatenation in Loops
**Problem**: Performance-Problem bei vielen Iterationen.
```java
// SCHLECHT:
String result = "";
for (Booking b : bookings) {
    result += b.toString();
}

// GUT:
StringBuilder result = new StringBuilder();
for (Booking b : bookings) {
    result.append(b.toString());
}
```

## Spring Boot Anti-Patterns

### Field Injection
**Problem**: Schwer testbar, versteckte Dependencies.
```java
// SCHLECHT:
@Autowired
private BookingRepository repository;

// GUT:
private final BookingRepository repository;

@Autowired
public BookingService(BookingRepository repository) {
    this.repository = repository;
}
```

### Business Logic in Controllers
**Problem**: Controller sollten nur Koordination, keine Logik.
```java
// SCHLECHT:
@PostMapping
public ResponseEntity<BookingResponse> create(@RequestBody BookingRequest request) {
    if (request.getEndTime().isBefore(request.getStartTime())) {
        throw new IllegalArgumentException("Invalid time range");
    }
    Booking booking = new Booking();
    // ... mehr Logik
}

// GUT:
@PostMapping
public ResponseEntity<BookingResponse> create(@Valid @RequestBody BookingRequest request) {
    BookingResponse response = bookingService.createBooking(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### Entities als DTOs
**Problem**: Interne Struktur wird nach außen exponiert.
```java
// SCHLECHT:
@GetMapping("/{id}")
public Booking getBooking(@PathVariable UUID id) {
    return bookingRepository.findById(id).orElseThrow();
}

// GUT:
@GetMapping("/{id}")
public BookingResponse getBooking(@PathVariable UUID id) {
    return bookingService.getBookingById(id);
}
```

### Missing Transactions
**Problem**: Schreiboperationen ohne Transaktion.
```java
// SCHLECHT:
public class BookingService {
    public void updateBooking(Booking booking) {
        repository.save(booking);
    }
}

// GUT:
@Service
@Transactional(readOnly = true)
public class BookingService {

    @Transactional
    public void updateBooking(Booking booking) {
        repository.save(booking);
    }
}
```

### Exception Swallowing
**Problem**: Exceptions ignorieren ohne Logging.
```java
// SCHLECHT:
try {
    bookingService.createBooking(request);
} catch (Exception e) {
    // Ignoriert
}

// GUT:
try {
    bookingService.createBooking(request);
} catch (BookingException e) {
    log.error("Failed to create booking", e);
    throw new ApiException("Could not create booking", e);
}
```

## JPA/Hibernate Anti-Patterns

### N+1 Query Problem
**Problem**: Lazy Loading führt zu vielen einzelnen Queries.
```java
// SCHLECHT:
List<Booking> bookings = bookingRepository.findAll();
for (Booking b : bookings) {
    String name = b.getCustomer().getName(); // N zusätzliche Queries
}

// GUT:
@Query("SELECT b FROM Booking b JOIN FETCH b.customer")
List<Booking> findAllWithCustomer();
```

### Missing Indexes
**Problem**: Langsame Queries auf häufig abgefragten Spalten.
```sql
-- SCHLECHT: Keine Indizes

-- GUT:
CREATE INDEX idx_booking_status ON booking(status);
CREATE INDEX idx_booking_foodtruck_name ON booking(foodtruck_name);
```

### Entity State Confusion
**Problem**: Detached Entities führen zu Fehlern.
```java
// PROBLEMATISCH:
Booking booking = bookingRepository.findById(id).get();
// ... Transaktion endet
booking.setStatus(BookingStatus.CONFIRMED);
bookingRepository.save(booking); // Neue Transaktion
```

## Testing Anti-Patterns

### Testing Implementation Details
**Problem**: Tests brechen bei Refactoring.
```java
// SCHLECHT: Testet interne Implementierung
verify(repository, times(1)).findById(any());

// GUT: Testet Verhalten
BookingResponse response = service.getBookingById(id);
assertThat(response.getId()).isEqualTo(id);
```

### Test Dependencies
**Problem**: Tests sind voneinander abhängig.
```java
// SCHLECHT:
@Test
void test1() {
    Booking b = service.createBooking(request);
    bookingId = b.getId(); // Shared state
}

@Test
void test2() {
    service.deleteBooking(bookingId); // Abhängig von test1
}

// GUT: Jeder Test ist unabhängig
```

### No Assertions
**Problem**: Tests ohne Verifikation.
```java
// SCHLECHT:
@Test
void testCreateBooking() {
    service.createBooking(request);
    // Kein Assert!
}

// GUT:
@Test
void testCreateBooking() {
    BookingResponse response = service.createBooking(request);
    assertThat(response).isNotNull();
    assertThat(response.getStatus()).isEqualTo(BookingStatus.PENDING);
}
```

## Security Anti-Patterns

### SQL Injection
**Problem**: String-Concatenation für Queries.
```java
// GEFÄHRLICH:
String query = "SELECT * FROM booking WHERE id = '" + id + "'";

// SICHER:
@Query("SELECT b FROM Booking b WHERE b.id = :id")
Optional<Booking> findById(@Param("id") UUID id);
```

### Logging Sensitive Data
**Problem**: Passwörter, Tokens in Logs.
```java
// SCHLECHT:
log.info("User login: {}", userCredentials);

// GUT:
log.info("User login attempt for username: {}", username);
```

### Missing Input Validation
**Problem**: Ungefilterte User-Eingaben.
```java
// SCHLECHT:
public void createBooking(BookingRequest request) {
    // Keine Validierung
}

// GUT:
public void createBooking(@Valid BookingRequest request) {
    // Bean Validation prüft automatisch
}
```

## Performance Anti-Patterns

### Premature Optimization
**Problem**: Optimierung ohne Messung.
**Regel**: Erst messen, dann optimieren. "Premature optimization is the root of all evil."

### Loading All Data
**Problem**: Keine Pagination bei großen Datenmengen.
```java
// SCHLECHT:
List<Booking> bookings = repository.findAll();

// GUT:
Page<Booking> bookings = repository.findAll(PageRequest.of(page, size));
```

### Unnecessary Object Creation
**Problem**: Objekte in Loops erstellen.
```java
// SCHLECHT:
for (int i = 0; i < 1000; i++) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    String date = sdf.format(dates[i]);
}

// GUT:
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
for (int i = 0; i < 1000; i++) {
    String date = sdf.format(dates[i]);
}
```

## Projektspezifische Anti-Patterns

(Dieser Bereich wird gefüllt, wenn projektspezifische Probleme auftreten)

### Beispiel-Format für neue Einträge:
```markdown
### [Problem-Name]
**Datum**: YYYY-MM-DD
**Kontext**: [Wo ist das Problem aufgetreten]
**Problem**: [Beschreibung]
**Code-Beispiel**:
```java
// Problematischer Code
```
**Lösung**: [Wie wurde es behoben]
**Prävention**: [Wie vermeiden wir das in Zukunft]
```

## Review-Hinweis

Beim Code-Review gegen diese Liste prüfen. Bei neuen Anti-Patterns dieses Dokument erweitern.
