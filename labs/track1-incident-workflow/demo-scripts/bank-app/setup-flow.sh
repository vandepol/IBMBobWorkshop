#!/bin/bash
source "$(dirname "$0")/../docker-host.sh"
# Setup for Flow 3: Combined Performance & Scaling Issue
# This demo shows how infrastructure scaling resolves both overload AND performance issues

set -e

# Get the directory where this script is located
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
# Get the project root (two levels up from script location)
PROJECT_ROOT="$( cd "$SCRIPT_DIR/../.." && pwd )"

echo "🔧 Setting up Flow: Performance & Scaling Issue"
echo "==========================================================="
echo ""
echo "This demo simulates a realistic scenario where:"
echo "  • Single backend replica is overloaded (under-provisioned)"
echo "  • High load causes 3+ second response times"
echo "  • Scaling to 3 replicas resolves BOTH issues"
echo ""

# 1. Ensure Terraform is configured for single replica
echo "Step 1: Configuring infrastructure for single replica..."
cd "$PROJECT_ROOT/bank-app/terraform"

# Update terraform.tfvars to use 1 replica
if grep -q "backend_replicas" terraform.tfvars; then
    sed -i.bak 's/backend_replicas = [0-9]*/backend_replicas = 1/' terraform.tfvars
    rm -f terraform.tfvars.bak
    echo "✅ Configured for 1 backend replica (simulating under-provisioned infrastructure)"
else
    echo "backend_replicas = 1" >> terraform.tfvars
    echo "✅ Added backend_replicas = 1 to terraform.tfvars"
fi

# 2. Deploy or verify application is running
echo ""
echo "Step 2: Deploying application with Terraform..."
if ! docker ps | grep -q "bank-app-dev-backend"; then
    echo "⚠️  Application not running. Deploying..."
    terraform init -upgrade > /dev/null 2>&1
    terraform apply -auto-approve
    echo "✅ Application deployed with 1 backend replica"
else
    echo "✅ Application already running"
    # Verify it's running with 1 replica
    BACKEND_COUNT=$(docker ps --filter "name=bank-app-dev-backend" --format "{{.Names}}" | wc -l | tr -d ' ')
    if [ "$BACKEND_COUNT" -ne 1 ]; then
        echo "⚠️  Found $BACKEND_COUNT backend instances, expected 1"
        echo "   Redeploying to ensure single replica..."
        terraform apply -auto-approve
    fi
fi

cd "$SCRIPT_DIR"

# Wait for backend to be ready
echo ""
echo "Step 3: Waiting for backend to be ready..."
for i in {1..30}; do
    if curl -s --max-time 2 http://localhost:5001/health > /dev/null 2>&1; then
        echo "✅ Backend is ready"
        break
    fi
    if [ $i -eq 30 ]; then
        echo "❌ Backend failed to start after 30 seconds"
        exit 1
    fi
    sleep 1
done

# 4. Enable overload metrics (simulating high load on single instance)
echo ""
echo "Step 4: Simulating server overload conditions..."
echo "Setting high load metrics (CPU 95%, Memory 88%, 450 req/s)..."

LOAD_RESPONSE=$(curl -s -X POST http://localhost:5001/api/admin/load \
  -H "Content-Type: application/json" \
  -d '{
    "cpuUsage": 95,
    "memoryUsage": 88,
    "requestsPerSecond": 450,
    "activeConnections": 280,
    "overloaded": true
  }')

if echo "$LOAD_RESPONSE" | grep -q "overloaded"; then
    echo "✅ Server overload simulation enabled"
    echo "   CPU Usage: 95%"
    echo "   Memory Usage: 88%"
    echo "   Requests/sec: 450"
    echo "   Active Connections: 280"
    echo "   Status: ⚠️ OVERLOADED"
else
    echo "⚠️  Could not set load metrics. Response: $LOAD_RESPONSE"
fi

# 5. Enable artificial delay (consequence of overload)
echo ""
echo "Step 5: Enabling performance degradation (3-second delays)..."
echo "This simulates the performance impact of the overloaded server..."

DELAY_RESPONSE=$(curl -s -X POST http://localhost:5001/api/admin/delay \
  -H "Content-Type: application/json" \
  -d '{"delay": 3000}')

if echo "$DELAY_RESPONSE" | grep -q "3000"; then
    echo "✅ Performance degradation enabled: 3000ms delay"
    echo "   All API requests will now take 3+ seconds"
else
    echo "⚠️  Could not enable delay. Response: $DELAY_RESPONSE"
fi

# 6. Verify the problem is detectable
echo ""
echo "Step 6: Verifying issues are detectable..."

# Check metrics endpoint
echo "Checking metrics endpoint..."
METRICS=$(curl -s http://localhost:5001/api/admin/metrics)
if echo "$METRICS" | grep -q "overloaded.*true"; then
    echo "✅ Overload condition confirmed"
fi

# Check response time
echo "Testing response time (should take ~3 seconds)..."
START=$(date +%s)
curl -s http://localhost:5001/health > /dev/null
END=$(date +%s)
DURATION=$((END - START))

if [ $DURATION -ge 3 ]; then
    echo "✅ Performance degradation confirmed: ${DURATION}s response time"
else
    echo "⚠️  Response time: ${DURATION}s (expected 3+s)"
fi

# 7. Display current state
echo ""
echo "Step 7: Current infrastructure state"
echo "📊 Backend Instances:"
docker ps --filter "name=bank-app-dev-backend" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"

echo ""
echo "💾 Simulated Resource Usage:"
echo "   CPU: 95% (overloaded)"
echo "   Memory: 88% (high)"
echo "   Requests/sec: 450 (exceeding capacity)"
echo "   Response Time: 3+ seconds (degraded)"

# 8. Demo instructions
echo ""
echo "✅ Flow setup Complete!"
echo ""
echo "🎬 Ready for Demo! Next steps:"
echo "   1. Switch Bob to '🎫 SDLC Incident Manager' mode"
echo "   2. Tell Bob:"
echo "      \"Users reporting severe performance issues. Application is very slow,"
echo "      \"taking 3-5 seconds to load pages.\""
echo "🛑 To shutdown: ./shutdown-flow.sh"

# Made with Bob