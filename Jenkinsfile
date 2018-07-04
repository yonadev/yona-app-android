pipeline {
  agent {
    node {
      label 'android-f25'
    }
    
  }
  stages {
    stage('checkout') {
      steps {
        git(branch: 'Jenkinstest', credentialsId: 'git yona', url: 'https://Sivateja-CMI@bitbucket.org/yona-cmi/yona-app-android-cmi.git', changelog: true)
      }
    }
    stage('gradle step') {
      steps {
        sh '''cd $ANDROID_HOME
ls
cd platforms
ls'''
        script {
          echo sh(script: 'env|sort', returnStdout: true)
        }
        
        sh './gradlew app:assembleDebug'
      }
    }
    stage('Build') {
      steps {
        archiveArtifacts(artifacts: 'app/build/outputs/apk/*.apk', allowEmptyArchive: true)
      }
    }
  }
}