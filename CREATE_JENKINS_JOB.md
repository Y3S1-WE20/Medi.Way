# Quick Fix: Create Jenkins Pipeline Job

## The Problem
The job "Mediway-Local-CI-CD" doesn't exist yet in Jenkins.

## Solution: Create the Job Now (2 Minutes)

### Step-by-Step Instructions:

1. **Go to Jenkins Dashboard**
   - URL: http://localhost:8080
   - You should see the main Jenkins page

2. **Click "New Item"** (top left corner)

3. **Enter Job Details**:
   - **Item name**: `Mediway-Local-CI-CD`
   - **Select**: ✅ **Pipeline** (scroll down to find it)
   - **Click**: "OK" button at bottom

4. **Configure the Pipeline**:

   **General Tab**:
   - Description: `Local CI/CD pipeline for Medi.Way Healthcare System`

   **Build Triggers**:
   - ✅ Check: **Poll SCM**
   - Schedule: `H/5 * * * *`
     (This checks GitHub every 5 minutes for changes)

   **Pipeline Section** (scroll down):
   - Definition: Select **"Pipeline script from SCM"**
   - SCM: Select **"Git"**
   - Repository URL: `/Users/shiranthadissanayake/Documents/GitHub/Medi.Way`
   - Branch Specifier: `*/main` (or `*/master` if your branch is master)
   - Script Path: `Jenkinsfile.local`

5. **Click "Save"** at the bottom

6. **You'll be redirected to the job page**
   - Now you can click **"Build Now"** to run the pipeline!

---

## Verify It Worked

After saving, the URL should be:
```
http://localhost:8080/job/Mediway-Local-CI-CD/
```

You should see:
- Left sidebar with "Build Now" button
- Main area with "Build History" (empty at first)
- No more "Oops!" error

---

## If You See Errors:

### "Git executable not found"
```bash
# Install Git via Homebrew
brew install git
```

### "Jenkinsfile.local not found"
Check the file exists:
```bash
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way
ls -la Jenkinsfile.local
```

If missing, the file should be in your project root.

### "Repository not found"
Make sure you're using the **local file path**, not a GitHub URL:
```
/Users/shiranthadissanayake/Documents/GitHub/Medi.Way
```
(NOT `https://github.com/...`)

---

## Quick Checklist

Before clicking "Save", verify:
- [ ] Item name: `Mediway-Local-CI-CD`
- [ ] Type: Pipeline
- [ ] Poll SCM: `H/5 * * * *`
- [ ] Definition: Pipeline script from SCM
- [ ] SCM: Git
- [ ] Repository URL: `/Users/shiranthadissanayake/Documents/GitHub/Medi.Way`
- [ ] Script Path: `Jenkinsfile.local`

---

## Alternative: Create Via Jenkins CLI (Advanced)

If UI doesn't work, you can create the job via CLI:

```bash
cd /Users/shiranthadissanayake/Documents/GitHub/Medi.Way

# Create job config XML
cat > job-config.xml << 'EOF'
<?xml version='1.1' encoding='UTF-8'?>
<flow-definition plugin="workflow-job@2.40">
  <description>Local CI/CD pipeline for Medi.Way Healthcare System</description>
  <keepDependencies>false</keepDependencies>
  <properties/>
  <definition class="org.jenkinsci.plugins.workflow.cps.CpsScmFlowDefinition" plugin="workflow-cps@2.90">
    <scm class="hudson.plugins.git.GitSCM" plugin="git@4.8.2">
      <configVersion>2</configVersion>
      <userRemoteConfigs>
        <hudson.plugins.git.UserRemoteConfig>
          <url>/Users/shiranthadissanayake/Documents/GitHub/Medi.Way</url>
        </hudson.plugins.git.UserRemoteConfig>
      </userRemoteConfigs>
      <branches>
        <hudson.plugins.git.BranchSpec>
          <name>*/main</name>
        </hudson.plugins.git.BranchSpec>
      </branches>
    </scm>
    <scriptPath>Jenkinsfile.local</scriptPath>
    <lightweight>true</lightweight>
  </definition>
  <triggers>
    <hudson.triggers.SCMTrigger>
      <spec>H/5 * * * *</spec>
      <ignorePostCommitHooks>false</ignorePostCommitHooks>
    </hudson.triggers.SCMTrigger>
  </triggers>
</flow-definition>
EOF

# Create job (requires Jenkins CLI)
# This is just a reference - easier to use UI
```

---

## Next Step

Once the job is created successfully:
1. Click **"Build Now"** button
2. Watch the build progress in "Build History"
3. Click on the build number (e.g., #1) to see details
4. Click "Console Output" to see live logs

The build will take ~6 minutes to complete all 8 stages.
