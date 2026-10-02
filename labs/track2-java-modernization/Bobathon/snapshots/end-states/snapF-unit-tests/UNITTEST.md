# Unit Testing Strategy — Simple Pharmacy Dashboard

## Table of Contents
1. [Project Architecture Overview](#1-project-architecture-overview)
2. [Testing Approach by Module](#2-testing-approach-by-module)
3. [Test File Paths and Naming Conventions](#3-test-file-paths-and-naming-conventions)
4. [Test Commands](#4-test-commands)
5. [Recommended Frameworks and Libraries](#5-recommended-frameworks-and-libraries)
6. [Quality Metrics and Coverage Thresholds](#6-quality-metrics-and-coverage-thresholds)

---

## 1. Project Architecture Overview

The application is a **Jakarta EE 10 WAR** deployed on IBM Open Liberty, built with **Java 21** and **Maven**. It exposes a JAX-RS REST API consumed by an Angular frontend. There is no database — state is held in in-memory `ConcurrentHashMap` singletons.

### Layer diagram

```
┌──────────────────────────────────────────────────────┐
│                  API Layer (JAX-RS)                   │
│  MedicineResource  PrescriptionResource  OrderResource│
│  DashboardResource  CorsFilter  PharmacyApplication  │
└───────────────────────┬──────────────────────────────┘
                        │ uses
┌───────────────────────▼──────────────────────────────┐
│               Repository Layer (Singletons)           │
│  MedicineRepository  PrescriptionRepository           │
│  OrderRepository                                      │
└───────────────────────┬──────────────────────────────┘
                        │ manages
┌───────────────────────▼──────────────────────────────┐
│                  Model Layer (POJOs)                   │
│  Medicine  Prescription  Order                        │
└──────────────────────────────────────────────────────┘
```

### Key components requiring testing

| Component | Package | Priority | Reason |
|---|---|---|---|
| `MedicineRepository` | `com.pharmacy.repository` | High | Business logic: `updateStock`, `searchByName`, ID management |
| `PrescriptionRepository` | `com.pharmacy.repository` | High | Business logic: `generateId`, status-based queries |
| `OrderRepository` | `com.pharmacy.repository` | High | Business logic: `generateId`, multi-filter queries |
| `MedicineResource` | `com.pharmacy.api` | High | REST contract: routing, 404 handling, search fallback |
| `PrescriptionResource` | `com.pharmacy.api` | High | Orchestration: validates medicine lookup, expiry calc, status machine |
| `OrderResource` | `com.pharmacy.api` | High | Orchestration: prescription-to-order workflow, stock deduction, payment flow |
| `DashboardResource` | `com.pharmacy.api` | Medium | Aggregation logic correctness |
| `CorsFilter` | `com.pharmacy.api` | Low | Header injection |
| `Medicine` / `Order` / `Prescription` | `com.pharmacy.model` | Low | POJO construction, getter/setter symmetry |

---

## 2. Testing Approach by Module

### 2.1 Model Layer — `com.pharmacy.model`

**Strategy:** plain unit tests, no mocking needed.

| Class | Test scenarios |
|---|---|
| `Medicine` | Default constructor produces all-null/zero fields; all-args constructor stores all values; each getter returns the value set by its setter; `BigDecimal` price round-trips without loss |
| `Order` | Same getter/setter coverage; status field accepts all lifecycle values (`PENDING`, `VALIDATED`, `PAID`, `COLLECTED`, `CANCELLED`); `totalAmount` precision is preserved |
| `Prescription` | Same getter/setter coverage; status field accepts all lifecycle values (`PENDING`, `VALIDATED`, `FULFILLED`, `EXPIRED`); `prescriptionDate` and `expiryDate` are independent |

> These tests are low-value individually but guarantee that future refactors (e.g. adding validation or Lombok) do not silently break the data contract.

---

### 2.2 Repository Layer — `com.pharmacy.repository`

**Strategy:** unit tests against the repository's public API. Because repositories are singletons backed by a shared `ConcurrentHashMap`, each test class must **reset the singleton** or work with a freshly populated state. Use reflection to null out `instance` before each test, or extract the map population into a helper that can be called independently.

#### `MedicineRepository`

| Method | Test scenarios |
|---|---|
| `addMedicine` / `findById` | Add a medicine → find by its ID returns the same object; unknown ID returns `null` |
| `findAll` | Returns all added medicines; result is a new list (mutations do not affect the store) |
| `searchByName` | Case-insensitive match on substring; no match returns empty list; null-safe blank name returns all |
| `updateMedicine` | Updated fields are reflected on the next `findById` |
| `deleteMedicine` | Deleted ID returns `null` on subsequent `findById`; size decreases by 1 |
| `updateStock` | Sufficient stock: quantity decreases and `true` is returned; exact stock: succeeds; zero stock with demand: returns `false`; unknown medicine ID: returns `false` |

#### `PrescriptionRepository`

| Method | Test scenarios |
|---|---|
| `generateId` | Produces IDs in `RXnnn` format; each successive call produces a unique, incremented ID |
| `addPrescription` / `findById` | Round-trip; unknown ID returns `null` |
| `findByPatientId` | Returns only prescriptions for the given patient; unknown patient ID returns empty list |
| `findByStatus` | Returns only prescriptions matching the requested status; unknown status returns empty list |
| `updatePrescription` | Status change persists on re-fetch |
| `deletePrescription` | Removed entry is no longer found |

#### `OrderRepository`

| Method | Test scenarios |
|---|---|
| `generateId` | Produces IDs in `ORDnnnn` format; sequential calls produce distinct IDs |
| `addOrder` / `findById` | Round-trip; unknown ID returns `null` |
| `findByPatientId` | Filters correctly; empty for unknown patient |
| `findByStatus` | Filters correctly; empty for unknown status |
| `findByPrescriptionId` | Filters correctly; empty for unknown prescription |
| `updateOrder` | Status change persists on re-fetch |
| `deleteOrder` | Entry is removed |

---

### 2.3 API Layer — `com.pharmacy.api`

**Strategy:** use **Mockito** to inject mock repositories into the resource classes (breaking the `getInstance()` hard-dependency via constructor injection or reflection), then call the resource methods directly — no HTTP container required. Verify the returned `jakarta.ws.rs.core.Response` status and entity.

#### `MedicineResource`

| Method | Scenario | Expected response |
|---|---|---|
| `getAllMedicines()` | Repo returns list | 200 + list body |
| `getMedicineById(id)` | Medicine found | 200 + medicine entity |
| `getMedicineById(id)` | Medicine not found | 404 |
| `searchMedicines(name)` | Non-blank name | 200 + filtered list from `searchByName` |
| `searchMedicines(name)` | Blank / null name | 200 + full list from `findAll` |

#### `PrescriptionResource`

| Method | Scenario | Expected response |
|---|---|---|
| `getAllPrescriptions()` | Repo returns list | 200 + list |
| `getPrescriptionById(id)` | Found | 200 + entity |
| `getPrescriptionById(id)` | Not found | 404 |
| `createPrescription(data)` | Valid data + known medicine | 201 + new prescription with status `PENDING` |
| `createPrescription(data)` | Unknown `medicineId` | 400 |
| `createPrescription(data)` | Missing required field (NPE path) | 400 |
| `createPrescription(data)` | Expiry date is ~30 days after today | Assert date gap ≥ 29 days |
| `validatePrescription(id)` | Found + status `PENDING` | 200 + status `VALIDATED` |
| `validatePrescription(id)` | Not found | 404 |
| `validatePrescription(id)` | Status not `PENDING` (e.g. `FULFILLED`) | 400 |

#### `OrderResource`

| Method | Scenario | Expected response |
|---|---|---|
| `getAllOrders()` | Repo returns list | 200 + list |
| `getOrderById(id)` | Found | 200 + entity |
| `getOrderById(id)` | Not found | 404 |
| `createOrderFromPrescription(data)` | Valid: prescription `VALIDATED` + medicine in stock | 201 + order; prescription status becomes `FULFILLED`; no stock deduction yet |
| `createOrderFromPrescription(data)` | Prescription not found | 404 |
| `createOrderFromPrescription(data)` | Prescription status not `VALIDATED` | 400 |
| `createOrderFromPrescription(data)` | Medicine not found | 400 |
| `createOrderFromPrescription(data)` | Insufficient stock | 400 |
| `createOrderFromPrescription(data)` | Total = price × quantity | Assert `BigDecimal` value |
| `processPayment(id, data)` | Order `PENDING` | 200 + status `PAID`; `updateStock` called |
| `processPayment(id, data)` | Order `VALIDATED` | 200 + status `PAID` |
| `processPayment(id, data)` | Order not found | 404 |
| `processPayment(id, data)` | Order already `PAID` or `COLLECTED` | 400 |
| `collectOrder(id)` | Order `PAID` | 200 + status `COLLECTED` |
| `collectOrder(id)` | Not found | 404 |
| `collectOrder(id)` | Not `PAID` | 400 |

#### `DashboardResource`

| Method | Scenario | Expected response |
|---|---|---|
| `getDashboardData()` | Both repos populated | Map contains `pendingPrescriptions`, `pendingOrders`, `totalPrescriptions`, `totalOrders` with correct counts |

#### `CorsFilter`

| Scenario | Assertion |
|---|---|
| `filter()` called | Response headers contain `Access-Control-Allow-Origin: *`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Headers`, `Access-Control-Max-Age` |

---

## 3. Test File Paths and Naming Conventions

### Directory structure

```
src/
└── test/
    └── java/
        └── com/
            └── pharmacy/
                ├── model/
                │   ├── MedicineTest.java
                │   ├── OrderTest.java
                │   └── PrescriptionTest.java
                ├── repository/
                │   ├── MedicineRepositoryTest.java
                │   ├── OrderRepositoryTest.java
                │   └── PrescriptionRepositoryTest.java
                └── api/
                    ├── MedicineResourceTest.java
                    ├── OrderResourceTest.java
                    ├── PrescriptionResourceTest.java
                    ├── DashboardResourceTest.java
                    └── CorsFilterTest.java
```

### Naming rules

| Element | Convention | Example |
|---|---|---|
| Test class | `<ClassUnderTest>Test` | `MedicineRepositoryTest` |
| Test method | `<methodName>_<scenario>_<expectedOutcome>` | `updateStock_sufficientQuantity_returnsTrueAndDecrements` |
| Test method (negative) | `<methodName>_<failScenario>_<expectedOutcome>` | `updateStock_insufficientStock_returnsFalse` |
| Test data constants | `UPPER_SNAKE_CASE` static fields | `private static final String MEDICINE_ID = "MED001";` |

### Example test class skeleton

```java
package com.pharmacy.repository;

import com.pharmacy.model.Medicine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MedicineRepositoryTest {

    private MedicineRepository repo;

    @BeforeEach
    void setUp() throws Exception {
        // Reset singleton so each test starts clean
        Field instance = MedicineRepository.class.getDeclaredField("instance");
        instance.setAccessible(true);
        instance.set(null, null);

        repo = MedicineRepository.getInstance();
    }

    @Test
    void updateStock_sufficientQuantity_returnsTrueAndDecrements() {
        Medicine med = new Medicine("T01", "Test", "", new BigDecimal("10.00"), 50, "Lab");
        repo.addMedicine(med);

        boolean result = repo.updateStock("T01", 20);

        assertTrue(result);
        assertEquals(30, repo.findById("T01").getStockQuantity());
    }

    @Test
    void updateStock_insufficientStock_returnsFalse() {
        Medicine med = new Medicine("T02", "Test", "", new BigDecimal("10.00"), 5, "Lab");
        repo.addMedicine(med);

        boolean result = repo.updateStock("T02", 10);

        assertFalse(result);
        assertEquals(5, repo.findById("T02").getStockQuantity());
    }

    @Test
    void findById_unknownId_returnsNull() {
        assertNull(repo.findById("DOES_NOT_EXIST"));
    }
}
```

---

## 4. Test Commands

### Run all unit tests
```bash
mvn test
```

### Run all tests with a clean build
```bash
mvn clean test
```

### Run a single test class
```bash
mvn test -Dtest=MedicineRepositoryTest
```

### Run a single test method
```bash
mvn test -Dtest=MedicineRepositoryTest#updateStock_sufficientQuantity_returnsTrueAndDecrements
```

### Run all tests in a package
```bash
mvn test -Dtest="com.pharmacy.repository.*"
```

### Run tests and generate JaCoCo HTML coverage report
```bash
mvn test jacoco:report
```
Report location: `target/site/jacoco/index.html`

### Full clean build with coverage report
```bash
mvn clean test jacoco:report
```

### Run tests skipping coverage instrumentation (faster local cycle)
```bash
mvn test -Djacoco.skip=true
```

---

## 5. Recommended Frameworks and Libraries

The project currently declares **no test-scoped dependencies**. The following additions to [`pom.xml`](pom.xml) are required before any tests can be written.

### 5.1 JUnit 5 (Jupiter) — primary test runner

```xml
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter</artifactId>
    <version>5.11.4</version>
    <scope>test</scope>
</dependency>
```

Use `@Test`, `@BeforeEach`, `@AfterEach`, `@ParameterizedTest`, and `@ValueSource` / `@MethodSource` for data-driven cases. JUnit 5 is the standard for Java 11+ projects.

Enable the JUnit Platform provider in the Surefire plugin:

```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-surefire-plugin</artifactId>
    <version>3.2.5</version>
</plugin>
```

> Surefire 3.x auto-discovers JUnit 5 — no extra `<dependencies>` block in the plugin is needed with `junit-jupiter` on the classpath.

### 5.2 Mockito — mocking repositories in API tests

```xml
<dependency>
    <groupId>org.mockito</groupId>
    <artifactId>mockito-junit-jupiter</artifactId>
    <version>5.14.2</version>
    <scope>test</scope>
</dependency>
```

Use `@ExtendWith(MockitoExtension.class)` and `@Mock` / `@InjectMocks` to break the `getInstance()` singleton dependency in resource tests. Because resources currently instantiate repositories via `getInstance()` (a static call), the recommended approach is to **inject the repository via constructor** in the production class, or use `Mockito.mockStatic()` to stub the static factory method.

Example with `mockStatic`:

```java
@Test
void getMedicineById_notFound_returns404() {
    try (MockedStatic<MedicineRepository> mock = mockStatic(MedicineRepository.class)) {
        MedicineRepository mockRepo = mock(MedicineRepository.class);
        mock.when(MedicineRepository::getInstance).thenReturn(mockRepo);
        when(mockRepo.findById("MISSING")).thenReturn(null);

        MedicineResource resource = new MedicineResource();
        Response response = resource.getMedicineById("MISSING");

        assertEquals(404, response.getStatus());
    }
}
```

### 5.3 AssertJ — fluent assertions (optional but recommended)

```xml
<dependency>
    <groupId>org.assertj</groupId>
    <artifactId>assertj-core</artifactId>
    <version>3.27.3</version>
    <scope>test</scope>
</dependency>
```

AssertJ produces more readable failure messages than plain JUnit assertions and provides rich collection and string matchers directly useful for testing repository `List` returns.

### 5.4 Summary of required `pom.xml` additions

```xml
<dependencies>
    <!-- existing provided dependency ... -->

    <!-- Test dependencies -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.11.4</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.14.2</version>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.assertj</groupId>
        <artifactId>assertj-core</artifactId>
        <version>3.27.3</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <!-- existing plugins ... -->
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.2.5</version>
        </plugin>
    </plugins>
</build>
```

---

## 6. Quality Metrics and Coverage Thresholds

### 6.1 Coverage targets

| Layer | Line coverage | Branch coverage | Rationale |
|---|---|---|---|
| Model (`com.pharmacy.model`) | ≥ 90 % | ≥ 80 % | Pure POJOs — high coverage is trivial to achieve |
| Repository (`com.pharmacy.repository`) | ≥ 85 % | ≥ 80 % | Contains conditional logic (`updateStock`, null checks) that must be exercised |
| API (`com.pharmacy.api`) | ≥ 80 % | ≥ 75 % | Orchestration and status-machine branches are the most critical paths |
| **Overall project** | **≥ 80 %** | **≥ 75 %** | Minimum acceptable bar before merging |

### 6.2 Enforcing thresholds with JaCoCo

Add a `check` execution to the JaCoCo plugin in [`pom.xml`](pom.xml) to fail the build when minimums are not met:

```xml
<execution>
    <id>check</id>
    <goals>
        <goal>check</goal>
    </goals>
    <configuration>
        <rules>
            <rule>
                <element>BUNDLE</element>
                <limits>
                    <limit>
                        <counter>LINE</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.80</minimum>
                    </limit>
                    <limit>
                        <counter>BRANCH</counter>
                        <value>COVEREDRATIO</value>
                        <minimum>0.75</minimum>
                    </limit>
                </limits>
            </rule>
        </rules>
    </configuration>
</execution>
```

### 6.3 Additional quality metrics

| Metric | Target | How to measure |
|---|---|---|
| Test count | ≥ 60 test methods across all classes | `mvn test` — Surefire summary line |
| Test execution time | ≤ 5 seconds total | Surefire report; these are pure unit tests with no I/O |
| Mutation score | ≥ 70 % (optional) | Add [PIT Mutation Testing](https://pitest.org/) plugin |
| Zero flaky tests | 100 % deterministic | Each test resets singleton state in `@BeforeEach` |
| No inter-test dependency | All tests pass in isolation | Run with `-Dsurefire.failIfNoSpecifiedTests=false -Dtest=SomeTest` |

### 6.4 Critical business logic paths that must be covered

The following paths represent the core pharmacy workflows and **must reach 100 % branch coverage** regardless of the overall threshold:

1. **`MedicineRepository.updateStock`** — both the sufficient-stock and insufficient-stock branches, plus the not-found branch.
2. **`OrderResource.createOrderFromPrescription`** — all four guard clauses (prescription not found, not validated, medicine not found, insufficient stock) plus the happy path.
3. **`OrderResource.processPayment`** — status guard (`PENDING`/`VALIDATED` allowed, others rejected).
4. **`OrderResource.collectOrder`** — `PAID` guard.
5. **`PrescriptionResource.validatePrescription`** — `PENDING` guard.

### 6.5 Reading the JaCoCo report

After running `mvn clean test jacoco:report`, open:

```
target/site/jacoco/index.html
```

Navigate to `com.pharmacy.repository` or `com.pharmacy.api` to drill into per-class and per-method coverage. Red highlighting indicates uncovered lines or uncovered branches (diamond icon with a fraction). Address all red paths in the critical list above before considering coverage complete.
