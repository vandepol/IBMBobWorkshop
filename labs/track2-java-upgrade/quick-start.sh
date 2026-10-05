#!/bin/bash

# Quick Start Script - Just run the server (assumes already built)
# Use this if you've already built the application and just want to restart

set -e

echo "=========================================="
echo "Simple Pharmacy - Quick Start"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${BLUE}Starting Liberty server...${NC}"
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
echo -e "${GREEN}📊 PHARMACY DASHBOARD:${NC}"
echo -e "  ${BLUE}➜${NC} http://localhost:9081/simple-pharmacy.war/dashboard"
echo ""
echo -e "${GREEN}Other URLs:${NC}"
echo -e "  Prescriptions:  http://localhost:9081/simple-pharmacy.war/prescription-list"
echo -e "  Orders:         http://localhost:9081/simple-pharmacy.war/order-list"
echo -e "  Medicines:      http://localhost:9081/simple-pharmacy.war/medicine-list"
echo ""
echo -e "${YELLOW}Press Ctrl+C to stop the server${NC}"
echo ""

# Keep script running
wait

# Made with Bob
