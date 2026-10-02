# Simple Pharmacy - Liberty Makefile
# Quick commands for building and running the application

.PHONY: help run quick-run build stop clean open-dashboard

help:
	@echo "Simple Pharmacy - Liberty Commands"
	@echo "===================================="
	@echo ""
	@echo "Available commands:"
	@echo "  make run            - Build and run Liberty server"
	@echo "  make quick-run      - Run Liberty server (skip build)"
	@echo "  make build          - Build application only"
	@echo "  make stop           - Stop Liberty server (properly frees port)"
	@echo "  make clean          - Clean build artifacts"
	@echo "  make open-dashboard - Open dashboard in browser"
	@echo ""
	@echo "Dashboard URL: http://localhost:9081/simple-pharmacy.war/dashboard"
	@echo ""
	@echo "Note: Use 'make stop' after Ctrl+C to ensure port 9081 is freed"

run:
	@echo "Building and starting Liberty server..."
	@mvn clean package
	@echo ""
	@echo "Starting Liberty server..."
	@mvn liberty:run &
	@sleep 5
	@echo ""
	@echo "=========================================="
	@echo "✓ Liberty Server Started!"
	@echo "=========================================="
	@echo ""
	@echo "📊 PHARMACY DASHBOARD:"
	@echo "  ➜ http://localhost:9081/simple-pharmacy.war/dashboard"
	@echo ""
	@echo "Other URLs:"
	@echo "  Prescriptions:  http://localhost:9081/simple-pharmacy.war/prescription-list"
	@echo "  Orders:         http://localhost:9081/simple-pharmacy.war/order-list"
	@echo "  Medicines:      http://localhost:9081/simple-pharmacy.war/medicine-list"
	@echo ""

quick-run:
	@echo "Starting Liberty server (skipping build)..."
	@mvn liberty:run &
	@sleep 5
	@echo ""
	@echo "=========================================="
	@echo "✓ Liberty Server Started!"
	@echo "=========================================="
	@echo ""
	@echo "📊 PHARMACY DASHBOARD:"
	@echo "  ➜ http://localhost:9081/simple-pharmacy.war/dashboard"
	@echo ""

build:
	@echo "Building application..."
	@mvn clean package
	@echo ""
	@echo "✓ Build complete!"

stop:
	@echo "Stopping Liberty server and freeing port 9081..."
	@./stop-liberty.sh

clean:
	@echo "Cleaning build artifacts..."
	@mvn clean
	@echo "✓ Clean complete"

open-dashboard:
	@echo "Opening dashboard in browser..."
	@if command -v open > /dev/null; then \
		open http://localhost:9081/simple-pharmacy.war/dashboard; \
	elif command -v xdg-open > /dev/null; then \
		xdg-open http://localhost:9081/simple-pharmacy.war/dashboard; \
	elif command -v start > /dev/null; then \
		start http://localhost:9081/simple-pharmacy.war/dashboard; \
	else \
		echo "Please open: http://localhost:9081/simple-pharmacy.war/dashboard"; \
	fi