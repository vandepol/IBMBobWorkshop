# Liberty Automation Scripts

Quick automation scripts to build and run the Simple Pharmacy application on Liberty server.

## 🚀 Quick Start

### For macOS/Linux:

**Full Build & Run:**
```bash
./run-liberty.sh
```

**Quick Start (if already built):**
```bash
./quick-start.sh
```

**Quick Start (if already built):**
```bash
./quick-start.sh
```

**Stop Server:**
```bash
./stop-liberty.sh
```

### For Windows:

**Full Build & Run:**
```cmd
run-liberty.bat
```

**Stop Server:**
```cmd
stop-liberty.bat
```

## 📊 Application URLs

Once the server starts, the scripts will display all available URLs. The main endpoints are:

- **Dashboard (Main):** http://localhost:9081/simple-pharmacy.war/dashboard
- **Prescriptions:** http://localhost:9081/simple-pharmacy.war/prescription-list
- **Orders:** http://localhost:9081/simple-pharmacy.war/order-list
- **Medicines:** http://localhost:9081/simple-pharmacy.war/medicine-list

## 🛠️ What Each Script Does

### `run-liberty.sh` / `run-liberty.bat`
- Cleans previous builds
- Compiles and packages the application (Maven)
- Starts Liberty server
- Waits for server to be ready
- **Displays all application URLs including the dashboard endpoint**

### `quick-start.sh`
- Skips the build step (faster)
- Starts Liberty server with existing WAR file
- Waits for server to be ready
- **Displays all application URLs including the dashboard endpoint**

## 📝 Manual Commands

If you prefer to run commands manually:

```bash
# Build the application
mvn clean package

# Run Liberty server
mvn liberty:run

# Stop Liberty server (in another terminal)
mvn liberty:stop
```

## 🔧 Configuration

The application is configured to run on:
- **HTTP Port:** 9081
- **HTTPS Port:** 9443
- **Context Path:** /simple-pharmacy.war
- **Server:** Liberty (defaultServer)

Configuration files:
- [`pom.xml`](pom.xml) - Maven configuration with Liberty plugin
- [`src/main/liberty/config/server.xml`](src/main/liberty/config/server.xml) - Liberty server configuration
- [`src/main/resources/struts.xml`](src/main/resources/struts.xml) - Struts action mappings

## 🛑 Stopping the Server

**Important:** When you press `Ctrl+C`, the Liberty server may continue running in the background and keep port 9081 occupied.

**Recommended way to stop:**

**macOS/Linux:**
```bash
./stop-liberty.sh
```

**Windows:**
```cmd
stop-liberty.bat
```

**Or using Make:**
```bash
make stop
```

**Manual Maven command:**
```bash
mvn liberty:stop
```

**If port is still occupied, the stop scripts will:**
- Run Maven stop command
- Find and kill any process using port 9081
- Terminate any remaining Liberty Java processes

## 📦 Requirements

- Java 8 or higher
- Maven 3.6 or higher
- Liberty server (automatically downloaded by Maven plugin)

## 🎯 Dashboard Endpoint

The pharmacy dashboard is the main entry point of the application:

**URL:** http://localhost:9081/simple-pharmacy.war/dashboard

This endpoint is mapped to [`DashboardAction.java`](src/main/java/com/pharmacy/action/DashboardAction.java) and displays the main dashboard view at [`dashboard.jsp`](src/main/webapp/WEB-INF/content/dashboard.jsp).