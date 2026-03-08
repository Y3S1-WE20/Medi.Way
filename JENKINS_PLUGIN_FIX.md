# Jenkins Pipeline Plugin Installation Fix

## Problem
```
Failed to load: Pipeline (workflow-aggregator 608.v67378e9d3db_1)
 - Failed to load: Pipeline: Basic Steps (workflow-basic-steps 1098.v808b_fd7f8cf4)
```

## Solution Options (Try in Order)

---

## Option 1: Update Jenkins First (RECOMMENDED)

The plugin versions are too new for your Jenkins. Update Jenkins first:

```bash
# Stop Jenkins
brew services stop jenkins-lts

# Update Jenkins to latest LTS
brew upgrade jenkins-lts

# Start Jenkins
brew services start jenkins-lts

# Verify version
open http://localhost:8080/manage/systemInfo
```

Then try installing plugins again:
1. Manage Jenkins → Plugins → Available Plugins
2. Search and install: **Pipeline**
3. Restart Jenkins when prompted

---

## Option 2: Install Suggested Plugins (Fresh Setup)

If you just installed Jenkins, use the **Install Suggested Plugins** option during setup wizard:

1. Open http://localhost:8080
2. Enter admin password from: `cat ~/.jenkins/secrets/initialAdminPassword`
3. Choose: **Install suggested plugins** (this includes Pipeline)
4. Wait for all plugins to install (~5 minutes)
5. Create admin user
6. Save and Finish

This installs compatible versions automatically.

---

## Option 3: Install Pipeline Plugin Dependencies First

Pipeline plugin needs several base plugins. Install these in order:

### Step 1: Update Plugin Manager
```
Manage Jenkins → Plugins → Advanced Settings
→ Click "Check now" button at bottom
→ Wait 30 seconds
```

### Step 2: Install Base Plugins First
Go to: Manage Jenkins → Plugins → Available Plugins

Install these **one at a time** in order:
1. ✅ **Script Security**
2. ✅ **Structs**
3. ✅ **Credentials**
4. ✅ **Git**
5. ✅ **SCM API**
6. ✅ **Durable Task**
7. ✅ **Pipeline: API**
8. ✅ **Pipeline: Step API**
9. ✅ **Pipeline: Basic Steps**
10. ✅ **Pipeline: Groovy**
11. ✅ **Pipeline**

After each install, click **Install without restart** and wait.

---

## Option 4: Manual Plugin Installation (If Update Center Fails)

Download and install plugins manually:

### Step 1: Download Plugins

Visit: https://updates.jenkins.io/download/plugins/

Download these `.hpi` files (use compatible versions for Jenkins 2.x):

1. workflow-aggregator (Pipeline)
2. workflow-api
3. workflow-basic-steps
4. workflow-cps
5. workflow-durable-task-step
6. workflow-job
7. workflow-step-api
8. workflow-support

### Step 2: Upload to Jenkins

```bash
# Copy plugins to Jenkins
cp *.hpi ~/.jenkins/plugins/

# Restart Jenkins
brew services restart jenkins-lts
```

Or via UI:
1. Manage Jenkins → Plugins → Advanced Settings
2. Upload Plugin section
3. Choose `.hpi` file
4. Click Upload
5. Restart Jenkins

---

## Option 5: Use Jenkins Without Pipeline Plugin (Alternative)

If plugins won't install, use **Freestyle project** instead:

1. New Item → **Freestyle project** (not Pipeline)
2. Source Code Management → Git
3. Build → Add build step → Execute shell
4. Add your build commands directly

**Example build steps**:
```bash
# Build
cd backend
mvn clean package

# Test
mvn test

# Docker Build
docker build -t shiranthads/mediway-backend:${BUILD_NUMBER} .

# Docker Push
docker login -u shiranthads -p ${DOCKERHUB_PASSWORD}
docker push shiranthads/mediway-backend:${BUILD_NUMBER}

# Deploy
cd ..
docker-compose up -d
```

---

## Verification Steps

After installing Pipeline plugin successfully:

### Check Plugin Status
```
Manage Jenkins → Plugins → Installed Plugins
```

Look for these with green checkmarks:
- ✅ Pipeline
- ✅ Pipeline: Basic Steps
- ✅ Pipeline: Groovy
- ✅ Pipeline: Job
- ✅ Pipeline: API

### Test Pipeline Creation
```
New Item → Enter name → Choose "Pipeline" → OK
```

If "Pipeline" appears as an option, plugins installed correctly!

---

## Check Jenkins Version Compatibility

### Current Jenkins Version
```
Manage Jenkins → System Information
→ Look for "Jenkins" version
```

### Minimum Required Versions
- **Jenkins**: 2.387.3 or higher (LTS)
- **Java**: 11 or higher

### Check Java Version
```bash
java -version
```

Should show: `openjdk version "11"` or higher

If Java is old:
```bash
# Install Java 17 (recommended)
brew install openjdk@17

# Link it
sudo ln -sfn /opt/homebrew/opt/openjdk@17/libexec/openjdk.jdk \
  /Library/Java/JavaVirtualMachines/openjdk-17.jdk
```

