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
        script {
          echo sh(script: 'env|sort', returnStdout: true)
        }
        
        sh 'ls -l /opt/android/android-sdk-linux'
        sh 'ls -l /opt/android/android-sdk-linux/tools'
        sh 'ls -l /opt/android/android-sdk-linux/tools/bin'
        sh 'ls -l /opt/android/android-sdk-linux/tools/bin/sdkmanager'
        sh '${ANDROID_HOME}/tools/bin/sdkmanager --licenses'
        sh './gradlew -Dorg.gradle.jvmargs=-Xmx1536m app:assembleDebug --scan'
      }
    }
    stage('Build') {
      steps {
        archiveArtifacts(artifacts: 'app/build/outputs/apk/*.apk', allowEmptyArchive: true)
      }
    }
  }
}
