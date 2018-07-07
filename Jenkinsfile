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
        
        sh 'echo \"y\" | ${ANDROID_HOME}/tools/android update sdk --no-ui --all --filter platform-tools,android-25,extra-android-m2repository'
        sh 'ls -l ${ANDROID_HOME}/tools/bin64'
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
