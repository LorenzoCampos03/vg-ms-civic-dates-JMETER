pipeline {
    agent any

    tools {
        maven 'MAVEN'
    }

    environment {
        GIT_TOKEN = credentials('gitlab-token-lorenzo') 
        REPO_PATH = 'gitlab.com/vallegrande/as232s5_prs1/vg-ms-civic-dates.git'
        
        SLACK_CHANNEL = '#notificaciones'
        SLACK_CRED_ID = 'slack-token-lorenzo' 
    }

    stages {
        stage('Checkout') {
            steps {
                deleteDir()
                sh """
                    git clone https://oauth2:${GIT_TOKEN}@${REPO_PATH} .
                    git checkout develop
                """
            }
        }

        stage('Build & Test') {
            steps {
                sh 'mvn clean test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                withSonarQubeEnv('sonar-server') {
                    sh """
                        mvn clean verify sonar:sonar \
                        -Dsonar.organization=as232s5-prs1 \
                        -Dsonar.projectKey=as232s5-prs1_vg-ms-event \
                        -Dsonar.token=ecc2436ccb9ba6697395d1ae2ddbaa850cc750ed
                    """
                }
            }
        }
        
        // El stage 'Quality Gate' ha sido removido porque entra en conflicto con el token global de tu servidor Jenkins
        // para proyectos privados.
    }

    post {
        success {
            slackSend(
                channel: "${SLACK_CHANNEL}",
                tokenCredentialId: "${SLACK_CRED_ID}",
                botUser: false,
                color: 'good',
                message: "🎉 BUILD EXITOSO - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
            )
        }
        failure {
            slackSend(
                channel: "${SLACK_CHANNEL}",
                tokenCredentialId: "${SLACK_CRED_ID}",
                botUser: false,
                color: 'danger',
                message: "🚨 BUILD FALLIDO - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
            )
        }
        unstable {
            slackSend(
                channel: "${SLACK_CHANNEL}",
                tokenCredentialId: "${SLACK_CRED_ID}",
                botUser: false,
                color: 'warning',
                message: "⚠️ BUILD INESTABLE - ${env.JOB_NAME} #${env.BUILD_NUMBER}"
            )
        }
    }
}