---

## Troubleshooting Commands

### Check Jenkins Logs
```bash
# View live logs
tail -f ~/.jenkins/logs/jenkins.log

# Search for errors
grep -i error ~/.jenkins/logs/jenkins.log | tail -20
```

### Restart Jenkins
```bash
# Via Homebrew
brew services restart jenkins-lts

# Via Jenkins UI
# Manage Jenkins → Reload Configuration from Disk
```

### Clean Plugin Cache
```bash
# Stop Jenkins
brew services stop jenkins-lts

# Clean cache
rm -rf ~/.jenkins/plugins/*.tmp
rm -rf ~/.jenkins/plugins/*.bak

# Start Jenkins
brew services start jenkins-lts
```

### Reset Jenkins Plugins (Nuclear Option)
```bash
# ⚠️ WARNING: This removes ALL plugins

# Stop Jenkins
brew services stop jenkins-lts

# Backup current plugins
cp -r ~/.jenkins/plugins ~/.jenkins/plugins.backup

# Remove all plugins
rm -rf ~/.jenkins/plugins/*

# Start Jenkins
brew services start jenkins-lts

# Re-run setup wizard (will reinstall suggested plugins)
rm ~/.jenkins/config.xml
open http://localhost:8080
```

---

## Quick Fix Script

Save this as `fix-jenkins-plugins.sh`:

```bash
#!/bin/bash

echo "🔧 Fixing Jenkins Plugin Issues..."

# Stop Jenkins
echo "Stopping Jenkins..."
brew services stop jenkins-lts
sleep 3

# Update Jenkins
echo "Updating Jenkins to latest LTS..."
brew upgrade jenkins-lts

# Clean plugin cache
echo "Cleaning plugin cache..."
rm -rf ~/.jenkins/plugins/*.tmp
rm -rf ~/.jenkins/plugins/*.bak

# Start Jenkins
echo "Starting Jenkins..."
brew services start jenkins-lts
sleep 10

# Open Jenkins
echo "Opening Jenkins..."
open http://localhost:8080

echo "✅ Done!"
echo ""
echo "Now try installing Pipeline plugin again:"
echo "Manage Jenkins → Plugins → Available Plugins → Search 'Pipeline'"
```

Run it:
```bash
chmod +x fix-jenkins-plugins.sh
./fix-jenkins-plugins.sh
```

---

## What To Do Right Now

### Recommended Path:

1. **Check if Jenkins is running**:
   ```bash
   brew services list | grep jenkins
   ```

2. **Check Jenkins version**:
   ```
   open http://localhost:8080/manage/systemInfo
   ```
   Look for Jenkins version (need 2.387.3+)

3. **If Jenkins version is old, update it**:
   ```bash
   brew services stop jenkins-lts
   brew upgrade jenkins-lts
   brew services start jenkins-lts
   ```

4. **Try installing Pipeline plugin again**:
   - Go to: http://localhost:8080/manage/pluginManager/available
   - Search: "Pipeline"
   - Check the box next to "Pipeline"
   - Click "Install without restart"
   - Wait for installation

5. **If still fails, use Option 2** (Install Suggested Plugins during setup wizard)

---

## Alternative: Skip Plugins, Use Simple Pipeline

If you're in a hurry, you can create a **minimal working pipeline** without installing additional plugins:

1. Create New Item → Choose "Multibranch Pipeline" (built-in)
2. Or use the existing `Jenkinsfile.local` with basic Pipeline plugin

Most Jenkins installations come with **basic Pipeline support**. The error might be about the **aggregator** plugin specifically.

Try installing just these core plugins:
- ✅ Git plugin
- ✅ Docker plugin
- ✅ TestNG Results Plugin

Then create your job and see if it works!

---

## Still Not Working?

If none of the above works, you have two options:

### Option A: Fresh Jenkins Install
```bash
# Completely remove Jenkins
brew services stop jenkins-lts
brew uninstall jenkins-lts
rm -rf ~/.jenkins

# Reinstall
brew install jenkins-lts
brew services start jenkins-lts
open http://localhost:8080

# Choose "Install suggested plugins" during setup
```

### Option B: Use Docker-Based Jenkins (Guaranteed to Work)
```bash
# Stop Homebrew Jenkins
brew services stop jenkins-lts

# Run Jenkins in Docker
docker run -d \
  -p 8080:8080 -p 50000:50000 \
  -v jenkins_home:/var/jenkins_home \
  -v /var/run/docker.sock:/var/run/docker.sock \
  --name jenkins \
  jenkins/jenkins:lts

# Get admin password
docker exec jenkins cat /var/jenkins_home/secrets/initialAdminPassword

# Open Jenkins
open http://localhost:8080
```

Docker Jenkins comes with all plugins working out of the box!

---

## Success Indicator

You'll know plugins are installed correctly when:

1. ✅ No error messages in plugin manager
2. ✅ "Pipeline" appears as job type option
3. ✅ Can create new Pipeline job without errors
4. ✅ All plugins show green checkmarks in installed plugins list

---

**Priority**: Try **Option 1 (Update Jenkins)** first - this fixes 90% of plugin issues!
