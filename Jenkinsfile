pipeline {
    agent {
      docker {
            image 'markhobson/maven-chrome:jdk-17'
            args '--network automation-network --shm-size=2g -v $HOME/.m2:/home/jenkins/.m2'
}
    }

    parameters {
        choice(
            name: 'ENVIRONMENT',
            choices: ['qa', 'dev', 'prod'],
            description: 'Select the target environment properties'
        )

        string(
            name: 'THREADS',
            defaultValue: '2',
            description: 'Number of parallel thread execution'
        )

        choice(
            name: 'TAGS',
            choices: ['@smoke', '@regression', '@all'],
            description: 'Filter scenarios by Cucumber tags'
        )

        choice(
            name: 'PLATFORM',
            choices: ['web', 'android', 'ios'],
            description: 'Target platform: Web (Local) or Mobile (BrowserStack)'
        )
    }

    environment {
        ALLURE_RESULTS = 'target/allure-results'
        EMAIL_RECIPIENTS = 'poulomidas89@gmail.com'

        /* * Securely link the Jenkins Credential 'browserstack_creds'
         * to the Environment Variables used in DriverFactory.java
         */
        BS_CRED = credentials('browserstack_creds')
        BROWSERSTACK_USERNAME = "${env.BS_CRED_USR}"
        BROWSERSTACK_ACCESS_KEY = "${env.BS_CRED_PSW}"
    }

    stages {
        stage('Initialize & Checkout') {
            steps {
                cleanWs()
                checkout scm
                echo "🚀 Execution Started: Platform=${params.PLATFORM} | Env=${params.ENVIRONMENT}"
            }
        }

        stage('Automation Execution') {
            steps {
                script {
                    try {
                        // -Dcucumber.filter.tags is used to pass the TAGS parameter to the Runner
                        sh "mvn clean test -e " +
                           "-Dplatform=${params.PLATFORM} " +
                           "-Denv=${params.ENVIRONMENT} " +
                           "-Dcucumber.filter.tags=${params.TAGS} " +
                           "-Ddataproviderthreadcount=${params.THREADS}"
                    } catch (Exception e) {
                        // Mark build as Unstable so report generation still triggers
                        currentBuild.result = 'UNSTABLE'
                        echo "⚠️ Some tests failed. Generating report..."
                    }
                }
            }
        }

        stage('Report Generation') {
            steps {
                // Generates Allure Static HTML from results
                sh 'mvn io.qameta.allure:allure-maven:report'

                script {
                    if (fileExists('target/site/allure-maven-plugin')) {
                        zip zipFile: 'allure-report.zip',
                            dir: 'target/site/allure-maven-plugin',
                            archive: true
                    }
                }
            }
        }

        stage('Publish Reports') {
            steps {
                allure includeProperties: false, results: [[path: 'target/allure-results']]
            }
        }
    }

    post {
        always {
            archiveArtifacts artifacts: 'target/*.log', allowEmptyArchive: true
            junit testResults: '**/target/surefire-reports/*.xml', allowEmptyResults: true
        }

        success {
            emailext(
                subject: "✅ PASSED: ${env.JOB_NAME} [${params.PLATFORM}] #${env.BUILD_NUMBER}",
                body: """<h2>Build Successful 🚀</h2>
                         <b>Platform:</b> ${params.PLATFORM}<br>
                         <b>Environment:</b> ${params.ENVIRONMENT}<br>
                         <b>Tags Run:</b> ${params.TAGS}<br>
                         <b>Allure Report:</b> <a href="${env.BUILD_URL}allure">View Results</a>""",
                attachmentsPattern: 'allure-report.zip',
                mimeType: 'text/html',
                to: "${EMAIL_RECIPIENTS}"
            )
        }

        failure {
            emailext(
                subject: "❌ FAILED: ${env.JOB_NAME} [${params.PLATFORM}] #${env.BUILD_NUMBER}",
                body: """<h2>Pipeline Failed ❌</h2>
                         <b>Check Console Output:</b> <a href="${env.BUILD_URL}console">Click here</a>""",
                to: "${EMAIL_RECIPIENTS}"
            )
        }
    }
}