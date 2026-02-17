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
        EMAIL_RECIPIENTS = 'poulomidas89@gmail.com'
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
                // This ID must match exactly what you created in Manage Jenkins > Credentials
                browserstack(credentialsId: 'browserstack_creds') {
                    script {
                        try {
                            // Passing BS_USER and BS_KEY directly as Maven properties
                            // This ensures Java's System.getProperty("BROWSERSTACK_USERNAME") catches them
                            sh "mvn clean test -e " +
                               "-Dplatform=${params.PLATFORM} " +
                               "-Denv=${params.ENVIRONMENT} " +
                               "-Dcucumber.filter.tags=${params.TAGS} " +
                               "-Ddataproviderthreadcount=${params.THREADS} " +
                               "-DBROWSERSTACK_USERNAME=${env.BROWSERSTACK_USER} " +
                               "-DBROWSERSTACK_ACCESS_KEY=${env.BROWSERSTACK_ACCESS_KEY}"
                        } catch (Exception e) {
                            currentBuild.result = 'UNSTABLE'
                            echo "Tests failed, proceeding to report generation..."
                        }
                    }
                }
            }
        }

        stage('Reporting') {
            steps {
                script {
                    sh 'mvn io.qameta.allure:allure-maven:report'
                    
                    // Generate environment.properties for Allure dashboard
                    sh "mkdir -p ${env.ALLURE_RESULTS}"
                    writeFile file: "${env.ALLURE_RESULTS}/environment.properties", 
                              text: "Platform=${params.PLATFORM}\nEnvironment=${params.ENVIRONMENT}"

                    if (fileExists('target/site/allure-maven-plugin')) {
                        zip zipFile: 'allure-report.zip', dir: 'target/site/allure-maven-plugin', archive: true
                    }
                    
                    allure includeProperties: false, results: [[path: 'target/allure-results']]
                }
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: '**/*.log', allowEmptyArchive: true
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }

        success {
            emailext(
                subject: "✅ PASSED | ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Build Successful. View Report: ${env.BUILD_URL}allure",
                to: "${EMAIL_RECIPIENTS}"
            )
        }

        failure {
            emailext(
                subject: "❌ FAILED | ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                body: "Pipeline Error. Console: ${env.BUILD_URL}console",
                to: "${EMAIL_RECIPIENTS}"
            )
        }
    }
}
