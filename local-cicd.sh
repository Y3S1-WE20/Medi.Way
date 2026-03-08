#!/bin/bash

# ============================================================
# Local CI/CD Management Script
# ============================================================
# Usage: ./local-cicd.sh [command]
# Commands: start, stop, restart, status, logs, clean, test
# ============================================================

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
JENKINS_SERVICE="jenkins-lts"

# Helper functions
print_header() {
    echo -e"${BLUE}════════════════════════════════════════════════════════${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}════════════════════════════════════════════════════════${NC}"
}

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
    echo -e "${BLUE}ℹ️  $1${NC}"
}

# Check if Docker is running
check_docker() {
    if ! docker info > /dev/null 2>&1; then
        print_error "Docker is not running. Please start Docker Desktop."
        exit 1
    fi
    print_success "Docker is running"
}

# Check if Jenkins is running
check_jenkins() {
    if brew services list | grep -q "${JENKINS_SERVICE}.*started"; then
        print_success "Jenkins is running"
        return 0
    else
        print_warning "Jenkins is not running"
        return 1
    fi
}

# Start all services
start_services() {
    print_header "Starting Local CI/CD Services"
    
    # Check Docker
    check_docker
    
    # Start Jenkins
    print_info "Starting Jenkins..."
    brew services start ${JENKINS_SERVICE}
    sleep 5
    
    if check_jenkins; then
        print_success "Jenkins started successfully"
        print_info "Jenkins UI: http://localhost:8080"
    else
        print_error "Failed to start Jenkins"
        exit 1
    fi
    
    # Start application containers
    print_info "Starting application containers..."
    cd "${PROJECT_DIR}"
    docker-compose up -d
    
    print_success "All services started!"
    echo ""
    print_info "Access points:"
    echo "  • Jenkins:    http://localhost:8080"
    echo "  • Frontend:   http://localhost"
    echo "  • Backend:    http://localhost:8081"
    echo "  • MySQL:      localhost:3306"
}

# Stop all services
stop_services() {
    print_header "Stopping Local CI/CD Services"
    
    # Stop application containers
    print_info "Stopping application containers..."
    cd "${PROJECT_DIR}"
    docker-compose down
    print_success "Containers stopped"
    
    # Stop Jenkins
    print_info "Stopping Jenkins..."
    brew services stop ${JENKINS_SERVICE}
    print_success "Jenkins stopped"
    
    print_success "All services stopped!"
}

# Restart all services
restart_services() {
    print_header "Restarting Local CI/CD Services"
    stop_services
    echo ""
    sleep 3
    start_services
}

# Show status of all services
show_status() {
    print_header "Local CI/CD Service Status"
    
    # Docker status
    echo -e "\n${BLUE}Docker:${NC}"
    if docker info > /dev/null 2>&1; then
        print_success "Running"
    else
        print_error "Not running"
    fi
    
    # Jenkins status
    echo -e "\n${BLUE}Jenkins:${NC}"
    if check_jenkins; then
        echo "  Status: Running"
        echo "  URL: http://localhost:8080"
    else
        echo "  Status: Stopped"
    fi
    
    # Application containers status
    echo -e "\n${BLUE}Application Containers:${NC}"
    cd "${PROJECT_DIR}"
    docker-compose ps
    
    # Port usage
    echo -e "\n${BLUE}Port Usage:${NC}"
    lsof -i :80 2>/dev/null | grep LISTEN || echo "  Port 80: Available"
    lsof -i :8080 2>/dev/null | grep LISTEN || echo "  Port 8080: Available"
    lsof -i :8081 2>/dev/null | grep LISTEN || echo "  Port 8081: Available"
    lsof -i :3306 2>/dev/null | grep LISTEN || echo "  Port 3306: Available"
}

