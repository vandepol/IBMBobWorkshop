#!/bin/bash

# Build MCP Servers Script
# This script builds all three MCP servers required for Bob's SDLC Incident Manager mode

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${BLUE}==>${NC} $1"
}

print_success() {
    echo -e "${GREEN}✓${NC} $1"
}

print_error() {
    echo -e "${RED}✗${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}⚠${NC} $1"
}

# Function to build an MCP server
build_mcp_server() {
    local server_name=$1
    local server_path=$2
    
    print_status "Building ${server_name}..."
    
    if [ ! -d "$server_path" ]; then
        print_error "Directory not found: $server_path"
        return 1
    fi
    
    cd "$server_path"
    
    # Install dependencies
    print_status "  Installing dependencies..."
    if npm install > /dev/null 2>&1; then
        print_success "  Dependencies installed"
    else
        print_error "  Failed to install dependencies"
        return 1
    fi
    
    # Build the server
    print_status "  Compiling TypeScript..."
    if npm run build > /dev/null 2>&1; then
        print_success "  Build completed"
    else
        print_error "  Build failed"
        return 1
    fi
    
    # Verify build output
    if [ -d "build" ] && [ -f "build/index.js" ]; then
        print_success "  Build artifacts verified"
    else
        print_warning "  Build directory exists but may be incomplete"
    fi
    
    cd - > /dev/null
    echo ""
}

# Main script
echo ""
echo "╔════════════════════════════════════════════════════════════╗"
echo "║         Building MCP Servers for Bob SDLC Mode            ║"
echo "╚════════════════════════════════════════════════════════════╝"
echo ""

# Check if Node.js is installed
print_status "Checking prerequisites..."
if ! command -v node &> /dev/null; then
    print_error "Node.js is not installed. Please install Node.js v20 or higher."
    exit 1
fi

if ! command -v npm &> /dev/null; then
    print_error "npm is not installed. Please install npm."
    exit 1
fi

NODE_VERSION=$(node --version)
print_success "Node.js ${NODE_VERSION} found"
echo ""

# Get the script directory (project root)
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

# Build each MCP server
BUILD_FAILED=0

# 1. ServiceNow MCP Server
if build_mcp_server "ServiceNow MCP Server" "local-servicenow-mcp"; then
    print_success "ServiceNow MCP Server built successfully"
else
    print_error "ServiceNow MCP Server build failed"
    BUILD_FAILED=1
fi

# 2. Ansible MCP Server
if build_mcp_server "Ansible MCP Server" "ansible-mcp-server"; then
    print_success "Ansible MCP Server built successfully"
else
    print_error "Ansible MCP Server build failed"
    BUILD_FAILED=1
fi

# 3. Terraform MCP Server
if build_mcp_server "Terraform MCP Server" "terraform-mcp-server"; then
    print_success "Terraform MCP Server built successfully"
else
    print_error "Terraform MCP Server build failed"
    BUILD_FAILED=1
fi

# Summary
echo ""
echo "╔════════════════════════════════════════════════════════════╗"
if [ $BUILD_FAILED -eq 0 ]; then
    echo "║                  ✓ All Builds Successful                  ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo ""
    print_success "All MCP servers are ready to use!"
    echo ""
    echo "Next steps:"
    echo "  1. Ensure .env file is configured with ServiceNow credentials"
    echo "  2. Switch Bob to 'SDLC Incident Manager' mode"
    echo "  3. Start using Bob for incident management"
    echo ""
    exit 0
else
    echo "║                  ✗ Some Builds Failed                     ║"
    echo "╚════════════════════════════════════════════════════════════╝"
    echo ""
    print_error "One or more MCP servers failed to build"
    echo ""
    echo "Troubleshooting:"
    echo "  1. Check that you have Node.js v20 or higher installed"
    echo "  2. Ensure you have internet connectivity for npm packages"
    echo "  3. Try running 'npm cache clean --force' and retry"
    echo "  4. Check individual server directories for error logs"
    echo ""
    exit 1
fi

# Made with Bob
