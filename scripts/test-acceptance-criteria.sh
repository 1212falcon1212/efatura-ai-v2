#!/bin/bash

# Stage 4 Acceptance Criteria Test Script
# Bu script Stage 4 kabul kriterlerini test eder

set -e

echo "=========================================="
echo "Stage 4 Acceptance Criteria Tests"
echo "=========================================="
echo ""

# Colors
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Test counters
PASSED=0
FAILED=0

# Test function
test_check() {
    local test_name=$1
    local command=$2
    
    echo -n "Testing: $test_name... "
    if eval "$command" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ PASSED${NC}"
        ((PASSED++))
        return 0
    else
        echo -e "${RED}✗ FAILED${NC}"
        ((FAILED++))
        return 1
    fi
}

# Check if Docker is running
echo "1. Checking Docker..."
if ! docker info > /dev/null 2>&1; then
    echo -e "${RED}✗ Docker is not running${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Docker is running${NC}"
echo ""

# Check if docker-compose.prod.yml exists
echo "2. Checking docker-compose.prod.yml..."
if [ ! -f "docker-compose.prod.yml" ]; then
    echo -e "${RED}✗ docker-compose.prod.yml not found${NC}"
    exit 1
fi
echo -e "${GREEN}✓ docker-compose.prod.yml exists${NC}"
echo ""

# Stop any existing containers
echo "3. Cleaning up existing containers..."
docker-compose -f docker-compose.prod.yml down -v 2>/dev/null || true
echo ""

# Start services
echo "4. Starting production services..."
echo "This may take a few minutes..."
docker-compose -f docker-compose.prod.yml up -d --build

# Wait for services to be healthy
echo ""
echo "5. Waiting for services to be healthy..."
sleep 10

# Wait for PostgreSQL
echo "Waiting for PostgreSQL..."
timeout=60
elapsed=0
while ! docker exec efatura-postgres-prod pg_isready -U efatura -d efatura > /dev/null 2>&1; do
    if [ $elapsed -ge $timeout ]; then
        echo -e "${RED}✗ PostgreSQL failed to start${NC}"
        exit 1
    fi
    sleep 2
    elapsed=$((elapsed + 2))
done
echo -e "${GREEN}✓ PostgreSQL is ready${NC}"

# Wait for RabbitMQ
echo "Waiting for RabbitMQ..."
timeout=60
elapsed=0
while ! docker exec efatura-rabbitmq-prod rabbitmq-diagnostics ping > /dev/null 2>&1; do
    if [ $elapsed -ge $timeout ]; then
        echo -e "${RED}✗ RabbitMQ failed to start${NC}"
        exit 1
    fi
    sleep 2
    elapsed=$((elapsed + 2))
done
echo -e "${GREEN}✓ RabbitMQ is ready${NC}"

# Wait for MinIO
echo "Waiting for MinIO..."
timeout=60
elapsed=0
while ! curl -f http://localhost:9000/minio/health/live > /dev/null 2>&1; do
    if [ $elapsed -ge $timeout ]; then
        echo -e "${RED}✗ MinIO failed to start${NC}"
        exit 1
    fi
    sleep 2
    elapsed=$((elapsed + 2))
done
echo -e "${GREEN}✓ MinIO is ready${NC}"

# Wait for Backend
echo "Waiting for Backend..."
timeout=120
elapsed=0
while ! curl -f http://localhost:8080/actuator/health > /dev/null 2>&1; do
    if [ $elapsed -ge $timeout ]; then
        echo -e "${RED}✗ Backend failed to start${NC}"
        docker-compose -f docker-compose.prod.yml logs backend | tail -50
        exit 1
    fi
    sleep 5
    elapsed=$((elapsed + 5))
done
echo -e "${GREEN}✓ Backend is ready${NC}"

# Wait for Frontend
echo "Waiting for Frontend..."
timeout=60
elapsed=0
while ! curl -f http://localhost:80/health > /dev/null 2>&1; do
    if [ $elapsed -ge $timeout ]; then
        echo -e "${RED}✗ Frontend failed to start${NC}"
        exit 1
    fi
    sleep 2
    elapsed=$((elapsed + 2))
done
echo -e "${GREEN}✓ Frontend is ready${NC}"

echo ""
echo "=========================================="
echo "Running Acceptance Criteria Tests"
echo "=========================================="
echo ""

# Test 1: Docker Compose up works
test_check "Docker Compose Production" "docker-compose -f docker-compose.prod.yml ps | grep -q 'Up'"

# Test 2: Backend health endpoint
test_check "Backend /actuator/health UP" "curl -f http://localhost:8080/actuator/health | grep -q '\"status\":\"UP\"'"

# Test 3: Backend readiness probe
test_check "Backend /actuator/health/readiness" "curl -f http://localhost:8080/actuator/health/readiness"

# Test 4: Backend liveness probe
test_check "Backend /actuator/health/liveness" "curl -f http://localhost:8080/actuator/health/liveness"

# Test 5: Frontend health endpoint
test_check "Frontend /health" "curl -f http://localhost:80/health"

# Test 6: Prometheus metrics endpoint
test_check "Backend /actuator/prometheus" "curl -f http://localhost:8080/actuator/prometheus | grep -q 'jvm_memory_used_bytes'"

# Test 7: Backend info endpoint
test_check "Backend /actuator/info" "curl -f http://localhost:8080/actuator/info"

# Test 8: Frontend serves index.html
test_check "Frontend serves index.html" "curl -f http://localhost:80/ | grep -q '<!DOCTYPE html>'"

# Test 9: Backend API endpoint accessible
test_check "Backend API accessible" "curl -f http://localhost:8080/api/health 2>/dev/null || curl -f http://localhost:8080/actuator/health"

# Test 10: All containers are running
test_check "All containers running" "[ \$(docker-compose -f docker-compose.prod.yml ps | grep -c 'Up') -ge 5 ]"

echo ""
echo "=========================================="
echo "Test Results"
echo "=========================================="
echo -e "${GREEN}Passed: $PASSED${NC}"
echo -e "${RED}Failed: $FAILED${NC}"
echo ""

if [ $FAILED -eq 0 ]; then
    echo -e "${GREEN}✓ All acceptance criteria tests passed!${NC}"
    echo ""
    echo "Services are running:"
    echo "  - Backend: http://localhost:8080"
    echo "  - Frontend: http://localhost:80"
    echo "  - PostgreSQL: localhost:5432"
    echo "  - RabbitMQ: localhost:5672 (Management: http://localhost:15672)"
    echo "  - MinIO: http://localhost:9000 (Console: http://localhost:9001)"
    echo ""
    echo "To stop services: docker-compose -f docker-compose.prod.yml down"
    exit 0
else
    echo -e "${RED}✗ Some tests failed. Check logs above.${NC}"
    echo ""
    echo "To view logs: docker-compose -f docker-compose.prod.yml logs"
    exit 1
fi
