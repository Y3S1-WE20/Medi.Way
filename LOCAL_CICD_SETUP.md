# 🚀 Local CI/CD Setup Guide - Quick Start

## 📋 Overview

Complete CI/CD pipeline running locally on your Mac after AWS account suspension. Includes Jenkins, Docker, TestNG testing, and full deployment automation.

**Status**: ✅ Ready to Install  
**Setup Time**: 30 minutes  
**Build Time**: ~6 minutes per build

---

## ⚡ Quick Start (3 Steps)

### Step 1: Install Jenkins (10 minutes)

```bash
# Install Jenkins
brew install jenkins-lts

# Start Jenkins
brew services start jenkins-lts

# Get admin password
cat ~/.jenkins/secrets/initialAdminPassword

# Open Jenkins
open http://localhost:8080
```

Enter the admin password and install suggested plugins.

### Step 2: Configure Jenkins (15 minutes)

1. **Install Additional Plugins**:
   - Go to: Manage Jenkins → Plugins → Available Plugins
   - Install:
     - ✅ Docker Pipeline
     - ✅ TestNG Results Plugin
     - ✅ HTML Publisher
     - ✅ GitHub Branch Source Plugin

2. **Configure Tools**:
   - Go to: Manage Jenkins → Tools
   - **Maven**:
     - Name: `Maven-3.9`
     - Install automatically: Yes
     - Version: 3.9.x
   - **JDK**:
     - Name: `JDK-17`
     - Install automatically: Yes
     - Version: jdk-17

3. **Add DockerHub Credentials**:
   - Go to: Manage Jenkins → Credentials → System → Global credentials
   - Add Credentials:
     - Kind: Username with password
     - Username: `shiranthads`
     - Password: [your DockerHub password]
     - ID: `dockerhub-credentials`
     - Description: DockerHub credentials for Mediway

### Step 3: Create Pipeline Job (5 minutes)

1. **Create New Pipeline**:
   - New Item → Name: `Mediway-Local-CI-CD` → Pipeline → OK

2. **Configure Build Triggers**:
   - ✅ Poll SCM
   - Schedule: `H/5 * * * *` (check GitHub every 5 minutes)

3. **Configure Pipeline**:
   - Definition: **Pipeline script from SCM**
   - SCM: **Git**
   - Repository URL: `/Users/shiranthadissanayake/Documents/GitHub/Medi.Way`
   - Branch: `*/main` (or your branch name)
   - Script Path: `Jenkinsfile.local`

4. **Save and Build**:
   - Click **Save**
   - Click **Build Now**

---

## 🎯 What You Get

### Pipeline Stages (8 Stages)

1. **📥 Checkout** - Fetches code from Git
2. **🔨 Build** - Compiles backend with Maven
3. **🧪 Test** - Runs 88 TestNG tests
4. **📦 Package** - Creates JAR file
5. **🐳 Docker Build** - Builds Docker images
6. **⬆️ Docker Push** - Pushes to DockerHub
7. **🚀 Deploy** - Deploys with docker-compose
8. **❤️ Health Check** - Verifies deployment

### Test Coverage

- **68 Service Tests** - Business logic testing with Mockito
- **15 Model Tests** - Entity validation
- **5 Integration Tests** - End-to-end scenarios
- **Total: 88 tests**, 100% pass rate

### Deployed Application

After successful build, access:
- **Frontend**: http://localhost
- **Backend API**: http://localhost:8081
- **API Docs**: http://localhost:8081/api/doctors
- **MySQL**: localhost:3306

---

## 🛠️ Management Script

Use the provided script for easy management:

```bash
# Make executable (one-time)
chmod +x local-cicd.sh

# Start all services
./local-cicd.sh start

# Check status
./local-cicd.sh status

# View logs
./local-cicd.sh logs

# Run tests locally
./local-cicd.sh test

# Stop services
./local-cicd.sh stop

# Clean everything
./local-cicd.sh clean

# Show help
./local-cicd.sh help
```

---

## 📊 Expected Build Output

### Successful Build Example

