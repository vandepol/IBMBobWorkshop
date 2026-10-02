#!/bin/bash

# Simple Pharmacy - Liberty Build & Run Script
# This script builds the application and runs it on Liberty server

set -e

echo "=========================================="
echo "Simple Pharmacy - Liberty Build & Run"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Clean and build
echo -e "${BLUE}[1/3] Building application...${NC}"
mvn clean package

echo ""
echo -e "${BLUE}[2/3] Starting Liberty server...${NC}"
mvn liberty:run &

# Wait for server to start
echo ""
echo -e "${YELLOW}Waiting for Liberty server to start...${NC}"
sleep 5

# Check if server is running
MAX_ATTEMPTS=30
ATTEMPT=0
while [ $ATTEMPT -lt $MAX_ATTEMPTS ]; do
    if curl -s http://localhost:9081 > /dev/null 2>&1; then
        break
    fi
    ATTEMPT=$((ATTEMPT + 1))
    echo -n "."
    sleep 2
done

echo ""
echo ""
echo -e "${GREEN}=========================================="
echo "✓ Liberty Server Started Successfully!"
echo "==========================================${NC}"
echo ""
echo -e "${GREEN}Application URLs:${NC}"
echo -e "  ${BLUE}Dashboard:${NC}      http://localhost:9081/simple-pharmacy.war/dashboard"
echo -e "  ${BLUE}Prescriptions:${NC}  http://localhost:9081/simple-pharmacy.war/prescription-list"
echo -e "  ${BLUE}Orders:${NC}         http://localhost:9081/simple-pharmacy.war/order-list"
echo -e "  ${BLUE}Medicines:${NC}      http://localhost:9081/simple-pharmacy.war/medicine-list"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop the server${NC}"
echo ""

# Keep script running
wait

# Made with Bob
