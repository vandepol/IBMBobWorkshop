# Use IBM WebSphere Application Server Traditional base image
# Note: This is a large image (~2GB). First pull may take several minutes.
# For Apple Silicon (M1/M2/M3), this will run under x86_64 emulation
FROM --platform=linux/amd64 ibmcom/websphere-traditional:latest

# Set maintainer
LABEL maintainer="pharmacy-app"
LABEL description="Simple Pharmacy Dashboard on WebSphere Traditional"

# Copy the WAR file to WebSphere deployment directory
COPY target/simple-pharmacy.war /work/config/

# Copy deployment scripts (start-and-deploy.sh should already be executable)
COPY docker/deploy-app.py /work/deploy-app.py
COPY docker/start-and-deploy.sh /work/start-and-deploy.sh

# Expose WebSphere ports
# 9060 - Admin Console (HTTP)
# 9043 - Admin Console (HTTPS)
# 9080 - Application (HTTP)
# 9443 - Application (HTTPS)
EXPOSE 9060 9043 9080 9443

# Set working directory
WORKDIR /work

# Start WebSphere and deploy application
CMD ["/bin/bash", "/work/start-and-deploy.sh"]