```
Stage View:
✅ Checkout        (10s)
✅ Build           (1m 30s)
✅ Test            (2m 15s)  - 88 tests passed
✅ Package         (45s)
✅ Docker Build    (1m 20s)
✅ Docker Push     (40s)
✅ Deploy          (30s)
✅ Health Check    (20s)

Total Duration: ~6 minutes
```

### Test Results

```
Tests run: 88
Failures: 0
Errors: 0
Skipped: 0
Success rate: 100%
```

### Docker Images Created

```
shiranthads/mediway-backend:[BUILD_NUMBER]-[GIT_COMMIT]
shiranthads/mediway-frontend:[BUILD_NUMBER]-[GIT_COMMIT]
```

---

## 🔍 Verification Steps

### After First Build

1. **Check Jenkins Build Console**:
   ```
   http://localhost:8080/job/Mediway-Local-CI-CD/
   ```
   Should see green checkmark ✅

2. **Check TestNG Report**:
   - Jenkins → Mediway-Local-CI-CD → Latest Build → TestNG Results
   - Should show 88 passed tests

3. **Check Deployed Application**:
   ```bash
   # Frontend
   curl http://localhost
   
   # Backend health
   curl http://localhost:8081/actuator/health
   
   # API endpoint
   curl http://localhost:8081/api/doctors
   ```

4. **Check Running Containers**:
   ```bash
   docker ps
   ```
   Should show 3 containers:
   - `mediway-mysql`
   - `mediway-backend`
   - `mediway-frontend`

---

## 🚨 Troubleshooting

### Jenkins Won't Start

```bash
# Check if port 8080 is in use
lsof -i :8080

# Restart Jenkins
brew services restart jenkins-lts

# Check logs
tail -f ~/.jenkins/logs/jenkins.log
```

### Docker Issues

```bash
# Start Docker Desktop
open -a Docker

# Check Docker status
docker info

# Restart Docker from menu bar
```

### Build Failures

```bash
# Check if tools are configured
# Jenkins → Manage Jenkins → Tools
# Verify: Maven-3.9 and JDK-17

# Clean workspace
rm -rf ~/.jenkins/workspace/Mediway-Local-CI-CD

# Rebuild
# Jenkins → Build Now
```

### Port Already in Use

```bash
# Check what's using port 80
sudo lsof -i :80

# Stop existing containers
docker-compose down

# Start fresh
./local-cicd.sh start
```

### Tests Failing

```bash
# Run tests manually
cd backend
mvn clean test

# Check test output
cat target/surefire-reports/*.txt

# View detailed report
open test-output/index.html
```

---

## 📁 File Structure

```
Medi.Way/
├── Jenkinsfile.local                    ← Pipeline configuration
├── docker-compose.yml                   ← Container orchestration
├── local-cicd.sh                        ← Management script
├── LOCAL_CICD_SETUP.md                  ← This file
├── docs/
│   ├── LOCAL_JENKINS_CICD_GUIDE.md     ← Detailed guide (15 parts)
│   └── TESTNG_TESTING_EXPLAINED.md     ← Testing documentation
├── backend/
│   ├── pom.xml                         ← Maven configuration
│   ├── testng.xml                      ← TestNG suite
│   └── src/
│       └── test/java/backend/          ← 88 tests
└── frontend/
    └── src/                            ← React app
```

---

## 🎓 VIVA Preparation

### Key Points to Demonstrate

1. **Local CI/CD Setup**:
   - "Due to AWS account suspension, migrated entire CI/CD to local Mac"
   - "Full Jenkins pipeline running on localhost"

2. **Pipeline Stages**:
   - Show 8-stage pipeline in Jenkins UI
   - Explain each stage's purpose

3. **TestNG Testing**:
   - Open TestNG report showing 88 passed tests
   - Explain test categories: Service (68), Model (15), Integration (5)

4. **Docker Deployment**:
   - Run `docker ps` to show 3 containers
   - Access application at http://localhost

5. **Automation**:
   - Show GitHub polling (checks every 5 minutes)
   - Demonstrate automatic build trigger

### Common VIVA Questions

**Q: Why local instead of cloud?**  
A: AWS account was suspended. Local setup provides zero cost, full control, and faster builds without network latency.

**Q: How does polling work?**  
A: Jenkins polls GitHub every 5 minutes (`H/5 * * * *`) and triggers build if changes detected.

