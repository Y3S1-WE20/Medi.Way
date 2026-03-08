# Local Jenkins CI/CD Setup Guide (for Mac)

## 🎯 Purpose
Run complete CI/CD pipeline locally on your Mac without AWS, deploying to localhost.

## 📋 Prerequisites
- macOS (your current system)
- Docker Desktop installed and running
- Java 17 installed
- Maven installed
- 8GB+ RAM recommended
- 20GB+ free disk space

---

## 📦 Part 1: Install Jenkins Locally

### Step 1: Install Jenkins via Homebrew

```bash
# Install Homebrew if not already installed
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"

# Install Jenkins LTS
brew install jenkins-lts

# Check Jenkins version
jenkins-lts --version
```

**Expected Output**: `Jenkins 2.x.x`

### Step 2: Start Jenkins

```bash
# Start Jenkins as a service
brew services start jenkins-lts

# Check if Jenkins is running
brew services list | grep jenkins
```

**Jenkins will be available at**: http://localhost:8080

### Step 3: Initial Jenkins Setup

1. **Get Initial Admin Password**:
```bash
cat ~/.jenkins/secrets/initialAdminPassword
```

2. **Open Jenkins in Browser**:
   - Go to: http://localhost:8080
   - Paste the admin password

3. **Install Suggested Plugins**:
   - Click "Install suggested plugins"
   - Wait for installation (5-10 minutes)

4. **Create Admin User**:
   - Username: `admin`
   - Password: `admin123` (or your choice)
   - Full name: `Your Name`
   - Email: `your.email@example.com`

5. **Jenkins URL**: Keep as `http://localhost:8080/`

---

## 🔧 Part 2: Configure Jenkins for Local Deployment

### Step 1: Install Required Plugins

1. Go to: **Manage Jenkins** → **Manage Plugins** → **Available**

2. Install these plugins:
   - ✅ Pipeline
   - ✅ Git plugin
   - ✅ GitHub plugin
   - ✅ Docker Pipeline
   - ✅ TestNG Results plugin
   - ✅ HTML Publisher plugin

3. Click **Install without restart**

### Step 2: Configure Tools

#### A) Configure Maven

1. Go to: **Manage Jenkins** → **Global Tool Configuration**
2. Scroll to **Maven**
3. Click **Add Maven**
   - Name: `Maven-3.9`
   - ✅ Install automatically
   - Version: Latest 3.9.x
4. Click **Save**

#### B) Configure JDK

1. In **Global Tool Configuration**
2. Scroll to **JDK**
3. Click **Add JDK**
   - Name: `JDK-17`
   - ✅ Install automatically
   - Version: Java 17
4. Click **Save**

### Step 3: Configure Docker Access

```bash
# Add Jenkins user to docker group (allows Jenkins to use Docker)
# This is automatically handled on Mac with Docker Desktop
# Just ensure Docker Desktop is running

# Verify Docker works
docker ps
```

### Step 4: Set Up DockerHub Credentials

1. Go to: **Manage Jenkins** → **Manage Credentials**
2. Click **(global)** domain
3. Click **Add Credentials**
   - Kind: `Username with password`
   - Username: `shiranthads`
   - Password: `YOUR_DOCKERHUB_PASSWORD`
   - ID: `dockerhub-credentials`
   - Description: `DockerHub credentials`
4. Click **Create**

---

## 🚀 Part 3: Create Local Pipeline

### Step 1: Create Pipeline Job

1. Go to Jenkins Dashboard
2. Click **New Item**
3. Enter name: `Mediway-Local-CI-CD`
4. Select **Pipeline**
5. Click **OK**

### Step 2: Configure Pipeline

1. **Description**: `Local CI/CD pipeline for Medi.Way Healthcare System`

2. **Build Triggers**:
   - ✅ **Poll SCM**: `H/5 * * * *` (check every 5 minutes)
   
3. **Pipeline**:
   - Definition: `Pipeline script from SCM`
   - SCM: `Git`
   - Repository URL: `/Users/shiranthadissanayake/Documents/GitHub/Medi.Way`
   - Branch: `*/main`
   - Script Path: `Jenkinsfile.local`

4. Click **Save**

---

## 📝 Part 4: Create Local Jenkinsfile

The `Jenkinsfile.local` has been created in your project root with stages optimized for local deployment.

**Key Differences from AWS version:**
- ❌ No SSH deployment to remote server
- ✅ Deploys directly to localhost via docker-compose
- ✅ Health checks use localhost URLs
- ✅ Uses local file paths instead of EC2

---

## 🐳 Part 5: Local Deployment Setup

### Docker Compose for Local Deployment

