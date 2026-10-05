#!/bin/bash

# Stop Liberty Server Script
# This script properly stops the Liberty server and frees the port

set -e

echo "=========================================="
echo "Stopping Liberty Server"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Try Maven stop command first
echo -e "${YELLOW}Attempting to stop Liberty server via Maven...${NC}"
mvn liberty:stop 2>/dev/null || echo "Maven stop command completed"

# Give it a moment
sleep 2

# Check if port 9081 is still in use and kill the process
echo ""
echo -e "${YELLOW}Checking for processes on port 9081...${NC}"

# Find process using port 9081
PID=$(lsof -ti:9081 2>/dev/null || true)

if [ -z "$PID" ]; then
    echo -e "${GREEN}✓ Port 9081 is free${NC}"
else
    echo -e "${YELLOW}Found process $PID using port 9081${NC}"
    echo -e "${YELLOW}Killing process...${NC}"
    kill -9 $PID 2>/dev/null || true
    sleep 1
    
    # Verify it's killed
    if lsof -ti:9081 >/dev/null 2>&1; then
        echo -e "${RED}✗ Failed to free port 9081${NC}"
        echo "You may need to manually kill the process with: kill -9 $PID"
    else
        echo -e "${GREEN}✓ Process killed, port 9081 is now free${NC}"
    fi
fi

# Also check for any Java processes running Liberty
echo ""
echo -e "${YELLOW}Checking for Liberty Java processes...${NC}"
LIBERTY_PIDS=$(ps aux | grep -i "liberty" | grep -i "java" | grep -v grep | awk '{print $2}' || true)

if [ -z "$LIBERTY_PIDS" ]; then
    echo -e "${GREEN}✓ No Liberty processes found${NC}"
else
    echo -e "${YELLOW}Found Liberty processes: $LIBERTY_PIDS${NC}"
    echo -e "${YELLOW}Killing Liberty processes...${NC}"
    echo "$LIBERTY_PIDS" | xargs kill -9 2>/dev/null || true
    sleep 1
    echo -e "${GREEN}✓ Liberty processes terminated${NC}"
fi

echo ""
echo -e "${GREEN}=========================================="
echo "✓ Liberty Server Stopped"
echo "==========================================${NC}"
echo ""

# Made with Bob
