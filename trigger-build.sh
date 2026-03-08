#!/bin/bash

# ============================================================
# Trigger Jenkins Build - Medi.Way Local CI/CD
# ============================================================
# This script triggers the Jenkins pipeline build and monitors progress
# ============================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
MAGENTA='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
JENKINS_URL="http://localhost:8080"
JOB_NAME="Mediway-Local-CI-CD"
USER="admin"

echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Medi.Way CI/CD Pipeline - Build Trigger${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo ""

# Function to print colored messages
print_success() {
    echo -e "${GREEN}✅ $1${NC}"
}

print_error() {
    echo -e "${RED}❌ $1${NC}"
}

print_warning() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

print_info() {
    echo -e "${CYAN}ℹ️  $1${NC}"
}

print_stage() {
    echo -e "${MAGENTA}🔹 $1${NC}"
}

# Check if Jenkins is running
echo -e "${BLUE}Step 1: Checking Jenkins Status...${NC}"
if curl -s -o /dev/null -w "%{http_code}" "$JENKINS_URL" | grep -q "200\|403"; then
    print_success "Jenkins is running at $JENKINS_URL"
else
    print_error "Jenkins is not accessible at $JENKINS_URL"
    print_info "Start Jenkins with: brew services start jenkins-lts"
    exit 1
fi

# Check if Docker is running
echo ""
echo -e "${BLUE}Step 2: Checking Docker Status...${NC}"
if docker info > /dev/null 2>&1; then
    print_success "Docker is running"
else
    print_error "Docker is not running"
    print_info "Start Docker Desktop from Applications"
    exit 1
fi

# Check if job exists
echo ""
echo -e "${BLUE}Step 3: Verifying Pipeline Job...${NC}"
if curl -s "$JENKINS_URL/job/$JOB_NAME/" | grep -q "$JOB_NAME"; then
    print_success "Pipeline job '$JOB_NAME' found"
else
    print_warning "Cannot verify job existence (may need authentication)"
    print_info "Please ensure '$JOB_NAME' job is created in Jenkins"
fi

# Display environment info
echo ""
echo -e "${BLUE}Step 4: Environment Information${NC}"
echo "  • Jenkins URL: $JENKINS_URL"
echo "  • Job Name: $JOB_NAME"
echo "  • Docker Status: Running"
echo "  • Workspace: $(pwd)"

# Trigger build
echo ""
echo -e "${BLUE}Step 5: Triggering Build...${NC}"
print_info "Opening Jenkins in browser..."
open "$JENKINS_URL/job/$JOB_NAME/"

echo ""
print_warning "Please click 'Build Now' button in the Jenkins UI"
print_info "URL: $JENKINS_URL/job/$JOB_NAME/"
echo ""

# Wait for user confirmation
read -p "Press ENTER once you've clicked 'Build Now'..."

# Open console output
echo ""
print_info "Opening build console output..."
sleep 2
open "$JENKINS_URL/job/$JOB_NAME/lastBuild/console"

echo ""
echo -e "${CYAN}════════════════════════════════════════════════════════${NC}"
echo -e "${CYAN}  Expected Pipeline Stages:${NC}"
echo -e "${CYAN}════════════════════════════════════════════════════════${NC}"
echo ""
echo "  1. 📥 Checkout           (~5 seconds)"
echo "  2. 🔨 Build Backend      (~1 minute)"
echo "  3. 🧪 Run TestNG Tests   (~15 seconds)"
echo "  4. 📦 Package            (~30 seconds)"
echo "  5. 🐳 Build Docker       (~2 minutes)"
echo "  6. ⬆️  Push to DockerHub  (~1 minute)"
echo "  7. 🚀 Deploy Locally     (~30 seconds)"
echo "  8. ❤️  Health Check       (~45 seconds)"
echo ""
echo -e "${CYAN}  Total Expected Time: ~6 minutes${NC}"
echo -e "${CYAN}════════════════════════════════════════════════════════${NC}"
echo ""

# Monitor build progress
echo -e "${BLUE}Step 6: Monitoring Build...${NC}"
print_info "Waiting for build to start..."
sleep 10

# Function to check build status
check_build_status() {
    local build_url="$JENKINS_URL/job/$JOB_NAME/lastBuild/api/json"
    local status=$(curl -s "$build_url" 2>/dev/null | grep -o '"building":[^,]*' | cut -d':' -f2)
    echo "$status"
}

# Monitor loop
echo ""
print_info "Monitoring build progress (refresh every 10 seconds)..."
echo "  Press Ctrl+C to stop monitoring (build will continue)"
echo ""

BUILD_COUNT=0
while true; do
    BUILD_COUNT=$((BUILD_COUNT + 1))
    
    # Check if building
    if curl -s "$JENKINS_URL/job/$JOB_NAME/lastBuild/api/json" 2>/dev/null | grep -q '"building":false'; then
        echo ""
        print_success "Build completed!"
        
        # Check result
        if curl -s "$JENKINS_URL/job/$JOB_NAME/lastBuild/api/json" 2>/dev/null | grep -q '"result":"SUCCESS"'; then
            echo ""
            echo -e "${GREEN}╔══════════════════════════════════════════════════════════════╗${NC}"
            echo -e "${GREEN}║                    BUILD SUCCESS! 🎉                         ║${NC}"
            echo -e "${GREEN}╠══════════════════════════════════════════════════════════════╣${NC}"
            echo -e "${GREEN}║  All stages completed successfully                           ║${NC}"
            echo -e "${GREEN}╚══════════════════════════════════════════════════════════════╝${NC}"
            echo ""
            print_info "Application deployed and running at:"
            echo "  • Frontend:  http://localhost"
            echo "  • Backend:   http://localhost:8081"
            echo "  • API:       http://localhost:8081/api/doctors"
            echo ""
            print_info "View full report: $JENKINS_URL/job/$JOB_NAME/lastBuild/"
            echo ""
            
            # Verify deployment
            echo -e "${BLUE}Verifying Deployment...${NC}"
            sleep 5
            
            if docker ps | grep -q "mediway"; then
                print_success "Docker containers running:"
                docker ps --filter "name=mediway" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
            else
                print_warning "No mediway containers found running yet"
                print_info "They may still be starting up..."
            fi
            
            echo ""
            print_info "Testing application endpoints..."
            
            # Test frontend
            if curl -s -o /dev/null -w "%{http_code}" http://localhost | grep -q "200\|304"; then
                print_success "Frontend is accessible at http://localhost"
            else
                print_warning "Frontend not yet accessible (may need more time)"
            fi
            
            # Test backend
            sleep 10
            if curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api/doctors | grep -q "200\|500"; then
                print_success "Backend API is accessible at http://localhost:8081/api/doctors"
            else
                print_warning "Backend not yet accessible (may need more time)"
            fi
            
            echo ""
            print_success "Pipeline execution completed successfully!"
            exit 0
        else
            echo ""
            echo -e "${RED}╔══════════════════════════════════════════════════════════════╗${NC}"
            echo -e "${RED}║                    BUILD FAILED! ❌                           ║${NC}"
            echo -e "${RED}╠══════════════════════════════════════════════════════════════╣${NC}"
            echo -e "${RED}║  One or more stages failed                                   ║${NC}"
            echo -e "${RED}╚══════════════════════════════════════════════════════════════╝${NC}"
            echo ""
            print_error "Check console output: $JENKINS_URL/job/$JOB_NAME/lastBuild/console"
            exit 1
        fi
    else
        # Still building
        echo -ne "\r  ⏳ Build in progress... (check #$BUILD_COUNT, elapsed: $((BUILD_COUNT * 10))s)"
        sleep 10
    fi
    
    # Timeout after 15 minutes
    if [ $BUILD_COUNT -gt 90 ]; then
        echo ""
        print_warning "Build monitoring timeout (15 minutes)"
        print_info "Build may still be running. Check: $JENKINS_URL/job/$JOB_NAME/lastBuild/"
        exit 0
    fi
done