The existing `docker-compose.yml` will work for local deployment. Just ensure you use local image tags.

### Local Environment Variables

Create a `.env.local` file in project root:

```bash
cat > .env.local << 'EOF'
# Local Development Environment
BUILD_NUMBER=local
DOCKER_USERNAME=shiranthads

# MySQL Configuration
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=mediway
MYSQL_USER=mediway
MYSQL_PASSWORD=mediway123

# Backend Configuration
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080

# Frontend Configuration
NODE_ENV=production
EOF
```

---

## 🎬 Part 6: Running Your First Local Build

### Step 1: Trigger Build Manually

1. Go to Jenkins: http://localhost:8080
2. Click on **Mediway-Local-CI-CD**
3. Click **Build Now**

### Step 2: Monitor Build Progress

Watch the build pipeline execute:
- ✅ Stage 1: Checkout (5 sec)
- ✅ Stage 2: Build Backend (1 min)
- ✅ Stage 3: Run TestNG Tests (15 sec)
- ✅ Stage 4: Package (30 sec)
- ✅ Stage 5: Build Docker Images (2 min)
- ✅ Stage 6: Push to DockerHub (1 min)
- ✅ Stage 7: Deploy Locally (30 sec)
- ✅ Stage 8: Health Check (45 sec)

**Total Time**: ~6 minutes

### Step 3: Access Your Application

Once build completes successfully:

```bash
# Check running containers
docker ps

# Should see:
# - mediway-mysql (port 3306)
# - mediway-backend (port 8081)
# - mediway-frontend (port 80)
```

**Access URLs**:
- 🌐 Frontend: http://localhost
- 🔧 Backend API: http://localhost:8081/api/doctors
- 🗄️ MySQL: localhost:3306

---

## 🧪 Part 7: Testing the Pipeline

### Manual Test Flow

```bash
# 1. Make a code change
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way
echo "# Test local CI/CD" >> README.md

# 2. Commit and push to GitHub
git add README.md
git commit -m "Test local Jenkins pipeline"
git push origin main

# 3. Wait for Jenkins to detect change (up to 5 minutes with Poll SCM)
# Or click "Build Now" immediately

# 4. Watch build in Jenkins dashboard
# Build progress will show in real-time

# 5. Once complete, verify deployment
curl http://localhost/
curl http://localhost:8081/api/doctors
```

### Verify Each Stage

#### Stage 1 - Checkout:
```bash
# Check Jenkins workspace
ls -la ~/.jenkins/workspace/Mediway-Local-CI-CD/
```

#### Stage 2 - Build:
```bash
# Check compiled classes
ls -la ~/.jenkins/workspace/Mediway-Local-CI-CD/backend/target/classes/
```

#### Stage 3 - Tests:
```bash
# Check test reports
open ~/.jenkins/workspace/Mediway-Local-CI-CD/backend/test-output/index.html
```

#### Stage 4 - Package:
```bash
# Check JAR file
ls -la ~/.jenkins/workspace/Mediway-Local-CI-CD/backend/target/*.jar
```

#### Stage 5 - Docker Images:
```bash
# List built images
docker images | grep mediway
```

#### Stage 6 - DockerHub:
```bash
# Verify on DockerHub (open in browser)
open https://hub.docker.com/r/shiranthads/mediway-backend/tags
```

#### Stage 7 - Deployment:
```bash
# Check containers
docker-compose ps
```

#### Stage 8 - Health Check:
```bash
# Manual health check
curl http://localhost/
curl http://localhost:8081/api/doctors
```

---

## 🔍 Part 8: Viewing Test Reports

### TestNG Results in Jenkins

1. Go to build page: http://localhost:8080/job/Mediway-Local-CI-CD/lastBuild/
2. Click **TestNG Results**
3. View:
   - ✅ Total tests: 88
   - ✅ Passed: 88
   - ✅ Failed: 0
   - ✅ Skipped: 0

### HTML Test Reports

1. Click **MEDI.WAY Test Summary**
2. View detailed test execution results
3. Drill down to individual test methods

---

## 📊 Part 9: Monitoring and Logs

### Jenkins Logs

```bash
# View Jenkins logs
tail -f ~/.jenkins/logs/jenkins.log

# View specific build logs
cat ~/.jenkins/jobs/Mediway-Local-CI-CD/builds/1/log
```

### Docker Container Logs

```bash
# View all container logs
docker-compose logs

# View specific container
docker-compose logs backend
docker-compose logs frontend
docker-compose logs mysql

# Follow logs in real-time
docker-compose logs -f backend
```

### Application Logs

