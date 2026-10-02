#!/bin/bash
source "$(dirname "$0")/../docker-host.sh"

# Deploy Initial Application (Part 2)
# This script deploys the bank application in a healthy state with 1 backend replica
# No performance issues or overload - just a working application

set -e  # Exit on any error

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Function to print colored output
print_header() {
    echo ""
    echo -e "${CYAN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${CYAN}║$1${NC}"
    echo -e "${CYAN}╚════════════════════════════════════════════════════════════╝${NC}"
    echo ""
}

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

print_info() {
    echo -e "${CYAN}ℹ${NC} $1"
}

# Get the script directory
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
PROJECT_ROOT="$( cd "$SCRIPT_DIR/../.." && pwd )"

# Navigate to project root
cd "$PROJECT_ROOT"

print_header "       Part 2: Deploy Initial Application (Healthy State)       "

# Step 1: Check prerequisites
print_status "Step 1: Checking prerequisites..."

if ! command -v docker &> /dev/null; then
    print_error "Docker is not installed or not running"
    exit 1
fi

if ! docker ps &> /dev/null; then
    print_error "Docker daemon is not running. Please start Docker/Colima."
    exit 1
fi

if ! command -v terraform &> /dev/null; then
    print_error "Terraform is not installed"
    exit 1
fi

print_success "Docker is running"
print_success "Terraform is installed"
echo -e ""

# Step 2: Navigate to Terraform directory
print_status "Step 2: Preparing Terraform configuration..."
cd "$PROJECT_ROOT/bank-app/terraform"

# Step 3: Initialize Terraform if needed
if [ ! -d ".terraform" ]; then
    print_status "Initializing Terraform..."
    if terraform init > /dev/null 2>&1; then
        print_success "Terraform initialized"
    else
        print_error "Terraform initialization failed"
        exit 1
    fi
else
    print_success "Terraform already initialized"
fi

# Step 4: Configure for 1 backend replica (healthy low-traffic state)
print_status "Step 3: Configuring for low-traffic deployment (1 backend replica)..."

# Create or update terraform.tfvars
cat > terraform.tfvars << EOF
# Initial deployment configuration
# 1 backend replica is adequate for low traffic periods
backend_replicas = 1
EOF

print_success "Configuration set: backend_replicas = 1"
echo -e ""

# Step 5: Force rebuild frontend image to ensure clean state
print_status "Step 4: Ensuring frontend image is clean..."
if docker rmi -f bank-app-frontend:latest > /dev/null 2>&1; then
    print_success "Removed old frontend image"
else
    print_info "No old frontend image to remove"
fi
echo -e ""

# Step 6: Deploy infrastructure
print_status "Step 5: Deploying application infrastructure..."
echo -e ""
print_info "This will create:"
print_info "  • Docker network (bank-app-network)"
print_info "  • PostgreSQL database container"
print_info "  • 1 Backend API container (Node.js)"
print_info "  • Frontend container (Nginx + React)"
echo -e ""

if terraform apply -auto-approve; then
    echo ""
    print_success "Infrastructure deployed successfully!"
else
    echo ""
    print_error "Terraform deployment failed"
    exit 1
fi

echo -e ""

# Step 7: Wait for services to be ready
print_status "Step 6: Waiting for services to be ready..."
sleep 5

# Check if containers are running
BACKEND_COUNT=$(docker ps --filter "name=bank-app-dev-backend" --format "{{.Names}}" | wc -l)
FRONTEND_RUNNING=$(docker ps --filter "name=bank-app-dev-frontend" --format "{{.Names}}" | wc -l)
DB_RUNNING=$(docker ps --filter "name=bank-app-dev-db" --format "{{.Names}}" | wc -l)

if [ "$BACKEND_COUNT" -eq 1 ] && [ "$FRONTEND_RUNNING" -eq 1 ] && [ "$DB_RUNNING" -eq 1 ]; then
    print_success "All containers are running"
else
    print_warning "Some containers may not be running. Check with: docker ps"
fi

echo -e ""

# Step 8: Verify application health
print_status "Step 7: Verifying application health..."

# Wait a bit more for backend to be fully ready
sleep 3

# Check backend health
if curl -s http://localhost:5001/health > /dev/null 2>&1; then
    print_success "Backend is healthy"
    
    # Check metrics
    METRICS=$(curl -s http://localhost:5001/api/admin/metrics)
    OVERLOADED=$(echo "$METRICS" | grep -o '"overloaded":[^,}]*' | cut -d':' -f2)
    
    if [ "$OVERLOADED" = "false" ]; then
        print_success "Metrics show normal operation (overloaded: false)"
    else
        print_warning "Metrics endpoint accessible but may show unexpected values"
    fi
else
    print_warning "Backend health check failed. It may still be starting up."
    print_info "Try: curl http://localhost:5001/health"
fi

echo -e ""

# Step 8b: Start the Local Service Desk (workshop stand-in for ServiceNow)
if curl -s --max-time 2 http://localhost:8099/health > /dev/null 2>&1; then
    print_success "Local Service Desk already running on http://localhost:8099"
else
    nohup node "$PROJECT_ROOT/local-service-desk/server.mjs" --reset > "$PROJECT_ROOT/local-service-desk/server.log" 2>&1 &
    sleep 1
    print_success "Local Service Desk started on http://localhost:8099"
fi

# Step 9: Display summary
print_header "                    ✓ Deployment Complete!                     "

echo -e "${GREEN}Application Status:${NC}"
echo -e "  • Frontend:  http://localhost"
echo -e "  • Backend:   http://localhost:5001"
echo -e "  • Database:  localhost:5437"
echo -e "  • Service Desk (incidents): http://localhost:8099"
echo -e ""
echo -e "${GREEN}Infrastructure:${NC}"
echo -e "  • Backend replicas: 1 (adequate for low traffic)"
echo -e "  • Performance: Normal (<1 second response times)"
echo -e "  • Status: Healthy, no issues"
echo -e ""
echo -e "${CYAN}Next Steps (Part 2 of Lab):${NC}"
echo -e ""
echo -e "  1. Open the application in your browser:"
echo -e "     ${BLUE}open http://localhost${NC}"
echo -e ""
echo -e "  2. Login with demo credentials:"
echo -e "     Username: ${BLUE}demo${NC}"
echo -e "     Password: ${BLUE}demo123${NC}"
echo -e ""
echo -e "  3. Explore the application:"
echo -e "     • View account balances"
echo -e "     • Make deposits/withdrawals"
echo -e "     • Check transaction history"
echo -e "     • Request loans"
echo -e "     ${GREEN}Notice how fast everything loads!${NC}"
echo -e ""
echo -e "  4. Verify the infrastructure:"
echo -e "     ${BLUE}docker ps --filter \"name=bank-app\"${NC}"
echo -e "     ${BLUE}curl -s http://localhost:5001/api/admin/metrics | jq${NC}"
echo -e ""
echo -e "${YELLOW}When ready to simulate the traffic surge (Part 3):${NC}"
echo -e "  ${BLUE}./demo-scripts/bank-app/setup-flow.sh${NC}"
echo -e ""

exit 0

# Made with Bob
