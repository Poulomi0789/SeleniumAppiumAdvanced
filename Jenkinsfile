pipeline {
    agent {
        docker {
            image 'markhobson/maven-chrome:jdk-17'
            args '--shm-size=2g -v $HOME/.m2:/home/jenkins/.m2'
        }
    }

    parameters {
        choice(name: 'ENVIRONMENT', choices: ['qa', 'dev', 'prod'], description: 'Select environment')
        string(name: 'THREADS', defaultValue: '2', description: 'Parallel instances')
        choice(name: 'TAGS', choices: ['@smoke', '@regression', '@all'], description: 'Cucumber tags')
        choice(name: 'PLATFORM', choices: ['web', 'android', 'ios'], description: 'Target platform')
    }

    environment {
        ALLURE_RESULTS = 'target/allure-results'
        // Moving this to a 'def' or hardcoding in post to avoid Binding errors
        RECIPIENTS = 'poulomidas89@gmail.com'
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
                // FIX: Use the 'browserstack' step provided by the plugin 
                // This automatically sets BROWSERSTACK_USERNAME and BROWSERSTACK_ACCESS_KEY
                browserstack(credentialsId: 'browserstack_creds') {
                    script {
                        try {
                            sh "mvn clean test -e -Dplatform=${params.PLATFORM} -Denv=${params.ENVIRONMENT} -Dcucumber.filter.tags=${params.TAGS} -Ddataproviderthreadcount=${params.THREADS}"
                        } catch (Exception e) {
                            currentBuild.result = 'UNSTABLE'
                        }
                    }
                }
            }
        }

        stage('Reports') {
            steps {
                sh 'mvn io.qameta.allure:allure-maven:report'
                allure includeProperties: false, results: [[path: 'target/allure-results']]
            }
        }
    }

    // FIX: Ensure 'post' is inside the pipeline so it stays within the agent context
    post {
        always {
            // This now has access to the workspace (hudson.FilePath)
            archiveArtifacts artifacts: 'target/*.log', allowEmptyArchive: true
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }
        
        failure {
            emailext(
                subject: "❌ FAILED: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build failed. Check console: ${env.BUILD_URL}",
                to: "poulomidas89@gmail.com" // Used direct string to avoid BindingException
            )
        }
    }
}