```bash
# Backend logs
docker logs mediway-backend --tail 100 -f

# Frontend logs
docker logs mediway-frontend --tail 100 -f

# MySQL logs
docker logs mediway-mysql --tail 100 -f
```

---

## 🛠️ Part 10: Troubleshooting

### Issue 1: Jenkins Won't Start

**Solution**:
```bash
# Check if port 8080 is already in use
lsof -i :8080

# Kill process using port 8080
kill -9 <PID>

# Restart Jenkins
brew services restart jenkins-lts
```

### Issue 2: Docker Build Fails

**Solution**:
```bash
# Ensure Docker Desktop is running
open -a Docker

# Check Docker
docker ps

# Restart Docker service if needed
# (via Docker Desktop menu → Restart)
```

### Issue 3: Tests Fail

**Solution**:
```bash
# Run tests manually to see detailed errors
cd backend
mvn clean test

# Check test configuration
cat testng.xml
```

### Issue 4: Port Already in Use

**Solution**:
```bash
# Check what's using ports
lsof -i :80    # Frontend
lsof -i :8081  # Backend
lsof -i :3306  # MySQL

# Stop existing containers
docker-compose down

# Kill processes if needed
sudo lsof -ti:80 | xargs kill -9
```

### Issue 5: Health Check Fails

**Solution**:
```bash
# Wait longer for containers to start
sleep 60

# Check container status
docker-compose ps

# Check backend health directly
docker exec mediway-backend curl -s http://localhost:8080/actuator/health

# Check if services are listening
docker exec mediway-backend netstat -tulpn
```

---

## 🔄 Part 11: Daily Development Workflow

### Morning Routine

```bash
# 1. Start Docker Desktop
open -a Docker

# 2. Start Jenkins (if not running)
brew services start jenkins-lts

# 3. Check Jenkins status
open http://localhost:8080

# 4. Pull latest code
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way
git pull origin main
```

### After Code Changes

```bash
# 1. Run tests locally first
cd backend
mvn clean test

# 2. If tests pass, commit
git add .
git commit -m "Your descriptive commit message"
git push origin main

# 3. Trigger Jenkins build
# Option A: Wait up to 5 minutes for auto-trigger
# Option B: Click "Build Now" in Jenkins UI

# 4. Monitor build progress
open http://localhost:8080/job/Mediway-Local-CI-CD/lastBuild/console

# 5. Once deployed, test application
open http://localhost
curl http://localhost:8081/api/doctors
```

### Evening Shutdown (Optional)

```bash
# Stop containers to free resources
docker-compose down

# Stop Jenkins (optional - it can run 24/7)
brew services stop jenkins-lts

# Quit Docker Desktop (frees ~4GB RAM)
# Do this via Docker Desktop menu → Quit
```

---

## 📈 Part 12: Performance Optimization

### Speed Up Builds

#### 1. Use Docker Layer Caching

Already configured in Dockerfile - no action needed.

#### 2. Skip Unchanged Stages

Jenkins automatically skips if no changes detected.

#### 3. Increase Jenkins Memory

Edit Jenkins configuration:
```bash
# Edit launch daemon
nano ~/Library/LaunchAgents/homebrew.mxcl.jenkins-lts.plist

# Add to <dict> section:
<key>EnvironmentVariables</key>
<dict>
  <key>JENKINS_OPTS</key>
  <string>-Xmx2048m</string>
</dict>

# Restart Jenkins
brew services restart jenkins-lts
```

### Reduce Docker Image Sizes

Already optimized in your Dockerfiles with multi-stage builds.

---

## 🎓 Part 13: Demo Preparation

### For VIVA/Presentation

#### 1. Quick Demo Script

```bash
# Clean start
docker-compose down -v
brew services restart jenkins-lts

# Wait 30 seconds for Jenkins startup
sleep 30

# Trigger build
# (Click "Build Now" in Jenkins UI)

# Show build progress
open http://localhost:8080/job/Mediway-Local-CI-CD/lastBuild/console

# Once complete, show application
open http://localhost

# Show test reports
open http://localhost:8080/job/Mediway-Local-CI-CD/lastBuild/testReport/
```

#### 2. Explain Each Stage (with timing)

```
┌────────────────────────────────────┬─────────┐
│ Stage                              │ Time    │
├────────────────────────────────────┼─────────┤
│ 1. Checkout from Git               │ 5 sec   │
│ 2. Build Backend (Maven compile)   │ 1 min   │
│ 3. Run TestNG Tests (88 tests)     │ 15 sec  │
│ 4. Package JAR                     │ 30 sec  │
│ 5. Build Docker Images             │ 2 min   │
│ 6. Push to DockerHub               │ 1 min   │
│ 7. Deploy Locally                  │ 30 sec  │
│ 8. Health Check                    │ 45 sec  │
├────────────────────────────────────┼─────────┤
│ Total                              │ ~6 min  │
└────────────────────────────────────┴─────────┘
```

