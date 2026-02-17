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
       stage('UI Automation Execution') {
    steps {
        // This credentialsId must match the ID you created in Jenkins
        browserstack(credentialsId: 'browserstack_creds') {
            script {
                // The browserstack plugin provides BS_USER and BS_KEY automatically
                // We must map them to the names your DriverFactory expects
                withEnv([
                    "BROWSERSTACK_USERNAME=${env.BROWSERSTACK_USER}",
                    "BROWSERSTACK_ACCESS_KEY=${env.BROWSERSTACK_ACCESS_KEY}"
                ]) {
                    sh "mvn clean test -Dplatform=${params.PLATFORM} -Denv=${params.ENVIRONMENT} -Dcucumber.filter.tags=${params.TAGS}"
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
