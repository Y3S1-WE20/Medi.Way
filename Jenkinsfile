// ============================================================
// MEDI.WAY CI/CD Pipeline - Jenkinsfile
// ============================================================
// 
// Pipeline Flow:
// 1. Checkout → 2. Build → 3. Test (TestNG) → 4. Package
// 5. Build Docker Images → 6. Push to DockerHub → 7. Deploy to EC2
//
// Prerequisites:
// - Jenkins with Pipeline plugin
// - Docker installed on Jenkins agent
// - DockerHub credentials configured
// - EC2 SSH key configured
// - TestNG Results Plugin
// ============================================================

pipeline {
    agent any
    
    // ============================================================
    // ENVIRONMENT VARIABLES
    // ============================================================
    environment {
        // Docker Hub credentials (configured in Jenkins)
        DOCKERHUB_CREDENTIALS = credentials('dockerhub-credentials')
        DOCKER_USERNAME = 'shiranthads'  // Your DockerHub username
        
        // AWS EC2 Configuration
        EC2_HOST = '13.62.209.133'  // Replace with your EC2 public IP when deploying
        EC2_USER = 'ubuntu'
        
        // Application Configuration
        APP_NAME = 'mediway'
        
        // Build Information
        BUILD_TIMESTAMP = sh(script: 'date +%Y%m%d%H%M%S', returnStdout: true).trim()
    }
    
    // ============================================================
    // TOOLS CONFIGURATION
    // ============================================================
    tools {
        maven 'Maven-3.9'    // Configure in Jenkins Global Tool Configuration
        jdk 'JDK-17'         // Configure in Jenkins Global Tool Configuration
    }
    
    // ============================================================
    // BUILD OPTIONS
    // ============================================================
    options {
        // Keep last 10 builds
        buildDiscarder(logRotator(numToKeepStr: '10'))
        
        // Timeout after 30 minutes
        timeout(time: 30, unit: 'MINUTES')
        
        // Add timestamps to console output
        timestamps()
        
        // Don't allow concurrent builds
        disableConcurrentBuilds()
    }
    
    // ============================================================
    // PIPELINE STAGES
    // ============================================================
    stages {
        
        // ────────────────────────────────────────────────────────
        // STAGE 1: CHECKOUT
        // ────────────────────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo '📥 Stage 1: Checking out source code from GitHub...'
                
                // Checkout source code
                checkout scm
                
                // Set build info
                script {
                    env.GIT_COMMIT_SHORT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
                    env.GIT_BRANCH_NAME = sh(script: 'git rev-parse --abbrev-ref HEAD', returnStdout: true).trim()
                    env.IMAGE_TAG = "${BUILD_NUMBER}-${GIT_COMMIT_SHORT}"
                }
                
                echo "Git Commit: ${GIT_COMMIT_SHORT}"
                echo "Git Branch: ${GIT_BRANCH_NAME}"
                echo "Image Tag: ${IMAGE_TAG}"
            }
        }
        
        // ────────────────────────────────────────────────────────
        // STAGE 2: BUILD BACKEND
        // ────────────────────────────────────────────────────────
        stage('Build Backend') {
            steps {
                echo '🔨 Stage 2: Building backend application...'
                
                dir('backend') {
                    sh '''
                        echo "Maven Version:"
                        mvn --version
                        
                        echo "Compiling backend..."
                        mvn clean compile -B -q
                    '''
                }
            }
            post {
                success {
                    echo '✅ Backend build successful'
                }
                failure {
                    echo '❌ Backend build failed'
                }
            }
        }
        
        // ────────────────────────────────────────────────────────
        // STAGE 3: RUN TESTNG TESTS
        // ────────────────────────────────────────────────────────
        stage('Run TestNG Tests') {
            steps {
                echo '🧪 Stage 3: Running TestNG tests...'
                
                dir('backend') {
                    sh '''
                        echo "Running TestNG test suite..."
                        mvn test -B
                    '''
                }
            }
            post {
                always {
                    echo '📊 Publishing TestNG results...'
                    
                    // Publish TestNG Results
                    testNG reportFilenamePattern: '**/testng-results.xml'
                    
                    // Publish HTML Report (TestNG native report)
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'backend/test-output',
                        reportFiles: 'index.html',
                        reportName: 'TestNG HTML Report',
                        reportTitles: 'TestNG Report'
                    ])
                    
                    // Publish custom HTML summary
                    publishHTML(target: [
                        allowMissing: true,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: 'backend/test-output',
                        reportFiles: 'mediway-summary.html',
                        reportName: 'MEDI.WAY Test Summary',
                        reportTitles: 'MEDI.WAY Summary'
                    ])
                    
                    // Archive JUnit-compatible results
                    junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
                    
                    // Archive test artifacts
                    archiveArtifacts artifacts: 'backend/test-output/**/*', allowEmptyArchive: true
                }
                success {
                    echo '✅ All tests passed!'
                }
                failure {
                    echo '❌ Some tests failed. Check the TestNG report for details.'
                }
            }
        }
        
        // ────────────────────────────────────────────────────────
        // STAGE 4: PACKAGE APPLICATION
        // ────────────────────────────────────────────────────────
        stage('Package') {
            steps {
                echo '📦 Stage 4: Packaging application...'
                
                dir('backend') {
                    sh '''
                        echo "Creating JAR package..."
                        mvn package -DskipTests -B -q
                        
                        echo "Package created:"
                        ls -la target/*.jar
                    '''
                }
            }
            post {
                success {
                    // Archive the JAR file
                    archiveArtifacts artifacts: 'backend/target/*.jar', fingerprint: true
                }
            }
        }
        
        // ────────────────────────────────────────────────────────
        // STAGE 5: BUILD DOCKER IMAGES
        // ────────────────────────────────────────────────────────
        stage('Build Docker Images') {
            parallel {
                stage('Build Backend Image') {
                    steps {
                        echo '🐳 Building backend Docker image...'
                        
                        dir('backend') {
                            sh """
                                echo "Building backend image..."
                                docker build \
                                    -t ${DOCKER_USERNAME}/${APP_NAME}-backend:${IMAGE_TAG} \
                                    -t ${DOCKER_USERNAME}/${APP_NAME}-backend:latest \
                                    --build-arg BUILD_VERSION=${IMAGE_TAG} \
                                    .
                                
                                echo "Backend images built:"
                                docker images | grep ${APP_NAME}-backend
                            """
                        }
                    }
                }
                stage('Build Frontend Image') {
                    steps {
                        echo '🐳 Building frontend Docker image...'
                        
                        dir('frontend') {
                            sh """
                                echo "Building frontend image..."
                                docker build \
                                    -t ${DOCKER_USERNAME}/${APP_NAME}-frontend:${IMAGE_TAG} \
                                    -t ${DOCKER_USERNAME}/${APP_NAME}-frontend:latest \
                                    .
                                
                                echo "Frontend images built:"
                                docker images | grep ${APP_NAME}-frontend
                            """
                        }
                    }
                }
            }
        }
        
        // ────────────────────────────────────────────────────────
        // STAGE 6: PUSH TO DOCKER HUB
        // ────────────────────────────────────────────────────────
        stage('Push to Docker Hub') {
            steps {
                echo '📤 Stage 6: Pushing images to Docker Hub...'
                
                sh """
                    echo "Logging into Docker Hub..."
                    echo ${DOCKERHUB_CREDENTIALS_PSW} | docker login -u ${DOCKERHUB_CREDENTIALS_USR} --password-stdin
                    
                    echo "Pushing backend images..."
                    docker push ${DOCKER_USERNAME}/${APP_NAME}-backend:${IMAGE_TAG}
                    docker push ${DOCKER_USERNAME}/${APP_NAME}-backend:latest
                    
                    echo "Pushing frontend images..."
                    docker push ${DOCKER_USERNAME}/${APP_NAME}-frontend:${IMAGE_TAG}
                    docker push ${DOCKER_USERNAME}/${APP_NAME}-frontend:latest
                    
                    echo "Logging out..."
                    docker logout
                """
            }
            post {
                success {
                    echo '✅ Images pushed to Docker Hub successfully'
                }
            }
        }
        
        // ────────────────────────────────────────────────────────
        // STAGE 7: DEPLOY TO AWS EC2
        // ────────────────────────────────────────────────────────
        stage('Deploy to EC2') {
            steps {
                echo '🚀 Stage 7: Deploying to AWS EC2...'
                
                sshagent(['ec2-ssh-key']) {
                    sh """
                        echo "Connecting to EC2 instance..."
                        
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} '
                            echo "Connected to EC2 instance"
                            
                            # Navigate to application directory
                            cd /home/ubuntu/mediway
                            
                            # Export environment variables
                            export BUILD_NUMBER=${IMAGE_TAG}
                            export DOCKER_USERNAME=${DOCKER_USERNAME}
                            
                            # Pull latest images
                            echo "Pulling latest images..."
                            docker-compose pull
                            
                            # Stop existing containers
                            echo "Stopping existing containers..."
                            docker-compose down
                            
                            # Start new containers
                            echo "Starting new containers..."
                            docker-compose up -d
                            
                            # Cleanup old images
                            echo "Cleaning up old images..."
                            docker image prune -f
                            
                            # Show running containers
                            echo "Running containers:"
                            docker-compose ps
                        '
                    """
                }
            }
            post {
                success {
                    echo '✅ Deployment to EC2 successful'
                }
                failure {
                    echo '❌ Deployment to EC2 failed'
                }
            }
        }
        
        // ────────────────────────────────────────────────────────
        // STAGE 8: HEALTH CHECK
        // ────────────────────────────────────────────────────────
        stage('Health Check') {
            steps {
                echo '🏥 Stage 8: Running health checks...'
                
                script {
                    // Wait for services to start
                    sleep(time: 45, unit: 'SECONDS')
                    
                    // Check backend health
                    def backendHealth = sh(
                        script: "curl -s -o /dev/null -w '%{http_code}' http://${EC2_HOST}:8080/actuator/health || echo '000'",
                        returnStdout: true
                    ).trim()
                    
                    // Check frontend health
                    def frontendHealth = sh(
                        script: "curl -s -o /dev/null -w '%{http_code}' http://${EC2_HOST}/ || echo '000'",
                        returnStdout: true
                    ).trim()
                    
                    echo "Backend Health Status: ${backendHealth}"
                    echo "Frontend Health Status: ${frontendHealth}"
                    
                    if (backendHealth != '200') {
                        error "Backend health check failed! Status: ${backendHealth}"
                    }
                    
                    if (frontendHealth != '200') {
                        error "Frontend health check failed! Status: ${frontendHealth}"
                    }
                }
            }
            post {
                success {
                    echo '✅ All health checks passed!'
                }
            }
        }
    }
    
    // ============================================================
    // POST-BUILD ACTIONS
    // ============================================================
    post {
        always {
            echo '🧹 Cleaning up workspace...'
            
            // Clean up Docker images on Jenkins agent
            sh '''
                docker system prune -f || true
            '''
            
            // Clean workspace
            cleanWs()
        }
        
        success {
            echo '''
╔══════════════════════════════════════════════════════════════╗
║                    PIPELINE SUCCESS! 🎉                      ║
╠══════════════════════════════════════════════════════════════╣
║  All stages completed successfully.                          ║
║                                                              ║
║  Application deployed and running at:                        ║
║  Frontend: http://${EC2_HOST}/                               ║
║  Backend:  http://${EC2_HOST}:8080/                          ║
╚══════════════════════════════════════════════════════════════╝
            '''
        }
        
        failure {
            echo '''
╔══════════════════════════════════════════════════════════════╗
║                    PIPELINE FAILED! ❌                        ║
╠══════════════════════════════════════════════════════════════╣
║  One or more stages failed.                                  ║
║  Please check the console output for details.                ║
╚══════════════════════════════════════════════════════════════╝
            '''
            
            // Optional: Send notification (Slack, Email, etc.)
            // slackSend channel: '#deployments', message: "Build ${BUILD_NUMBER} failed!"
        }
        
        unstable {
            echo '⚠️ Pipeline completed with warnings (unstable build)'
        }
    }
}
