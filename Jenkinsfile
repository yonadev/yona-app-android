pipeline {
  agent {
    node {
      label 'android-f25'
    }
    
  }
  stages {
    stage('checkout') {
      steps {
        git(branch: 'Jenkinstest', credentialsId: 'git yona', url: 'https://github.com/yonadev/yona-app-android.git', changelog: true)
      }
    }
    stage('gradle step') {
      steps {
        sh '''(for i in
{1..30}

; do echo y; sleep 1; done) | /opt/android/android-sdk-linux/tools/android update sdk --no-ui --filter \\
tools,platform-tools,build-tools,\\
extra-android-m2repository,extra-android-support,extra-google-google_play_services,extra-google-m2repository'''
        script {
          echo sh(script: 'env|sort', returnStdout: true)
        }
        
      }
    }
    stage('Build') {
      steps {
        archiveArtifacts(artifacts: 'app/build/outputs/apk/*.apk', allowEmptyArchive: true)
      }
    }
  }
}