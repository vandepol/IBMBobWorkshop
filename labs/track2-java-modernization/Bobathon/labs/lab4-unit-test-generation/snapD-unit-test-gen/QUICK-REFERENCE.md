# Quick Reference - Liberty Server Management

## 🚨 Common Issue: Server Still Running After Ctrl+C

When you press `Ctrl+C` to stop the Liberty server, it may continue running in the background and keep port 9081 occupied.

## ✅ Solution: Use the Stop Script

### macOS/Linux:
```bash
./stop-liberty.sh
```

### Windows:
```cmd
stop-liberty.bat
```

### Using Make:
```bash
make stop
```

## 🔍 What the Stop Script Does

1. Runs `mvn liberty:stop` to gracefully stop the server
2. Checks if port 9081 is still in use
3. Finds and kills any process using port 9081
4. Terminates any remaining Liberty Java processes
5. Confirms the port is free

## 📋 Complete Workflow

### Starting the Server:
```bash
./run-liberty.sh          # Full build & run
# OR
./quick-start.sh          # Quick restart (skip build)
# OR
make run                  # Using Makefile
```

### Stopping the Server:
```bash
./stop-liberty.sh         # Proper shutdown
# OR
make stop                 # Using Makefile
```

## 🛠️ Manual Port Check (macOS/Linux)

Check what's using port 9081:
```bash
lsof -i :9081
```

Kill process manually:
```bash
kill -9 <PID>
```

## 🛠️ Manual Port Check (Windows)

Check what's using port 9081:
```cmd
netstat -ano | findstr :9081
```

Kill process manually:
```cmd
taskkill /F /PID <PID>
```

## 📊 Dashboard URL

Once running, access the pharmacy dashboard at:
**http://localhost:9081/simple-pharmacy.war/dashboard**