**Q: What happens in Test stage?**  
A: Runs 88 TestNG tests using Maven. Tests include service layer (Mockito mocking), model validation, and integration scenarios.

**Q: How is deployment verified?**  
A: Health Check stage performs HTTP requests to frontend (localhost:80) and backend API (localhost:8081/api/doctors) to confirm services are responding.

**Q: What if build fails?**  
A: Check Jenkins console output for errors. Common issues: Maven dependencies, Docker not running, port conflicts. All detailed in troubleshooting guide.

---

## 📝 Daily Workflow

### Making Changes

```bash
# 1. Make code changes
vim backend/src/main/java/backend/controller/DoctorController.java

# 2. Test locally (optional)
./local-cicd.sh test

# 3. Commit and push
git add .
git commit -m "Updated doctor controller"
git push origin main

# 4. Jenkins automatically detects changes (within 5 minutes)
#    and triggers build pipeline

# 5. Monitor build
open http://localhost:8080/job/Mediway-Local-CI-CD/

# 6. Verify deployment
curl http://localhost:8081/api/doctors
```

### Manual Build

```bash
# Trigger build immediately (don't wait for polling)
./local-cicd.sh build

# Or via Jenkins UI
open http://localhost:8080/job/Mediway-Local-CI-CD/
# Click "Build Now"
```

---

## 🔧 Advanced Configuration

### Change Build Frequency

Edit poll schedule in Jenkins job configuration:

- `H/5 * * * *` - Every 5 minutes (current)
- `H/15 * * * *` - Every 15 minutes
- `H * * * *` - Every hour
- `H H * * *` - Once per day

### Skip Tests (Emergency Deploy)

```bash
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way
mvn clean package -DskipTests
docker-compose up -d --build
```

### View Build History

```bash
# All builds
open http://localhost:8080/job/Mediway-Local-CI-CD/

# Specific build console output
open http://localhost:8080/job/Mediway-Local-CI-CD/[BUILD_NUMBER]/console

# Test trends
open http://localhost:8080/job/Mediway-Local-CI-CD/testng
```

---

## 📚 Additional Resources

- **Detailed Guide**: [docs/LOCAL_JENKINS_CICD_GUIDE.md](docs/LOCAL_JENKINS_CICD_GUIDE.md) (15 parts, 6,158 lines)
- **Testing Guide**: [docs/TESTNG_TESTING_EXPLAINED.md](docs/TESTNG_TESTING_EXPLAINED.md) (550 lines)
- **Jenkinsfile**: [Jenkinsfile.local](Jenkinsfile.local) (387 lines)
- **Docker Compose**: [docker-compose.yml](docker-compose.yml)

---

## ✅ Success Checklist

Before marking setup as complete, verify:

- [ ] Jenkins installed and accessible at http://localhost:8080
- [ ] Docker Desktop running
- [ ] Maven-3.9 and JDK-17 configured in Jenkins
- [ ] DockerHub credentials added
- [ ] Pipeline job "Mediway-Local-CI-CD" created
- [ ] First build completed successfully (all stages green)
- [ ] All 88 tests passing
- [ ] Frontend accessible at http://localhost
- [ ] Backend API responding at http://localhost:8081
- [ ] Docker containers running (`docker ps` shows 3 containers)
- [ ] TestNG report available in Jenkins

---

## 🎉 You're Ready!

Your local CI/CD pipeline is now fully operational. Every code change pushed to GitHub will automatically:

1. ✅ Trigger a build within 5 minutes
2. ✅ Run 88 comprehensive tests
3. ✅ Build Docker images
4. ✅ Push to DockerHub
5. ✅ Deploy to localhost
6. ✅ Verify with health checks

**Total automation time**: ~6 minutes from push to deployment

---

## 📞 Support

If you encounter issues:

1. Check [Troubleshooting](#🚨-troubleshooting) section above
2. Review [docs/LOCAL_JENKINS_CICD_GUIDE.md](docs/LOCAL_JENKINS_CICD_GUIDE.md) for detailed explanations
3. Check Jenkins console output for specific error messages
4. Run `./local-cicd.sh status` to verify all services

---

**Last Updated**: December 2024  
**Version**: 1.0  
**Status**: Production Ready ✅