#### 3. Show Key Artifacts

- ✅ Jenkinsfile.local (pipeline configuration)
- ✅ TestNG report (88/88 tests passing)
- ✅ Docker images (backend, frontend)
- ✅ Running application (http://localhost)
- ✅ Jenkins dashboard (build history)

---

## 📚 Part 14: Key Concepts for VIVA

### Q1: Why Jenkins locally instead of GitHub Actions?

**Answer**:
- Jenkins gives full control over build environment
- Can run completely offline
- More suitable for enterprise environments
- Better plugin ecosystem
- Industry-standard CI/CD tool

### Q2: How is local different from AWS deployment?

**Answer**:

| Aspect | AWS Deployment | Local Deployment |
|--------|----------------|------------------|
| Server | EC2 (13.62.209.133) | localhost |
| Access | SSH to remote | Direct file system |
| Cost | $10-20/month | Free |
| Speed | Network latency | Instant |
| Scalability | High | Limited to Mac |

### Q3: What happens if build fails?

**Answer**:
1. Pipeline stops at failed stage
2. Previous deployment remains running
3. Jenkins shows red X for failed stage
4. Email notification sent (if configured)
5. Can view console logs for debugging
6. No bad code reaches "production" (localhost)

### Q4: How do you roll back a deployment?

**Answer**:
```bash
# Option 1: Rebuild previous successful build
# In Jenkins: Click on build #N → Rebuild

# Option 2: Use previous Docker images
docker-compose down
export BUILD_NUMBER=<previous_build_number>
docker-compose up -d

# Option 3: Git revert and rebuild
git revert HEAD
git push origin main
# Jenkins auto-builds
```

### Q5: What's the purpose of each stage?

**Answer**:
- **Stage 1 (Checkout)**: Get latest code from GitHub
- **Stage 2 (Build)**: Compile Java code to .class files
- **Stage 3 (Test)**: Run 88 automated tests to verify quality
- **Stage 4 (Package)**: Create executable JAR file
- **Stage 5 (Docker Build)**: Package app into containers
- **Stage 6 (Push)**: Upload to DockerHub for sharing
- **Stage 7 (Deploy)**: Start containers locally
- **Stage 8 (Health)**: Verify deployment works

---

## 🎯 Part 15: Success Criteria

Your local CI/CD is working correctly when:

✅ Jenkins accessible at http://localhost:8080  
✅ Pipeline runs without errors (green checkmarks)  
✅ All 88 tests pass  
✅ Docker images built successfully  
✅ Images pushed to DockerHub  
✅ Application running at http://localhost  
✅ Backend API responds at http://localhost:8081/api/doctors  
✅ Health checks pass (200 status codes)  
✅ Test reports viewable in Jenkins  
✅ Build completes in ~6 minutes  

---

## 🚀 Quick Start Commands

```bash
# One-time setup
brew install jenkins-lts
brew services start jenkins-lts
# Complete Jenkins setup wizard at http://localhost:8080

# Daily usage
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way

# Make changes, test locally
cd backend && mvn test && cd ..

# Commit and push
git add . && git commit -m "Your message" && git push origin main

# Build in Jenkins (or wait 5 min for auto-trigger)
# http://localhost:8080/job/Mediway-Local-CI-CD/ → Build Now

# Access deployed app
open http://localhost
```

---

## 📞 Support Commands

```bash
# Check Jenkins status
brew services list | grep jenkins

# View Jenkins logs
tail -f ~/.jenkins/logs/jenkins.log

# Restart everything
docker-compose down
brew services restart jenkins-lts
docker-compose up -d

# Clean slate (nuclear option)
docker-compose down -v
docker system prune -a
rm -rf ~/.jenkins/workspace/Mediway-Local-CI-CD
# Then rebuild in Jenkins
```

---

## 🎉 Congratulations!

You now have a fully functional local CI/CD pipeline that:
- ✅ Runs on your Mac without AWS
- ✅ Automatically builds on code changes
- ✅ Runs all tests before deployment
- ✅ Builds and pushes Docker images
- ✅ Deploys to localhost
- ✅ Verifies deployment health
- ✅ Provides detailed test reports

**Total Setup Time**: ~30 minutes  
**Build Time**: ~6 minutes  
**Cost**: $0 (completely free!)  

---

**Created**: March 8, 2026  
**Project**: Medi.Way Healthcare Management System  
**Author**: CI/CD Implementation Team  
**Version**: 1.0.0
