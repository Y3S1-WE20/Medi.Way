# Complete CI/CD Pipeline Implementation Guide
## Medi.Way Healthcare Management System

---

## 📑 Table of Contents

1. [Overview](#overview)
2. [Prerequisites](#prerequisites)
3. [Phase 1: AWS EC2 Setup](#phase-1-aws-ec2-setup)
4. [Phase 2: Jenkins Installation](#phase-2-jenkins-installation)
5. [Phase 3: Jenkins Configuration](#phase-3-jenkins-configuration)
6. [Phase 4: DockerHub Setup](#phase-4-dockerhub-setup)
7. [Phase 5: GitHub Integration](#phase-5-github-integration)
8. [Phase 6: Pipeline Execution](#phase-6-pipeline-execution)
9. [Phase 7: Deployment Server Setup](#phase-7-deployment-server-setup)
10. [Verification & Testing](#verification--testing)
11. [Troubleshooting](#troubleshooting)

---

## 🎯 Overview

This guide will help you implement a complete CI/CD pipeline for the Medi.Way application using:

- **Jenkins** for CI/CD orchestration
- **TestNG** for automated testing
- **Docker** for containerization
- **DockerHub** for image registry
- **AWS EC2** for hosting
- **GitHub Webhooks** for instant build triggers

### Pipeline Flow

```
Code Push → GitHub Webhook → Jenkins → Build → TestNG Tests → Docker Build → 
DockerHub Push → EC2 Deployment → Health Checks → Success! 🎉
```

### Time Estimate

- **First-time setup:** 2-3 hours
- **Subsequent pipeline runs:** 5-10 minutes
- **Build trigger latency:** <10 seconds with webhooks

---

## 🔧 Prerequisites

### Required Accounts

- [ ] AWS Account (free tier eligible)
- [ ] GitHub Account
- [ ] DockerHub Account (free)

### Local Machine Requirements

- [ ] macOS/Linux/Windows with terminal access
- [ ] SSH client installed
- [ ] Web browser
- [ ] Text editor (VS Code recommended)

---

## 🚀 Phase 1: AWS EC2 Setup

### Step 1.1: Launch Jenkins Server Instance

#### 1.1.1: Sign in to AWS Console

1. Go to [https://aws.amazon.com/console/](https://aws.amazon.com/console/)
2. Sign in with your AWS account
3. Select your preferred region (e.g., `us-east-1`, `us-west-2`)

#### 1.1.2: Launch EC2 Instance

1. Navigate to **EC2 Dashboard**
   - Search "EC2" in the top search bar
   - Click **EC2** under Services

2. Click **Launch Instance** button

3. **Configure Instance:**

   **Name and tags:**
   ```
   Name: jenkins-server
   ```

   **Application and OS Images (Amazon Machine Image):**
   ```
   AMI: Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
   Architecture: 64-bit (x86)
   ```

   **Instance type:**
   ```
   Instance type: t2.medium
   (2 vCPUs, 4 GB RAM - Required for Jenkins)
   
   ⚠️ WARNING: t2.micro is too small for Jenkins!
   ```

   **Key pair (login):**
   ```
   - Click "Create new key pair"
   - Key pair name: jenkins-key
   - Key pair type: RSA
   - Private key file format: .pem
   - Click "Create key pair"
   - Save the .pem file to a secure location (you'll need this!)
   ```

   **Network settings:**
   ```
   - Click "Edit" next to Network settings
   - Auto-assign public IP: Enable
   
   - Security group name: jenkins-sg
   - Description: Security group for Jenkins server
   
   - Inbound Security Group Rules:
     1. SSH
        - Type: SSH
        - Port: 22
        - Source: My IP (or 0.0.0.0/0 for anywhere)
     
     2. Jenkins Web UI
        - Type: Custom TCP
        - Port: 8080
        - Source: 0.0.0.0/0 (anywhere)
     
     3. HTTP (for application testing)
        - Type: HTTP
        - Port: 80
        - Source: 0.0.0.0/0
     
     4. Custom (for backend API)
        - Type: Custom TCP
        - Port: 8081
        - Source: 0.0.0.0/0
   ```

   **Configure storage:**
   ```
   - Size: 30 GiB (increase from default 8 GiB)
   - Volume type: gp3
   - Delete on termination: Yes (checked)
   ```

4. **Review and Launch**
   - Review all settings
   - Click **Launch instance**
   - Wait 1-2 minutes for instance to start

5. **Note Instance Details**
   ```
   Instance ID: i-xxxxxxxxxxxx
   Public IPv4 address: x.x.x.x (SAVE THIS!)
   Public IPv4 DNS: ec2-x-x-x-x.compute-1.amazonaws.com
   ```

#### 1.1.3: Configure SSH Key Permissions

```bash
# Navigate to where you saved the .pem file
cd ~/Downloads  # or your download location

# Set correct permissions (required for SSH)
chmod 400 jenkins-key.pem

# Move to a permanent location
mkdir -p ~/.ssh/aws-keys
mv jenkins-key.pem ~/.ssh/aws-keys/
```

#### 1.1.4: Connect to EC2 Instance

```bash
# Replace with your actual public IP address
ssh -i ~/.ssh/aws-keys/jenkins-key.pem ubuntu@YOUR_EC2_PUBLIC_IP

# Example:
# ssh -i ~/.ssh/aws-keys/jenkins-key.pem ubuntu@54.123.45.67
```

**Expected Output:**
```
Welcome to Ubuntu 22.04.3 LTS (GNU/Linux 6.2.0-1009-aws x86_64)
...
ubuntu@ip-172-31-xx-xx:~$
```

---

### Step 1.2: Install Required Software

#### 1.2.1: Update System

```bash
# Update package lists
sudo apt update

# Upgrade installed packages
sudo apt upgrade -y

# This may take 5-10 minutes
```

#### 1.2.2: Install Java 17

```bash
# Install OpenJDK 17
sudo apt install -y openjdk-17-jdk


# Verify installation
java -version
```

**Expected Output:**
```
openjdk version "17.0.x" 2023-xx-xx
OpenJDK Runtime Environment (build 17.0.x+x-Ubuntu-xxubuntu1)
OpenJDK 64-Bit Server VM (build 17.0.x+x-Ubuntu-xxubuntu1, mixed mode)
```

#### 1.2.3: Install Jenkins

```bash
# Add Jenkins repository key
curl -fsSL https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key | sudo tee \
  /usr/share/keyrings/jenkins-keyring.asc > /dev/null

# Add Jenkins repository
echo deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] \
  https://pkg.jenkins.io/debian-stable binary/ | sudo tee \
  /etc/apt/sources.list.d/jenkins.list > /dev/null

# Update package list
sudo apt update

# Install Jenkins
sudo apt install -y jenkins

# Start Jenkins service
sudo systemctl start jenkins

# Enable Jenkins to start on boot
sudo systemctl enable jenkins

# Check Jenkins status
sudo systemctl status jenkins
```

**Expected Output:**
```
● jenkins.service - Jenkins Continuous Integration Server
     Loaded: loaded (/lib/systemd/system/jenkins.service; enabled; vendor preset: enabled)
     Active: active (running) since Thu 2026-03-06 10:00:00 UTC; 10s ago
```

Press `q` to exit status view.

#### 1.2.4: Install Docker

```bash
# Install Docker
sudo apt install -y docker.io

# Start Docker service
sudo systemctl start docker
sudo systemctl enable docker

# Add ubuntu user to docker group (run Docker without sudo)
sudo usermod -aG docker ubuntu

# Add jenkins user to docker group (Jenkins can use Docker)
sudo usermod -aG docker jenkins

# Restart Jenkins to apply group changes
sudo systemctl restart jenkins

# Verify Docker installation
sudo docker --version
```

**Expected Output:**
```
Docker version 24.0.x, build xxxxxxx
```

#### 1.2.5: Install Docker Compose

```bash
# Install Docker Compose
sudo apt install -y docker-compose

# Verify installation
docker-compose --version
```

**Expected Output:**
```
docker-compose version 2.x.x, build xxxxxxx
```

#### 1.2.6: Install Maven

```bash
# Install Maven
sudo apt install -y maven

# Verify installation
mvn --version
```

**Expected Output:**
```
Apache Maven 3.8.x (xxxxxxx)
Maven home: /usr/share/maven
Java version: 17.0.x, vendor: Private Build
```

#### 1.2.7: Get Jenkins Initial Password

```bash
# Get the initial admin password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

**Copy the output** (you'll need this in the next phase)

Example output:
```
7a6c2005477f4cfcae1999d94a127d16
```

---

## 🔐 Phase 2: Jenkins Configuration

### Step 2.1: Access Jenkins Web UI

#### 2.1.1: Open Jenkins in Browser

```
http://YOUR_EC2_PUBLIC_IP:8080

Example: http://54.123.45.67:8080
```

#### 2.1.2: Unlock Jenkins

1. You should see "Unlock Jenkins" page
2. Paste the initial admin password (from Step 1.2.7)
3. Click **Continue**

### Step 2.2: Install Plugins

#### 2.2.1: Install Suggested Plugins

1. Select **Install suggested plugins**
2. Wait 5-10 minutes for plugins to install
3. You'll see a progress bar with plugin names

#### 2.2.2: Create First Admin User

Fill in the form:
```
Username: admin
Password: [Choose a strong password]
Confirm password: [Same password]
Full name: Admin User
E-mail address: your-email@example.com
```

Click **Save and Continue**

#### 2.2.3: Instance Configuration

```
Jenkins URL: http://YOUR_EC2_PUBLIC_IP:8080/
```

Click **Save and Finish** → **Start using Jenkins**

### Step 2.3: Install Additional Required Plugins

#### 2.3.1: Navigate to Plugin Manager

1. Click **Manage Jenkins** (left sidebar)
2. Click **Plugins**
3. Click **Available plugins** tab

#### 2.3.2: Install Required Plugins

Search and select the following plugins (use the search box):

- [x] **Docker Pipeline** - Build and push Docker images
- [x] **TestNG Results Plugin** - Display TestNG test results
- [x] **HTML Publisher Plugin** - Publish HTML reports
- [x] **SSH Agent Plugin** - SSH deployment support
- [x] **GitHub Plugin** - GitHub integration
- [x] **Pipeline** - Pipeline support (should already be installed)

**To install:**
1. Check the boxes next to each plugin
2. Click **Install** button at the top
3. Check "Restart Jenkins when installation is complete and no jobs are running"
4. Wait for restart (2-3 minutes)

#### 2.3.3: Log Back In

```
URL: http://YOUR_EC2_PUBLIC_IP:8080/
Username: admin
Password: [Your password from Step 2.2.2]
```

### Step 2.4: Configure Jenkins Tools

#### 2.4.1: Configure JDK

1. **Manage Jenkins** → **Tools**
2. Scroll to **JDK installations**
3. Click **Add JDK**
4. Configure:
   ```
   Name: JDK-17
   [ ] Install automatically (uncheck this)
   JAVA_HOME: /usr/lib/jvm/java-17-openjdk-amd64
   ```
5. **Don't save yet** - continue to Maven

#### 2.4.2: Configure Maven

1. Scroll to **Maven installations**
2. Click **Add Maven**
3. Configure:
   ```
   Name: Maven-3.9
   [x] Install automatically (check this)
   Version: [Select latest 3.9.x from dropdown]
   ```

#### 2.4.3: Save Configuration

1. Scroll to bottom
2. Click **Save**

---

## 🐳 Phase 3: DockerHub Setup

### Step 3.1: Create DockerHub Account

If you don't have a DockerHub account:

1. Go to [https://hub.docker.com/signup](https://hub.docker.com/signup)
2. Fill in:
   ```
   Docker ID: shiranthads (your username)
   Email: your-email@example.com
   Password: [Choose strong password]
   ```
3. Verify your email
4. Sign in to DockerHub

### Step 3.2: Create Access Token (Recommended)

1. Sign in to DockerHub
2. Click your username (top right) → **Account Settings**
3. Click **Security** tab
4. Click **New Access Token**
5. Configure:
   ```
   Access Token Description: Jenkins CI/CD
   Access permissions: Read, Write, Delete
   ```
6. Click **Generate**
7. **Copy the token** (you won't see it again!)
   ```
   
   ```

### Step 3.3: Add DockerHub Credentials to Jenkins

#### 3.3.1: Navigate to Credentials

1. **Manage Jenkins** → **Credentials**
2. Click **(global)** domain
3. Click **Add Credentials** (left sidebar)

#### 3.3.2: Configure Credentials

```
Kind: Username with password
Scope: Global
Username: shiranthads (your DockerHub username)
Password: [Your DockerHub password or access token]
ID: dockerhub-credentials
Description: DockerHub Login for CI/CD
```

Click **Create**

---

## 🔗 Phase 4: GitHub Integration

### Step 4.1: Push Code to GitHub

If you haven't already pushed your code:

```bash
# On your local machine (in the project directory)
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way

# Initialize git (if not already done)
git init

# Add all files
git add .

# Commit
git commit -m "Initial commit with CI/CD pipeline"

# Add remote (replace with your GitHub repo URL)
git remote add origin https://github.com/YOUR-USERNAME/Medi.Way.git

# Push to GitHub
git push -u origin main
```

### Step 4.2: Configure GitHub Webhook

**Set up automatic build triggers using GitHub webhooks for instant builds on code push.**

#### 4.2.1: Add GitHub Credentials to Jenkins (if private repo)

1. **Manage Jenkins** → **Credentials** → **(global)** → **Add Credentials**
2. Configure:
   ```
   Kind: Username with password
   Username: [Your GitHub username]
   Password: [Your GitHub Personal Access Token]
   ID: github-credentials
   Description: GitHub Access
   ```

**To create GitHub PAT:**
- GitHub → Settings → Developer settings → Personal access tokens → Tokens (classic) → Generate new token
- Token name: `Jenkins CI/CD`
- Expiration: `90 days` (or custom)
- Select scopes: 
  - `repo` (all) - Full control of private repositories
  - `admin:repo_hook` - For managing webhooks
- Click **Generate token**
- **Copy the token immediately** (you won't see it again!)

#### 4.2.2: Set Up GitHub Webhook

1. **Go to your GitHub repository:**
   ```
   https://github.com/YOUR-USERNAME/Medi.Way
   ```

2. **Navigate to Settings:**
   - Click **Settings** tab (top right of repo)
   - Click **Webhooks** (left sidebar)
   - Click **Add webhook** button

3. **Configure Webhook:**
   ```
   Payload URL: http://YOUR_EC2_PUBLIC_IP:8080/github-webhook/
   (Example: http://13.62.209.133:8080/github-webhook/)
   
   Content type: application/json
   
   Secret: [Leave empty or add a secret token for security]
   
   SSL verification: [Leave as default]
   
   Which events would you like to trigger this webhook?
   • Just the push event (selected by default)
   
   Active: ✓ (checked)
   ```

4. **Click "Add webhook"**

5. **Verify Webhook:**
   - After adding, GitHub will immediately test the webhook
   - You should see a green checkmark ✓ next to your webhook
   - If you see a red X, check:
     - Jenkins URL is accessible from internet
     - Port 8080 is open in EC2 security group
     - URL ends with `/github-webhook/`

**⚠️ Important Security Note:**
If your EC2 instance has a dynamic IP, consider:
- Using an Elastic IP (AWS) for a permanent address
- Using a domain name with DNS
- For testing, dynamic IP works but may change if instance restarts

---

## 🏗️ Phase 5: Create Jenkins Pipeline

### Step 5.1: Create New Pipeline Job

1. **Jenkins Dashboard** → Click **New Item**
2. Configure:
   ```
   Enter an item name: Mediway-CI-CD-Pipeline
   Type: [Select] Pipeline
   ```
3. Click **OK**

### Step 5.2: Configure Pipeline

#### 5.2.1: General Section

```
[x] GitHub project
Project url: https://github.com/YOUR-USERNAME/Medi.Way/
```

#### 5.2.2: Build Triggers

**Recommended:** Use GitHub webhook for instant builds:
```
[x] GitHub hook trigger for GITScm polling
(Builds automatically when you push to GitHub - instant!)
```

**Alternative:** If webhook is not working, use Poll SCM:
```
[x] Poll SCM
Schedule: H/5 * * * *
(This checks GitHub every 5 minutes for changes - slower but reliable)
```

**✅ Use webhook** if you completed Step 4.2.2 (GitHub Webhook setup)  
**📊 Use Poll SCM** as backup or if webhook can't be configured

#### 5.2.3: Pipeline Section

```
Definition: Pipeline script from SCM

SCM: Git

Repositories:
  Repository URL: https://github.com/YOUR-USERNAME/Medi.Way.git
  Credentials: [Select github-credentials if private repo]
  
Branches to build:
  Branch Specifier: */main (or */master if that's your branch)

Script Path: Jenkinsfile
```

#### 5.2.4: Save Configuration

Click **Save** at the bottom

---

## 🎬 Phase 6: Run Your First Pipeline

### Step 6.1: Manual Build

1. Go to pipeline: **Mediway-CI-CD-Pipeline**
2. Click **Build Now** (left sidebar)
3. Watch the build progress

### Step 6.2: Monitor Pipeline Execution

#### 6.2.1: View Stage View

You'll see stages executing:

```
Stage 1: Checkout          ✓ (10s)
Stage 2: Build Backend     ✓ (2m 30s)
Stage 3: Run TestNG Tests  ✓ (1m 45s)
Stage 4: Package           ✓ (45s)
Stage 5: Build Docker      ⏳ (running...)
```

#### 6.2.2: View Console Output

1. Click the build number (e.g., **#1**)
2. Click **Console Output**
3. Watch real-time logs

### Step 6.3: Expected Pipeline Behavior

#### If Tests Pass ✅

```
Stage 1: Checkout          → ✅ Success
Stage 2: Build Backend     → ✅ Success
Stage 3: Run TestNG Tests  → ✅ All tests passed!
Stage 4: Package           → ✅ JAR created
Stage 5: Build Docker      → ✅ Images built
Stage 6: Push to DockerHub → ✅ Images pushed
Stage 7: Deploy to EC2     → ⚠️ Will fail (we haven't set up deployment server yet)
```

#### If Tests Fail ❌

```
Stage 1: Checkout          → ✅ Success
Stage 2: Build Backend     → ✅ Success
Stage 3: Run TestNG Tests  → ❌ FAILED! 2 tests failed
Pipeline ABORTED (remaining stages skipped)

Result: X Failed build - No deployment!
```

**This is the QUALITY GATE!** Bad code never reaches production.

### Step 6.4: View Test Reports

1. Go to build page (e.g., **Build #1**)
2. Click **TestNG Results** (left sidebar)
3. View detailed test report with pass/fail status

You should see:
```
Total Tests: 88
Passed: 88
Failed: 0
Skipped: 0
Success Rate: 100%
```

### Step 6.5: View HTML Reports

1. On build page, click **TestNG HTML Report**
2. View beautiful formatted report with:
   - Test execution timeline
   - Pass/Fail statistics
   - Individual test details
   - Execution time per test

---

## 🖥️ Phase 7: Deployment Server Setup (Optional)

If you want to deploy to a separate EC2 instance:

### Step 7.1: Launch Deployment Server

Follow same steps as Phase 1, Step 1.1, but with these differences:

```
Name: mediway-app-server
Instance type: t2.small (1 vCPU, 2 GB RAM)

Security Group Rules:
  1. SSH - Port 22
  2. HTTP - Port 80
  3. Backend API - Port 8080
  4. MySQL - Port 3306 (only from Jenkins server IP)

Key pair: Create new "mediway-app-key" or reuse "jenkins-key"
```

### Step 7.2: Install Docker on Deployment Server

```bash
# SSH to deployment server
ssh -i ~/.ssh/aws-keys/mediway-app-key.pem ubuntu@DEPLOYMENT_SERVER_IP

# Install Docker
sudo apt update
sudo apt install -y docker.io docker-compose

# Add ubuntu to docker group
sudo usermod -aG docker ubuntu

# Logout and login again for group changes
exit
ssh -i ~/.ssh/aws-keys/mediway-app-key.pem ubuntu@DEPLOYMENT_SERVER_IP

# Verify Docker
docker --version
```

### Step 7.3: Prepare Deployment Directory

```bash
# Create application directory
mkdir -p ~/mediway
cd ~/mediway

# Copy docker-compose.yml from your project
# (You can use git clone or scp to transfer files)

# Option 1: Using git
git clone https://github.com/YOUR-USERNAME/Medi.Way.git .

# Option 2: Using scp from your local machine
# (Run this from your local machine, not EC2)
# scp -i ~/.ssh/aws-keys/mediway-app-key.pem \
#     docker-compose.yml \
#     ubuntu@DEPLOYMENT_SERVER_IP:~/mediway/
```

### Step 7.4: Add Deployment Server SSH Key to Jenkins

1. **On your local machine**, copy the deployment server SSH key:
   ```bash
   cat ~/.ssh/aws-keys/mediway-app-key.pem
   ```

2. **In Jenkins:**
   - **Manage Jenkins** → **Credentials** → **Add Credentials**
   - Configure:
     ```
     Kind: SSH Username with private key
     ID: ec2-ssh-key
     Description: EC2 Deployment Server SSH Key
     Username: ubuntu
     Private Key: [Select] Enter directly
     Key: [Paste the .pem file content]
     ```
   - Click **Create**

### Step 7.5: Update Jenkinsfile with Deployment Server IP

The Jenkinsfile already has your DockerHub username configured. Now add the deployment server IP:

```bash
# On your local machine
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way

# Edit Jenkinsfile
# Change this line:
# EC2_HOST = 'your-ec2-public-ip'
# To:
# EC2_HOST = 'YOUR_DEPLOYMENT_SERVER_IP'
```

Or the file has already been updated with your DockerHub username. You just need to add the EC2 IP when ready.

### Step 7.6: Test Full Pipeline with Deployment

1. Commit and push Jenkinsfile changes:
   ```bash
   git add Jenkinsfile
   git commit -m "Update deployment server IP"
   git push origin main
   ```

2. Jenkins will automatically trigger a build (if you set up polling)
3. Or manually trigger: **Build Now**

4. All stages should now pass:
   ```
   Stage 1: Checkout          → ✅
   Stage 2: Build Backend     → ✅
   Stage 3: Run TestNG Tests  → ✅
   Stage 4: Package           → ✅
   Stage 5: Build Docker      → ✅
   Stage 6: Push to DockerHub → ✅
   Stage 7: Deploy to EC2     → ✅
   Stage 8: Health Check      → ✅
   
   Pipeline SUCCESS! 🎉
   ```

---

## ✅ Phase 8: Verification & Testing

### Step 8.1: Verify Jenkins Pipeline

```bash
# Check Jenkins is running
curl http://YOUR_JENKINS_SERVER_IP:8080

# Expected: You should get HTML response (Jenkins login page)
```

### Step 8.2: Verify Docker on Jenkins Server

```bash
# SSH to Jenkins server
ssh -i ~/.ssh/aws-keys/jenkins-key.pem ubuntu@YOUR_JENKINS_SERVER_IP

# Check Docker
docker --version
docker ps

# Check Jenkins can use Docker
sudo -u jenkins docker ps
```

Expected: No permission errors

### Step 8.3: Verify DockerHub Push

1. Go to [https://hub.docker.com](https://hub.docker.com)
2. Sign in
3. Click **Repositories**
4. You should see:
   ```
   shiranthads/mediway-backend:latest
   shiranthads/mediway-backend:1-a1b2c3d
   shiranthads/mediway-frontend:latest
   shiranthads/mediway-frontend:1-a1b2c3d
   ```

### Step 8.4: Test Deployed Application (if deployment server configured)

```bash
# Test frontend
curl http://YOUR_DEPLOYMENT_SERVER_IP

# Expected: HTML content from React app

# Test backend API health
curl http://YOUR_DEPLOYMENT_SERVER_IP:8080/api/health

# Expected: {"status":"UP"} or similar
```

### Step 8.5: View Application in Browser

```
Frontend: http://YOUR_DEPLOYMENT_SERVER_IP
Backend API: http://YOUR_DEPLOYMENT_SERVER_IP:8080
```

---

## 🔍 Troubleshooting

### Issue 1: Jenkins Won't Start

**Symptom:**
```bash
sudo systemctl status jenkins
# Status: failed
```

**Solutions:**

```bash
# Check Java is installed
java -version

# Check Jenkins logs
sudo journalctl -u jenkins -n 50

# Restart Jenkins
sudo systemctl restart jenkins

# If still failing, reinstall Jenkins
sudo apt remove --purge jenkins
sudo apt install jenkins
```

### Issue 2: Docker Permission Denied

**Symptom:**
```
ERROR: permission denied while trying to connect to the Docker daemon socket
```

**Solution:**

```bash
# Add jenkins user to docker group
sudo usermod -aG docker jenkins

# Restart Jenkins
sudo systemctl restart jenkins

# Verify
sudo -u jenkins docker ps
```

### Issue 3: TestNG Tests Fail

**Symptom:**
```
Stage 3: Run TestNG Tests → FAILED
```

**Solution:**

```bash
# SSH to Jenkins server
ssh -i ~/.ssh/aws-keys/jenkins-key.pem ubuntu@YOUR_JENKINS_SERVER_IP

# Run tests manually to see detailed errors
cd /var/lib/jenkins/workspace/Mediway-CI-CD-Pipeline/backend
mvn clean test

# Check test reports
ls -la target/test-reports/
```

### Issue 4: Cannot Connect to EC2

**Symptom:**
```bash
ssh -i jenkins-key.pem ubuntu@IP
# Connection timeout or refused
```

**Solutions:**

1. **Check Security Group:**
   - AWS Console → EC2 → Security Groups
   - Ensure Port 22 is open for your IP

2. **Check Key Permissions:**
   ```bash
   chmod 400 jenkins-key.pem
   ```

3. **Check Instance is Running:**
   - AWS Console → EC2 → Instances
   - State should be "Running"

4. **Check Public IP:**
   - IP might change if instance was stopped/started
   - Use Elastic IP for permanent address

### Issue 5: DockerHub Push Fails

**Symptom:**
```
Stage 6: Push to Docker Hub → denied: access forbidden
```

**Solutions:**

1. **Verify Credentials:**
   - Jenkins → Credentials → Edit dockerhub-credentials
   - Username: `shiranthads`
   - Password: Correct password or access token

2. **Test Login Manually:**
   ```bash
   # SSH to Jenkins server
   ssh -i ~/.ssh/aws-keys/jenkins-key.pem ubuntu@YOUR_JENKINS_SERVER_IP
   
   # Try Docker login
   docker login -u shiranthads
   # Enter password when prompted
   
   # If successful, logout
   docker logout
   ```

3. **Check Rate Limits:**
   - Free DockerHub accounts have pull/push limits
   - Wait 6 hours or upgrade account

### Issue 6: Build Takes Too Long

**Symptom:**
```
Build running for 30+ minutes
```

**Solutions:**

1. **Check Instance Resources:**
   ```bash
   # SSH to Jenkins server
   top
   # Check CPU and memory usage
   ```

2. **Upgrade Instance:**
   - t2.micro → t2.medium (required for Jenkins)
   - AWS Console → EC2 → Instance → Actions → Instance Settings → Change Instance Type

3. **Clean Docker:**
   ```bash
   # Remove old images/containers
   docker system prune -a --volumes
   ```

### Issue 7: Maven Build Fails

**Symptom:**
```
Stage 2: Build Backend → FAILED
[ERROR] Failed to execute goal
```

**Solutions:**

1. **Check pom.xml:**
   - Ensure all dependencies are correct
   - No version conflicts

2. **Clear Maven Cache:**
   ```bash
   # SSH to Jenkins server
   sudo rm -rf /var/lib/jenkins/.m2/repository
   # Next build will re-download dependencies
   ```

3. **Check Java Version:**
   ```bash
   java -version  # Must be Java 17
   ```

### Issue 8: GitHub Webhook Not Working

**Symptom:**
```
Pushed to GitHub but Jenkins doesn't build automatically
```

**Solutions:**

1. **Verify Webhook Configuration:**
   ```
   GitHub Repo → Settings → Webhooks
   - Should see green ✓ next to webhook
   - If red X, click to see error details
   ```

2. **Check Jenkins is Accessible:**
   ```bash
   # From your local machine, test if Jenkins is reachable
   curl http://YOUR_EC2_IP:8080/github-webhook/
   
   # Expected: Some response (not connection refused)
   ```

3. **Verify Security Group:**
   - AWS Console → EC2 → Security Groups
   - Ensure Port 8080 is open to `0.0.0.0/0` (or at least GitHub's IP ranges)
   - GitHub webhook needs internet access to your Jenkins

4. **Check Recent Deliveries:**
   - GitHub → Settings → Webhooks → Click your webhook
   - Scroll to "Recent Deliveries"
   - Click on a delivery to see request/response
   - Look for errors in the response

5. **Test Webhook Manually:**
   ```
   GitHub → Webhook → Click "Redeliver"
   Check if Jenkins receives it
   ```

6. **Enable Poll SCM as Backup:**
   - If webhook continues failing, enable Poll SCM in Jenkins
   - Builds will happen but with 5-minute delay

7. **Common Webhook Errors:**
   ```
   "Connection refused" → Jenkins not running or port blocked
   "Timeout" → Security group blocking traffic
   "404 Not Found" → URL incorrect (should be /github-webhook/)
   "500 Internal Error" → Check Jenkins logs
   ```

---

## 📊 Understanding Pipeline Stages

### Stage Flow Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 1: CHECKOUT                                               │
│ ─────────────────────────────────────────────────────────────── │
│ • Git clone from GitHub                                         │
│ • Get commit hash                                               │
│ • Set build tag                                                 │
│ Duration: ~10 seconds                                           │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 2: BUILD BACKEND                                          │
│ ─────────────────────────────────────────────────────────────── │
│ • mvn clean compile                                             │
│ • Compile Java source code                                      │
│ • Download dependencies                                         │
│ Duration: ~2 minutes (first build), ~30s (subsequent)           │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 3: RUN TESTNG TESTS ⭐ QUALITY GATE                       │
│ ─────────────────────────────────────────────────────────────── │
│ • mvn test                                                      │
│ • Run 88 TestNG tests                                           │
│ • Generate HTML reports                                         │
│ • If ANY test fails → ABORT pipeline                           │
│ Duration: ~1-2 minutes                                          │
└─────────────────────────────────────────────────────────────────┘
                            ↓
              ┌─────────────┴─────────────┐
              │ All Tests Passed?         │
              └─────────────┬─────────────┘
                   YES ✓    │    NO ✗
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ↓             ↓             ↓
        CONTINUE      ABORT PIPELINE   SEND ALERT
                      (Stages 4-8 
                       never run)
                            
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 4: PACKAGE                                                │
│ ─────────────────────────────────────────────────────────────── │
│ • mvn package -DskipTests                                       │
│ • Create backend-0.0.1-SNAPSHOT.jar                            │
│ • Archive JAR artifact                                          │
│ Duration: ~45 seconds                                           │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 5: BUILD DOCKER IMAGES (Parallel)                        │
│ ─────────────────────────────────────────────────────────────── │
│ Backend:                    Frontend:                           │
│ • docker build backend      • docker build frontend            │
│ • Multi-stage build         • npm build + nginx                │
│ • Tag: latest & build#      • Tag: latest & build#             │
│ Duration: ~3-5 minutes (parallel execution)                     │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 6: PUSH TO DOCKERHUB                                      │
│ ─────────────────────────────────────────────────────────────── │
│ • docker login                                                  │
│ • docker push backend:latest                                    │
│ • docker push backend:build-tag                                 │
│ • docker push frontend:latest                                   │
│ • docker push frontend:build-tag                                │
│ Duration: ~2-3 minutes                                          │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 7: DEPLOY TO EC2                                          │
│ ─────────────────────────────────────────────────────────────── │
│ • SSH to deployment server                                      │
│ • docker-compose pull (download new images)                     │
│ • docker-compose up -d (restart services)                       │
│ • Wait for services to start                                    │
│ Duration: ~1-2 minutes                                          │
└─────────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────────┐
│ STAGE 8: HEALTH CHECK                                           │
│ ─────────────────────────────────────────────────────────────── │
│ • curl http://server:8080/actuator/health                      │
│ • Verify response: {"status":"UP"}                             │
│ • If unhealthy → Mark build as unstable                        │
│ Duration: ~10 seconds                                           │
└─────────────────────────────────────────────────────────────────┘
                            ↓
                    ┌───────────────┐
                    │ SUCCESS! 🎉   │
                    │ Build #X      │
                    │ Duration: ~10m│
                    └───────────────┘
```

---

## 🎯 Quick Reference Commands

### Jenkins Server Management

```bash
# SSH to Jenkins Server
ssh -i ~/.ssh/aws-keys/jenkins-key.pem ubuntu@YOUR_JENKINS_IP

# Check Jenkins Status
sudo systemctl status jenkins

# Start/Stop/Restart Jenkins
sudo systemctl start jenkins
sudo systemctl stop jenkins
sudo systemctl restart jenkins

# View Jenkins Logs
sudo journalctl -u jenkins -f  # Follow live logs
sudo journalctl -u jenkins -n 100  # Last 100 lines

# Get Initial Admin Password
sudo cat /var/lib/jenkins/secrets/initialAdminPassword
```

### Docker Commands

```bash
# List Docker Images
docker images

# List Running Containers
docker ps

# Stop All Containers
docker stop $(docker ps -aq)

# Remove All Containers
docker rm $(docker ps -aq)

# Clean Up Docker System
docker system prune -a --volumes

# View Container Logs
docker logs <container-name>
docker logs -f mediway-backend  # Follow logs
```

### Pipeline Workspace

```bash
# Navigate to Pipeline Workspace
cd /var/lib/jenkins/workspace/Mediway-CI-CD-Pipeline

# Run Maven Commands Manually
cd backend
mvn clean test  # Run tests
mvn clean package  # Create JAR

# View Test Reports
cd target/test-reports
ls -la
```

### Application Management (Deployment Server)

```bash
# SSH to Deployment Server
ssh -i ~/.ssh/aws-keys/mediway-app-key.pem ubuntu@YOUR_DEPLOYMENT_IP

# Navigate to App Directory
cd ~/mediway

# View Running Services
docker-compose ps

# View Logs
docker-compose logs -f  # All services
docker-compose logs -f backend  # Backend only
docker-compose logs -f frontend  # Frontend only

# Restart Services
docker-compose restart

# Stop Services
docker-compose down

# Start Services
docker-compose up -d

# Pull Latest Images
docker-compose pull
```

---

## 📈 Cost Estimation

### AWS EC2 Costs (Free Tier)

**Free Tier Limits (First 12 months):**
- 750 hours/month of t2.micro instance (not sufficient for Jenkins)
- After free tier expires:

**Estimated Monthly Costs:**

| Resource | Instance Type | Cost/Hour | Hours/Month | Monthly Cost |
|----------|---------------|-----------|-------------|--------------|
| Jenkins Server | t2.medium | $0.0464 | 730 | $33.87 |
| Deployment Server | t2.small | $0.023 | 730 | $16.79 |
| **Total** | | | | **$50.66** |

**Cost Savings Tips:**
1. Stop instances when not in use (development/testing only)
2. Use t2.micro for deployment server (save ~$15/month)
3. Use Spot Instances (save 70-90%)
4. Use Reserved Instances for 1-year commitment (save 40%)

### Other Costs

| Service | Cost |
|---------|------|
| DockerHub (Free Tier) | $0 |
| GitHub (Free Tier) | $0 |
| Data Transfer | ~$1-5/month |
| **Total Est. Monthly** | **$51-56** |

---

## 🚀 Next Steps

After completing this guide, you should:

### 1. Test Your Pipeline with Automated Builds
```bash
# Make a code change
# Commit and push
git commit -am "Test CI/CD pipeline"
git push origin main

# With webhook: Build starts immediately (within seconds)
# With Poll SCM: Build starts within 5 minutes

# Watch Jenkins dashboard for automatic build
```

**Expected behavior:**
- **Webhook:** Build appears within 5-10 seconds of push
- **Poll SCM:** Build appears within 5 minutes of push

### 2. Verify Automated Triggers

**If using GitHub Webhook (Recommended - Step 4.2.2):**
- Push a test commit to your repository
- Jenkins should build automatically within seconds
- Check Jenkins dashboard for the new build

**Verify webhook is working:**
1. GitHub Repo → Settings → Webhooks
2. Click on your webhook
3. Scroll to "Recent Deliveries"
4. You should see successful deliveries (green checkmark ✓)

**If using Poll SCM:**
- Jenkins checks GitHub every 5 minutes
- Builds automatically on new commits
- Slightly slower but reliable

### 3. Add Notifications

**Email Notifications:**
1. Manage Jenkins → Configure System → E-mail Notification
2. Configure SMTP server
3. Add post-build action to Jenkinsfile

**Slack Notifications:**
1. Install Slack Notification Plugin
2. Configure Slack workspace
3. Add notifications to Jenkinsfile

### 4. Implement Blue-Green Deployment

Modify Stage 7 to support zero-downtime deployments:
- Deploy to "green" environment
- Run smoke tests
- Switch traffic from "blue" to "green"
- Keep "blue" for rollback

### 5. Add Security Scanning

**Integrate:**
- SonarQube for code quality
- OWASP Dependency Check
- Trivy for Docker image scanning

### 6. Monitor Your Application

**Set up:**
- CloudWatch for AWS metrics
- ELK Stack for log aggregation
- Prometheus + Grafana for application metrics

---

## 📚 Additional Resources

### Official Documentation

- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Docker Documentation](https://docs.docker.com/)
- [TestNG Documentation](https://testng.org/doc/documentation-main.html)
- [AWS EC2 Documentation](https://docs.aws.amazon.com/ec2/)
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)

### Video Tutorials

- Jenkins Pipeline Tutorial: [YouTube Search](https://www.youtube.com/results?search_query=jenkins+pipeline+tutorial)
- Docker Tutorial: [Docker Getting Started](https://docs.docker.com/get-started/)
- AWS EC2 Tutorial: [AWS Free Tier](https://aws.amazon.com/free/)

### Community Support

- Jenkins Community: [community.jenkins.io](https://community.jenkins.io/)
- Stack Overflow: Tag `jenkins`, `docker`, `testng`
- Docker Community: [forums.docker.com](https://forums.docker.com/)

---

## ✅ Completion Checklist

Mark items as you complete them:

### Infrastructure Setup
- [ ] AWS account created
- [ ] Jenkins EC2 instance launched (t2.medium)
- [ ] Security groups configured
- [ ] SSH key pair created and saved
- [ ] Can SSH to Jenkins server
- [ ] Jenkins installed and running
- [ ] Docker installed on Jenkins server
- [ ] Maven installed on Jenkins server

### Jenkins Configuration
- [ ] Jenkins web UI accessible
- [ ] Initial setup wizard completed
- [ ] Admin user created
- [ ] Required plugins installed (Docker, TestNG, HTML Publisher, SSH Agent)
- [ ] JDK-17 configured
- [ ] Maven-3.9 configured
- [ ] Tools configuration saved

### Credentials
- [ ] DockerHub account created
- [ ] DockerHub credentials added to Jenkins
- [ ] GitHub repository set up
- [ ] GitHub Personal Access Token created
- [ ] GitHub webhook configured
- [ ] Webhook tested and working (green checkmark in GitHub)
- [ ] EC2 SSH key added to Jenkins (if deploying)

### Pipeline Setup
- [ ] Pipeline job created in Jenkins
- [ ] GitHub repository connected
- [ ] Jenkinsfile configured with DockerHub username
- [ ] Build triggers configured (GitHub webhook recommended)
- [ ] Webhook verified working in GitHub

### First Build
- [ ] Manual build triggered successfully
- [ ] Stage 1 (Checkout) passed
- [ ] Stage 2 (Build) passed
- [ ] Stage 3 (TestNG Tests) passed - All 88 tests
- [ ] Stage 4 (Package) passed
- [ ] Stage 5 (Docker Build) passed
- [ ] Stage 6 (DockerHub Push) passed
- [ ] Images visible on DockerHub

### Deployment (Optional)
- [ ] Deployment EC2 instance launched
- [ ] Docker installed on deployment server
- [ ] docker-compose.yml deployed
- [ ] SSH credentials configured in Jenkins
- [ ] Stage 7 (Deploy) passing
- [ ] Stage 8 (Health Check) passing
- [ ] Application accessible in browser

### Testing & Verification
- [ ] TestNG reports visible in Jenkins
- [ ] HTML reports accessible
- [ ] Docker images pushed to DockerHub
- [ ] Application running on deployment server
- [ ] Can access frontend in browser
- [ ] Can access backend API
- [ ] Logs show no errors

### Automation
- [ ] Push new commits automatically trigger builds
- [ ] Failed tests stop the pipeline
- [ ] Successful builds deploy automatically
- [ ] Team members have Jenkins access

---

## 🎓 What You've Learned

By completing this guide, you now understand:

✅ **AWS EC2**: Launch and manage cloud instances  
✅ **Jenkins**: Set up and configure CI/CD server  
✅ **Pipeline as Code**: Define build process in Jenkinsfile  
✅ **TestNG Integration**: Automated testing in CI/CD  
✅ **Docker**: Containerize applications  
✅ **Multi-stage Builds**: Optimize Docker images  
✅ **DockerHub**: Push and pull Docker images  
✅ **Quality Gates**: Prevent bad code from deploying  
✅ **Automated Deployment**: Deploy on every commit  
✅ **GitHub Webhooks**: Instant build triggers on code push  
✅ **Security**: Manage credentials securely  

---

## 🎉 Congratulations!

You've successfully implemented a complete CI/CD pipeline for the Medi.Way Healthcare Management System!

**Your pipeline now:**
- ✅ Automatically builds on every commit (instant with webhooks!)
- ✅ Runs 88 automated tests
- ✅ Prevents broken code from deploying
- ✅ Containerizes your application
- ✅ Deploys to production automatically
- ✅ Provides detailed test reports

**Pipeline Metrics:**
- **Build Time:** ~8-10 minutes
- **Test Coverage:** 88 automated tests
- **Deployment Time:** ~2 minutes
- **Trigger Latency:** <10 seconds (webhook) or ~5 minutes (poll)
- **Success Rate:** Should be 95%+

---

## 📞 Support

If you encounter issues:

1. **Check Troubleshooting Section** (above)
2. **Review Jenkins Console Output** for detailed error messages
3. **Check Server Resources** (CPU, memory, disk space)
4. **Verify Credentials** (DockerHub, GitHub, SSH)
5. **Search Community Forums** (Jenkins, Docker, Stack Overflow)

---

**Document Version:** 1.0  
**Last Updated:** March 6, 2026  
**Author:** Medi.Way Development Team  
**Project:** Healthcare Management System CI/CD Pipeline

---

**Happy Deploying! 🚀**
