#!/bin/bash

# Simple Pharmacy Dashboard - Build and Run Script
# This script builds the application and runs it in WebSphere Traditional using Docker

set -e

echo "=========================================="
echo "Simple Pharmacy Dashboard"
echo "Build and Deploy Script"
echo "=========================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed. Please install Maven first."
    echo "Visit: https://maven.apache.org/install.html"
    exit 1
fi

# Check if Docker is installed
if ! command -v docker &> /dev/null; then
    echo "ERROR: Docker is not installed. Please install Docker first."
    echo ""
    echo "Options:"
    echo "  - Rancher Desktop: https://rancherdesktop.io/"
    echo "  - Docker Desktop: https://www.docker.com/products/docker-desktop"
    echo ""
    exit 1
fi

# Check if Docker daemon is running
if ! docker info &> /dev/null; then
    echo "ERROR: Docker daemon is not running."
    echo ""
    echo "Please start your Docker environment:"
    echo ""
    echo "For Rancher Desktop:"
    echo "  1. Open Rancher Desktop from Applications folder"
    echo "  2. Wait for Rancher Desktop to start (check menu bar icon)"
    echo "  3. Ensure 'dockerd (moby)' is selected as container runtime"
    echo "  4. Run this script again"
    echo ""
    echo "For Docker Desktop:"
    echo "  1. Open Docker Desktop from Applications folder"
    echo "  2. Wait for Docker to start (whale icon in menu bar)"
    echo "  3. Run this script again"
    echo ""
    echo "Start from command line:"
    echo "  Rancher Desktop: open -a 'Rancher Desktop'"
    echo "  Docker Desktop:  open -a Docker"
    echo ""
    exit 1
fi

echo "Docker daemon is running ✓"
echo ""

echo "Step 1: Cleaning previous builds..."
mvn clean

echo ""
echo "Step 2: Building application with Maven..."
mvn package

# Check if WAR file was created
if [ ! -f "target/simple-pharmacy.war" ]; then
    echo "ERROR: WAR file was not created. Build failed."
    exit 1
fi

echo ""
echo "Step 3: Pulling WebSphere base image (this may take several minutes on first run)..."
echo "Note: The WebSphere image is ~2GB. Please be patient."
echo ""

# Detect architecture
ARCH=$(uname -m)
if [ "$ARCH" = "arm64" ]; then
    echo "Detected Apple Silicon (ARM64) - will use x86_64 emulation"
    PLATFORM_FLAG="--platform linux/amd64"
else
    PLATFORM_FLAG=""
fi

# Try to pull the base image first with better error handling
if ! docker pull $PLATFORM_FLAG ibmcom/websphere-traditional:latest 2>&1 | tee /tmp/docker-pull.log; then
    echo ""
    echo "WARNING: Failed to pull WebSphere image from Docker Hub."
    echo "This could be due to:"
    echo "  - Network connectivity issues"
    echo "  - Docker Hub rate limiting"
    echo "  - Firewall/proxy restrictions"
    echo ""
    echo "Troubleshooting steps:"
    echo "  1. Check your internet connection"
    echo "  2. Try again in a few minutes (Docker Hub may be rate limiting)"
    echo "  3. Check if you need to configure proxy settings"
    echo "  4. Verify Docker can access registry-1.docker.io"
    echo ""
    echo "To test connectivity:"
    echo "  curl -I https://registry-1.docker.io/v2/"
    echo ""
    exit 1
fi

echo ""
echo "Step 4: Building Docker image..."
docker build -t simple-pharmacy:latest .

echo ""
echo "Step 5: Starting application with Docker Compose..."
docker-compose up -d

echo ""
echo "=========================================="
echo "Deployment Complete!"
echo "=========================================="
echo ""
echo "The application is starting up. This may take 2-3 minutes."
echo ""
echo "Access the application at:"
echo "  Application: http://localhost:9080/pharmacy/"
echo "  Admin Console: http://localhost:9060/ibm/console/"
echo "    Username: wsadmin"
echo "    Password: (check container logs for generated password)"
echo ""
echo "To view logs:"
echo "  docker-compose logs -f"
echo ""
echo "To stop the application:"
echo "  docker-compose down"
echo ""

# Made with Bob
