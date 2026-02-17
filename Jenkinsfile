pipeline {
    agent {
        docker {
            image 'markhobson/maven-chrome:jdk-17'
            args '--shm-size=2g -v $HOME/.m2:/home/jenkins/.m2'
        }
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['qa', 'dev', 'prod'], description: 'Select environment')
        string(name: 'THREADS', defaultValue: '2', description: 'Parallel threads')
        choice(name: 'TAGS', choices: ['@smoke', '@regression', '@all'], description: 'Cucumber tags')
        choice(name: 'PLATFORM', choices: ['web', 'android', 'ios'], description: 'Target platform')
    }

    environment {
        ALLURE_RESULTS = 'target/allure-results'
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
            }
        }

        stage('UI Automation Execution') {
            steps {
                // The specialized BrowserStack block
                browserstack(credentialsId: 'browserstack_creds') {
                    script {
                        try {
                            // Injecting credentials into the Shell environment for DriverFactory
                            withEnv([
                                "BROWSERSTACK_USERNAME=${env.BROWSERSTACK_USER}",
                                "BROWSERSTACK_ACCESS_KEY=${env.BROWSERSTACK_ACCESS_KEY}"
                            ]) {
                                sh "mvn clean test -e -Dplatform=${params.PLATFORM} -Denv=${params.ENVIRONMENT} -Dcucumber.filter.tags=${params.TAGS} -Ddataproviderthreadcount=${params.THREADS}"
                            }
                        } catch (Exception e) {
                            currentBuild.result = 'UNSTABLE'
                            echo "Tests failed, but proceeding to reports..."
                        }
                    }
                }
            }
        }

        stage('Reports') {
            steps {
                script {
                    sh 'mvn io.qameta.allure:allure-maven:report'
                    // Generate environment.properties for Allure
                    sh "mkdir -p ${env.ALLURE_RESULTS}"
                    def props = "Platform=${params.PLATFORM}\nEnvironment=${params.ENVIRONMENT}"
                    writeFile file: "${env.ALLURE_RESULTS}/environment.properties", text: props
                    
                    allure includeProperties: false, results: [[path: 'target/allure-results']]
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.log', allowEmptyArchive: true
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }
        
        failure {
            emailext(
                subject: "❌ FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed. Check console: ${env.BUILD_URL}",
                to: "poulomidas89@gmail.com"
            )
        }
    }
}
