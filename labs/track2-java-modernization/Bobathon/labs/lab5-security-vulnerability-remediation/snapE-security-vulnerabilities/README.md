# Simple Pharmacy Management System — Security Vulnerabilities Lab

A pharmacy management system with a Java REST API backend running on Open Liberty. This project contains **intentionally injected security vulnerabilities** in its Maven dependencies — the starting point for the Java Vulnerabilities Detection workflow.

---

## Lab Exercise — Java Vulnerabilities Detection Workflow

### What You Will Do

Use Bob's **Java Vulnerabilities Detection** workflow to automatically:
1. Scan the project's dependencies against the [OSV database](https://osv.dev)
2. Review the vulnerability report (CVE IDs, severity, affected versions)
3. Apply targeted version-bump fixes to `pom.xml`
4. Verify the build compiles cleanly after remediation

### Running the Workflow

1. In the top left of Bob chat, click the workflow play icon button by the settings gear.

2. Select the `Java Vulnerabilities Detection` workflow and press `Start`

3. Follow any additional prompts through the workflow


### What to Expect

The workflow will detect **9 vulnerabilities** across 3 dependencies:

| Dependency | Vulnerable Version | CVEs | Fix Version |
|------------|--------------------|------|-------------|
| `commons-text` | `1.9` | 1 (Text4Shell — CVE-2022-42889) | `1.10.0` |
| `snakeyaml` | `1.30` | 7 (incl. RCE — GHSA-mjmj-j48q-9wg2) | `2.0` |
| `commons-lang3` | `3.11` | 1 (Uncontrolled Recursion — GHSA-j288-q9x7-2f5v) | `3.18.0` |

After the workflow completes, all three dependencies will be bumped to their safe versions and the build will remain clean.

> For full details on the injected vulnerabilities and how to remove individual ones, see [VULNERABILITIES.md](VULNERABILITIES.md).

---

## Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

## 🚀 Quick Start

### Build and Run

```bash
# Build and run
mvn clean liberty:run

# Stop the server (in another terminal)
mvn liberty:stop
```

**Access the application:**
- **REST API**: http://localhost:9081/simple-pharmacy.war/api/

## 📁 Project Structure

```
simple-pharmacy/
├── src/main/java/com/pharmacy/
│   ├── api/                    # REST API endpoints (JAX-RS)
│   │   ├── DashboardResource.java
│   │   ├── MedicineResource.java
│   │   ├── OrderResource.java
│   │   ├── PrescriptionResource.java
│   │   ├── PharmacyApplication.java
│   │   └── CorsFilter.java
│   ├── model/                  # Domain models
│   └── repository/             # Data repositories
├── src/main/liberty/config/
│   └── server.xml              # Liberty server configuration
├── src/main/webapp/
│   └── WEB-INF/web.xml
└── pom.xml                     # Maven build configuration
```

## 🌐 Application Features

### Dashboard
- View pending prescriptions and orders
- See total counts and statistics

### Prescriptions
- Create new prescriptions
- Validate prescriptions
- View prescription history

### Orders
- Create orders from validated prescriptions
- Process payments
- Track order status

### Medicines
- View medicine inventory
- Search medicines by name
- Check stock levels

## 🔌 REST API Endpoints

Base URL: `http://localhost:9081/simple-pharmacy.war/api`

### Dashboard
- `GET /dashboard` - Get dashboard statistics

### Medicines
- `GET /medicines` - Get all medicines
- `GET /medicines/{id}` - Get medicine by ID
- `GET /medicines/search?name={query}` - Search medicines

### Prescriptions
- `GET /prescriptions` - Get all prescriptions
- `GET /prescriptions/{id}` - Get prescription by ID
- `POST /prescriptions` - Create new prescription
- `PUT /prescriptions/{id}/validate` - Validate prescription

### Orders
- `GET /orders` - Get all orders
- `GET /orders/{id}` - Get order by ID
- `POST /orders/from-prescription` - Create order from prescription
- `PUT /orders/{id}/payment` - Process payment
- `PUT /orders/{id}/collect` - Mark order as collected

## 🛠️ Maven Commands

### Build
```bash
# Full build
mvn clean package

# Build without tests
mvn clean package -DskipTests
```

### Run
```bash
# Start Liberty server
mvn liberty:run

# Stop Liberty server (in another terminal)
mvn liberty:stop
```

## 📝 Configuration

### Backend Configuration
- **Server Config**: `src/main/liberty/config/server.xml`
- **HTTP Port**: 9081
- **HTTPS Port**: 9443

## 🔄 Technology Stack

- **Java**: 21
- **Jakarta EE**: 10
- **JAX-RS**: 3.1
- **JSON-B**: 3.0
- **Servlet**: 6.0
- **Open Liberty**: Latest

## 🐛 Troubleshooting

### Port 9081 Already in Use
```bash
mvn liberty:stop
```

### Liberty Server Won't Start
Check logs in: `target/liberty/wlp/usr/servers/defaultServer/logs/`

## 📚 Additional Documentation

- [Quick Reference](QUICK-REFERENCE.md) - Command reference
- [Automation Scripts](README-AUTOMATION.md) - Script documentation