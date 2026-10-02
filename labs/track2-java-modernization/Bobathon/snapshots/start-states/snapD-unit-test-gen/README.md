# Simple Pharmacy Management System

A modern pharmacy management system with an Angular/Material Design frontend and Java REST API backend running on Liberty.

## 🎯 Overview

This application has been upgraded from Java 8 to Java 21 and completely modernized with:
- **Frontend**: Angular 17 + Angular Material
- **Backend**: JAX-RS REST API (Java 21)
- **Server**: Open Liberty with Jakarta EE 10
- **Build**: Maven with automated frontend build

## 🚀 Quick Start

### Prerequisites

- Java 21 or higher
- Maven 3.6 or higher

### Build and Run

**Single Command (Recommended):**
```bash
./run-fullstack.sh
```

This script will:
1. Build the Angular frontend
2. Build the Java backend
3. Package everything into a WAR file
4. Start the Liberty server
5. Display the application URL

**Manual Maven Commands:**
```bash
# Build and run in one step
mvn clean liberty:run

# Or build first, then run
mvn clean package
mvn liberty:run

# Stop the server (in another terminal)
mvn liberty:stop
```

**Access the application:**
- **Angular Frontend**: http://localhost:9081/simple-pharmacy.war/app/
- **REST API**: http://localhost:9081/simple-pharmacy.war/api/

### Docker Deployment (Java 21 + Open Liberty)

**Build and run with Docker Compose:**
```bash
# Build and start the container
docker-compose up --build

# Or run in detached mode
docker-compose up -d --build

# Stop the container
docker-compose down
```

**Build and run with Docker directly:**
```bash
# Build the image
docker build -t simple-pharmacy:j21-liberty .

# Run the container
docker run -d -p 9081:9081 -p 9443:9443 \
  --name simple-pharmacy-j21 \
  simple-pharmacy:j21-liberty

# Stop the container
docker stop simple-pharmacy-j21
docker rm simple-pharmacy-j21
```

**Access the containerized application:**
- **Angular Frontend**: http://localhost:9081/simple-pharmacy.war/app/
- **REST API**: http://localhost:9081/simple-pharmacy.war/api/dashboard

### Development Mode (Frontend Hot Reload)

For frontend development with hot reload:

**Terminal 1 - Start Backend:**
```bash
mvn clean package
mvn liberty:run
```

**Terminal 2 - Start Frontend Dev Server:**
```bash
./run-frontend-dev.sh
```

**Access the application:**
- **Frontend Dev Server**: http://localhost:3000 (with hot reload via Angular CLI)
- **Backend API**: http://localhost:9081

## 📁 Project Structure

```
simple-pharmacy/
├── frontend/                    # Angular frontend application
│   ├── src/
│   │   ├── app/
│   │   │   ├── components/     # Reusable Angular components
│   │   │   ├── pages/          # Page components (Dashboard, Medicines, etc.)
│   │   │   ├── services/       # API service layer
│   │   │   ├── app.component.ts
│   │   │   ├── app.config.ts
│   │   │   └── app.routes.ts
│   │   ├── index.html
│   │   ├── main.ts             # Entry point
│   │   └── styles.scss
│   ├── angular.json
│   ├── package.json
│   └── tsconfig.json
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
│   ├── app/                    # Built Angular app (generated)
│   └── WEB-INF/web.xml
├── pom.xml                     # Maven build configuration
└── run-fullstack.sh            # Quick start script
```

## 🌐 Application Features

### Dashboard
- View pending prescriptions and orders
- See total counts and statistics
- Quick overview of pharmacy operations

### Prescriptions
- Create new prescriptions
- Validate prescriptions
- View prescription history
- Search and filter prescriptions

### Orders
- Create orders from validated prescriptions
- Process payments (Cash, Credit Card, Insurance)
- Mark orders as collected
- Track order status

### Medicines
- View medicine inventory
- Search medicines by name
- Check stock levels
- View pricing and manufacturer information

## 🔌 REST API Endpoints

Base URL: `http://localhost:9081/simple-pharmacy.war/api`

### Dashboard
- `GET /dashboard` - Get dashboard statistics

### Medicines
- `GET /medicines` - Get all medicines
- `GET /medicines/{id}` - Get medicine by ID
- `GET /medicines/search?q={query}` - Search medicines

### Prescriptions
- `GET /prescriptions` - Get all prescriptions
- `GET /prescriptions/{id}` - Get prescription by ID
- `POST /prescriptions` - Create new prescription
- `PUT /prescriptions/{id}/validate` - Validate prescription

### Orders
- `GET /orders` - Get all orders
- `GET /orders/{id}` - Get order by ID
- `POST /orders/from-prescription/{prescriptionId}` - Create order from prescription
- `PUT /orders/{id}/payment` - Process payment
- `PUT /orders/{id}/collect` - Mark order as collected

## 🛠️ Maven Commands

### Build
```bash
# Full build (frontend + backend)
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

### Frontend Only
```bash
cd frontend
npm install
npm run build  # Builds Angular app to ../src/main/webapp/app/
```

## 📝 Configuration

### Backend Configuration
- **Server Config**: `src/main/liberty/config/server.xml`
- **HTTP Port**: 9081
- **HTTPS Port**: 9443

### Frontend Configuration
- **Dev Server Port**: 3000
- **API Proxy**: Configured in `frontend/proxy.conf.json`
- **Build Output**: `src/main/webapp/app/`

## 🔄 Architecture

The application uses a modern architecture:

### Frontend
- Angular 17 with standalone components
- Angular Material for consistent design
- RxJS for reactive programming
- Angular Router for navigation
- Angular CLI for development and builds

### Backend
- JAX-RS for REST API endpoints (Jakarta EE 10)
- Repository pattern for data access
- In-memory data storage (easily replaceable with database)
- CORS support for development

### Technology Stack
- **Java**: 21
- **Jakarta EE**: 10
- **JAX-RS**: 3.1
- **JSON-B**: 3.0
- **Servlet**: 6.0
- **Angular**: 17
- **Angular Material**: 17
- **Open Liberty**: Latest

### What Changed from Original
- ✅ Removed Struts framework entirely
- ✅ Replaced JSP views with Angular + Material Design frontend
- ✅ REST API endpoints for all operations
- ✅ Modern component-based architecture with TypeScript
- ✅ Improved user experience
- ✅ Hot reload for development
- ✅ Upgraded from Java 8 to Java 21
- ✅ Migrated from Java EE to Jakarta EE 10

### What Stayed
- ✅ All business logic and data models
- ✅ Repository layer
- ✅ Liberty server configuration
- ✅ Core pharmacy management functionality

## 🐛 Troubleshooting

### Port 9081 Already in Use
```bash
mvn liberty:stop
```

### Frontend Not Showing
Make sure you run `mvn clean package` first to build the frontend, then `mvn liberty:run`.

### Frontend Build Fails
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

### Liberty Server Won't Start
Check logs in: `target/liberty/wlp/usr/servers/defaultServer/logs/`

## 📚 Additional Documentation

- [Automation Scripts](README-AUTOMATION.md) - Detailed script documentation
- [Frontend README](frontend/README.md) - Frontend-specific documentation

## 🤝 Contributing

This is a demo application for pharmacy management. Feel free to extend and customize it for your needs.

## 📄 License

This project is provided as-is for educational and demonstration purposes.