# Show logs
show_logs() {
    print_header "Service Logs"
    
    echo -e "\n${BLUE}Choose logs to view:${NC}"
    echo "1) Application (all containers)"
    echo "2) Backend only"
    echo "3) Frontend only"
    echo "4) MySQL only"
    echo "5) Jenkins"
    read -p "Enter choice (1-5): " choice
    
    case $choice in
        1)
            cd "${PROJECT_DIR}"
            docker-compose logs -f
            ;;
        2)
            docker logs -f mediway-backend
            ;;
        3)
            docker logs -f mediway-frontend
            ;;
        4)
            docker logs -f mediway-mysql
            ;;
        5)
            tail -f ~/.jenkins/logs/jenkins.log
            ;;
        *)
            print_error "Invalid choice"
            ;;
    esac
}

# Clean up
clean_all() {
    print_header "Cleaning Up Local CI/CD Environment"
    
    print_warning "This will:"
    echo "  • Stop all containers"
    echo "  • Remove all containers"
    echo "  • Remove all volumes (including database data)"
    echo "  • Clean up Docker build cache"
    echo "  • Clean up old images"
    echo ""
    read -p "Are you sure? (yes/no): " confirm
    
    if [ "$confirm" != "yes" ]; then
        print_info "Cleanup cancelled"
        exit 0
    fi
    
    # Stop and remove containers
    print_info "Stopping and removing containers..."
    cd "${PROJECT_DIR}"
    docker-compose down -v
    
    # Clean Docker system
    print_info "Cleaning Docker system..."
    docker system prune -af
    
    # Clean Jenkins workspace
    print_info "Cleaning Jenkins workspace..."
    rm -rf ~/.jenkins/workspace/Mediway-Local-CI-CD
    
    print_success "Cleanup complete!"
}

# Run tests locally
run_tests() {
    print_header "Running Tests Locally"
    
    cd "${PROJECT_DIR}/backend"
    print_info "Running Maven tests..."
    mvn clean test
    
    print_success "Tests completed!"
    print_info "Test reports available at:"
    echo "  • backend/test-output/index.html"
    echo "  • backend/target/surefire-reports/"
}

# Trigger Jenkins build
trigger_build() {
    print_header "Triggering Jenkins Build"
    
    if ! check_jenkins; then
        print_error "Jenkins is not running. Start it first with: ./local-cicd.sh start"
        exit 1
    fi
    
    print_info "Opening Jenkins in browser..."
    open "http://localhost:8080/job/Mediway-Local-CI-CD/"
    
    print_info "Click 'Build Now' to start the pipeline"
}

# Show help
show_help() {
    cat << EOF
Local CI/CD Management Script

Usage: ./local-cicd.sh [command]

Commands:
    start       Start all services (Jenkins + application containers)
    stop        Stop all services
    restart     Restart all services
    status      Show status of all services
    logs        View logs (interactive)
    clean       Clean up everything (containers, volumes, cache)
    test        Run tests locally
    build       Trigger Jenkins build
    help        Show this help message

Examples:
    ./local-cicd.sh start               # Start everything
    ./local-cicd.sh status              # Check what's running
    ./local-cicd.sh logs                # View logs
    ./local-cicd.sh test                # Run tests locally
    ./local-cicd.sh build               # Trigger Jenkins build

URLs:
    Jenkins:   http://localhost:8080
    Frontend:  http://localhost
    Backend:   http://localhost:8081

For detailed documentation, see:
    docs/LOCAL_JENKINS_CICD_GUIDE.md

EOF
}

# Main script logic
main() {
    case "${1:-help}" in
        start)
            start_services
            ;;
        stop)
            stop_services
            ;;
        restart)
            restart_services
            ;;
        status)
            show_status
            ;;
        logs)
            show_logs
            ;;
        clean)
            clean_all
            ;;
        test)
            run_tests
            ;;
        build)
            trigger_build
            ;;
        help|--help|-h)
            show_help
            ;;
        *)
            print_error "Unknown command: $1"
            echo ""
            show_help
            exit 1
            ;;
    esac
}

# Run main function
main "$@"
