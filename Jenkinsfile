pipeline {
  agent any
  options {
    timeout(time: 30, unit: 'MINUTES')
  }
  tools {
    jdk 'jdk_8u144'
    nodejs 'node_8.4.0'
  }
  stages {
    stage('Prepare') {
      steps {
        sh 'chmod +x gradlew'
        sh './gradlew setupCiWorkspace clean'
      }
    }
    stage('Build') {
      steps {
        sh './gradlew build jar'
      }
    }
    stage('Run Server Test') {
      steps {
        dir('serverTest') {
          sh 'npm install'
          sh 'npm start'
        }
      }
      post {
        always {
          archiveArtifacts 'run/logs/fml-server-latest.log'
        }
      }
    }
    stage('Archive') {
      steps {
        archiveArtifacts 'build/libs/*.jar'
        fingerprint 'build/libs/*.jar'
      }
    }
  }
}
