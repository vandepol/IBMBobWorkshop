#!/bin/bash
set -e

echo "=========================================="
echo "Simple Pharmacy Dashboard"
echo "Starting WebSphere Application Server"
echo "=========================================="
echo ""

# Configure WebSphere
echo "Configuring WebSphere..."
/work/configure.sh

# Deploy application
echo ""
echo "Deploying simple-pharmacy.war..."
/opt/IBM/WebSphere/AppServer/bin/wsadmin.sh -lang jython -conntype NONE -f /work/deploy-app.py

# Start server
echo ""
echo "Starting application server..."
/opt/IBM/WebSphere/AppServer/bin/startServer.sh server1

echo ""
echo "=========================================="
echo "WebSphere Application Server Started"
echo "Application: http://localhost:9080/pharmacy/"
echo "Admin Console: http://localhost:9060/ibm/console/"
echo "=========================================="
echo ""

# Tail logs
tail -f /opt/IBM/WebSphere/AppServer/profiles/AppSrv01/logs/server1/SystemOut.log

# Made with Bob
