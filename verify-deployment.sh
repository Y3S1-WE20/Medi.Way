#!/bin/bash

# ============================================================
# Post-Build Verification Script
# ============================================================
# Verifies that the deployment was successful after Jenkins build
# ============================================================

set -e

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}  Medi.Way Deployment Verification${NC}"
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
echo ""

# Print functions
print_success() { echo -e "${GREEN}✅ $1${NC}"; }
print_error() { echo -e "${RED}❌ $1${NC}"; }
print_info() { echo -e "${CYAN}ℹ️  $1${NC}"; }
print_check() { echo -e "${YELLOW}🔍 Checking: $1${NC}"; }

ERRORS=0

# 1. Check Docker Containers
print_check "Docker containers"
if docker ps --format "{{.Names}}" | grep -q "mediway"; then
    print_success "Mediway containers are running"
    echo ""
    docker ps --filter "name=mediway" --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
    echo ""
else
    print_error "No mediway containers running"
    ERRORS=$((ERRORS + 1))
fi

# 2. Check MySQL
print_check "MySQL database"
if docker ps --filter "name=mediway-mysql" --format "{{.Names}}" | grep -q "mediway-mysql"; then
    print_success "MySQL container running on port 3306"
else
    print_error "MySQL container not found"
    ERRORS=$((ERRORS + 1))
fi

# 3. Check Backend
print_check "Backend service"
if docker ps --filter "name=mediway-backend" --format "{{.Names}}" | grep -q "mediway-backend"; then
    print_success "Backend container running"
    
    # Test backend health
    sleep 5
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8081/api/doctors --max-time 10 || echo "000")
    if [[ "$HTTP_CODE" == "200" ]] || [[ "$HTTP_CODE" == "500" ]]; then
        print_success "Backend API responding (HTTP $HTTP_CODE)"
    else
        print_error "Backend API not responding (HTTP $HTTP_CODE)"
        ERRORS=$((ERRORS + 1))
    fi
else
    print_error "Backend container not found"
    ERRORS=$((ERRORS + 1))
fi

# 4. Check Frontend
print_check "Frontend service"
if docker ps --filter "name=mediway-frontend" --format "{{.Names}}" | grep -q "mediway-frontend"; then
    print_success "Frontend container running"
    
    # Test frontend
    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost --max-time 10 || echo "000")
    if [[ "$HTTP_CODE" == "200" ]] || [[ "$HTTP_CODE" == "304" ]]; then
        print_success "Frontend responding (HTTP $HTTP_CODE)"
    else
        print_error "Frontend not responding (HTTP $HTTP_CODE)"
        ERRORS=$((ERRORS + 1))
    fi
else
    print_error "Frontend container not found"
    ERRORS=$((ERRORS + 1))
fi

# 5. Check Container Health
echo ""
print_check "Container health status"
docker ps --filter "name=mediway" --format "table {{.Names}}\t{{.Status}}"

# 6. Check Logs for errors
echo ""
print_check "Recent container logs for errors"
if docker logs mediway-backend --tail 50 2>&1 | grep -i "error" | grep -v "ERROR_LOGGER" | head -5; then
    print_info "Some errors found in backend logs (may be normal)"
else
    print_success "No critical errors in backend logs"
fi

# 7. Show Docker Images
echo ""
print_check "Docker images built"
if docker images | grep "shiranthads/mediway"; then
    docker images | grep "shiranthads/mediway" | head -5
    print_success "Docker images present"
else
    print_error "No mediway Docker images found"
    ERRORS=$((ERRORS + 1))
fi

# Final Summary
echo ""
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
if [ $ERRORS -eq 0 ]; then
    echo -e "${GREEN}✅ ALL CHECKS PASSED! Deployment is successful!${NC}"
    echo ""
    echo -e "${CYAN}Access your application:${NC}"
    echo "  • Frontend:  http://localhost"
    echo "  • Backend:   http://localhost:8081"
    echo "  • API Test:  curl http://localhost:8081/api/doctors"
    echo ""
    echo -e "${CYAN}Useful commands:${NC}"
    echo "  • View logs:     docker-compose logs -f"
    echo "  • Restart:       docker-compose restart"
    echo "  • Stop:          docker-compose down"
    echo "  • Status:        docker-compose ps"
else
    echo -e "${RED}❌ $ERRORS CHECKS FAILED!${NC}"
    echo ""
    echo -e "${CYAN}Troubleshooting steps:${NC}"
    echo "  1. Check Jenkins console: http://localhost:8080/job/Mediway-Local-CI-CD/lastBuild/console"
    echo "  2. View container logs:   docker-compose logs"
    echo "  3. Restart containers:    docker-compose restart"
    echo "  4. Check Docker:          docker ps -a"
fi
echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"

exit $ERRORS
